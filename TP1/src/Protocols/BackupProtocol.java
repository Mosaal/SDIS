package Protocols;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import Channels.MCChannel;
import Channels.MDBChannel;
import Utils.*;

public class BackupProtocol extends Protocol {

	// Instance variables
	private MDBChannel mdbChannel;
	private volatile ArrayList<String> toBeIgnored;
	private volatile HashMap<String, Integer> desiredRepDegrees;
	private volatile HashMap<String, ArrayList<Integer>> otherConfirmations; // FileID -> ([i] = RepDeg, where i = ChunkNo)
	private volatile HashMap<String, ArrayList<Integer>> storedConfirmations; // FileID -> ([i] = RepDeg, where i = ChunkNo)
	private volatile HashMap<String, ArrayList<Integer>> putChunkConfirmations; // FileID -> (List of chunk numbers)

	/**
	 * Creates a BackupProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 * @param mdbChannel multicast data backup channel this protocol subscribes to
	 */
	public BackupProtocol(String proVer, int peerID, MCChannel mcChannel, MDBChannel mdbChannel) {
		super(proVer, peerID, mcChannel);

		this.mdbChannel = mdbChannel;
		toBeIgnored = new ArrayList<String>();
		desiredRepDegrees = new HashMap<String, Integer>();
		putChunkConfirmations = FileManager.getStoredChunks(peerID);
		otherConfirmations = new HashMap<String, ArrayList<Integer>>();
		storedConfirmations = new HashMap<String, ArrayList<Integer>>();

		processStored.start();
		processPutchunk.start();
	}

	// Instance methods
	/** Returns the multicast data backup channel */
	public MDBChannel getMDBChannel() { return mdbChannel; }

	/** Returns the hashmap of the desired replication degrees */
	public HashMap<String, Integer> getDesiredRepDegrees() { return desiredRepDegrees; }

	/** Returns the hashmap of the replication degree for the stored chunks of each file (not belonging to this Peer) */
	public HashMap<String, ArrayList<Integer>> getOtherConfirmations() { return otherConfirmations; }

	/** Returns the hashmap of the replication degree for the stored chunks of each file (belonging to this Peer) */
	public HashMap<String, ArrayList<Integer>> getStoredConfirmations() { return storedConfirmations; }

	/** Returns the hashmap for the backed up chunks of each file */
	public HashMap<String, ArrayList<Integer>> getPutChunkConfirmations() { return putChunkConfirmations; }

	/**
	 * Backup a given file
	 * @param filePath path of the file to be backed up
	 * @param repDeg desired replication degree
	 */
	public String backupFile(String filePath, int repDeg) {
		// Get file ID
		File file = new File(filePath);
		String fileID = Utils.encryptString(file.getName() + file.length() + file.lastModified());
		FileManager.storeFileID(peerID, new File(filePath).getName(), fileID);

		// Store desired replication degree
		desiredRepDegrees.put(fileID, repDeg);

		// Split file into chunks
		ArrayList<byte[]> chunks = Utils.splitIntoChunks(filePath);

		// Store sent chunks in a hashmap for later confirmation
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < chunks.size(); i++) temp.add(0);
		storedConfirmations.put(fileID, temp);

		// Send them one by one
		int retries = 1;
		int waitInterval = Utils.INITIAL_WAIT_INTERVAL;
		for (int i = 0; i < chunks.size(); i++) {
			// Info print
			if (retries == 1)
				System.out.println("[ PUTCHUNK ] " + chunks.get(i).length + "B");

			// Create PUTCHUNK message and send it
			byte[] msg = Utils.createMessage(Utils.PUTCHUNK_STRING, proVer, peerID, fileID, i, repDeg, chunks.get(i));
			mdbChannel.send(msg);

			// Wait for a few seconds
			try { Thread.sleep(waitInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }

			// Check if current replication degree matches the desired one
			if (storedConfirmations.get(fileID).get(i) < repDeg && retries < Utils.MAX_RETRIES) {
				i--;
				retries++;
				waitInterval *= 2;
			} else {
				retries = 1;
				waitInterval = Utils.INITIAL_WAIT_INTERVAL;
			}
		}

		return Utils.SUCCESS_MESSAGE;
	}

	/**
	 * Backup a specified chunk of a file
	 * @param filePath path of the file the chunk belongs to
	 * @param chunkNo the number of the chunk
	 */
	public static boolean backupChunk(String filePath, int chunkNo) {
		

		return true;
	}

	/** Thread that is constantly processing STORED type messages */
	Thread processStored = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				String str = null;
				do { str = mcChannel.receive(Utils.STORED_INT); }
				while (str == null);

				// Split it
				String[] args = str.split(" ");

				// Parse fileID and chunkNo
				String fileID = args[3];
				int chunkNo = Integer.parseInt(args[4]);

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					// Check if it contains the specified file
					if (storedConfirmations.containsKey(fileID)) {
						// Add to confirmed stored list
						ArrayList<Integer> temp = storedConfirmations.get(fileID);
						temp.set(chunkNo, temp.get(chunkNo).intValue() + 1);
						storedConfirmations.put(fileID, temp);
					}
				}

				// Store perceived replication degree
				if (otherConfirmations.containsKey(fileID)) {
					// Add to confirmed other list
					ArrayList<Integer> temp = otherConfirmations.get(fileID);
					if (chunkNo >= temp.size()) temp.add(1);
					else temp.set(chunkNo, temp.get(chunkNo).intValue() + 1);
					otherConfirmations.put(fileID, temp);
				} else {
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.add(1);
					otherConfirmations.put(fileID, temp);
				}

				// Write to file
				FileManager.storePerceivedReplication(peerID, fileID, chunkNo, desiredRepDegrees.get(fileID).intValue(), otherConfirmations.get(fileID).get(chunkNo));
			}
		}
	});

	/** Thread that is constantly processing PUTCHUNK type messages */
	Thread processPutchunk = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				String str = null;
				do { str = mdbChannel.receive(); }
				while (str == null);

				// Split it
				String[] args = str.split(" ");

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					// Parse fileID, chunkNo and repDeg
					String fileID = args[3];
					int chunkNo = Integer.parseInt(args[4]);
					int repDeg = Integer.parseInt(args[5]);

					// Check if it must be ignored
					if (toBeIgnored.contains(fileID))
						continue;
					
					// Store desired replication degree
					desiredRepDegrees.put(fileID, repDeg);

					// Check if this chunk has already been stored
					if (putChunkConfirmations.containsKey(fileID)) {
						if (putChunkConfirmations.get(fileID).contains(chunkNo)) {
							continue;
						} else {
							// Write to disk and send confirmation
							byte[] chunk = Utils.getChunkData(str);

							if (FileManager.storeChunk(peerID, fileID, chunkNo, chunk)) {
								// Add to confirmed chunks list
								ArrayList<Integer> temp = putChunkConfirmations.get(fileID);
								temp.add(chunkNo);
								putChunkConfirmations.put(fileID, temp);

								// Create STORED message and send it
								System.out.println("[ STORED ] " + chunk.length + "B");
								byte[] reply = Utils.createMessage(Utils.STORED_STRING, proVer, peerID, fileID, chunkNo, repDeg, new byte[] {});
								mcChannel.send(reply);
							}
						}
					} else {
						// Write to disk and send confirmation
						byte[] chunk = Utils.getChunkData(str);

						if (FileManager.storeChunk(peerID, fileID, chunkNo, chunk)) {
							// Add to confirmed chunks list
							ArrayList<Integer> temp = new ArrayList<Integer>();
							temp.add(chunkNo);
							putChunkConfirmations.put(fileID, temp);

							// Create STORED message and send it
							System.out.println("[ STORED ] " + chunk.length + "B");
							byte[] reply = Utils.createMessage(Utils.STORED_STRING, proVer, peerID, fileID, chunkNo, repDeg, new byte[] {});
							mcChannel.send(reply);
						}
					}
				}
			}
		}
	});
}
