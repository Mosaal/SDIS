package Protocols;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import Channels.MCChannel;
import Channels.MDBChannel;
import Utils.Utils;

public class BackupProtocol extends Protocol {

	// Instance variables
	private MDBChannel mdbChannel;
	private HashMap<String, int[]> storedConfirmations; // FileID -> ([i] = RepDeg, where i = ChunkNo)

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
		storedConfirmations = new HashMap<String, int[]>();

		processStored.start();
		processPutchunk.start();
	}

	// Instance methods
	/** Returns the multicast data backup channel */
	public MDBChannel getMDBChannel() { return mdbChannel; }

	/** Returns the hashmap of the replication degree for the stored chunks of each file */
	public HashMap<String, int[]> getStoredConfirmations() { return storedConfirmations; }

	/**
	 * Backup a given file
	 * @param filePath path of the file to be backed up
	 * @param repDeg desired replication degree
	 */
	public boolean backupFile(String filePath, int repDeg) {
		// Get file ID
		File file = new File(filePath);
		String fileID = Utils.encryptString(file.getName() + file.length() + file.lastModified());

		// Split file into chunks
		LinkedList<byte[]> chunks = Utils.splitIntoChinks(filePath);

		// Store sent chunks in a hashmap for later confirmation
		int[] temp = new int[chunks.size()];
		Arrays.fill(temp, 0);
		storedConfirmations.put(fileID, temp);

		// Send them one by one
		int retries = 1;
		int waitInterval = Utils.INITIAL_WAIT_INTERVAL;
		for (int i = 0; i < chunks.size(); i++) {
			// Create PUTCHUNK message and send it
			byte[] msg = Utils.createMessage(Utils.PUTCHUNK_STRING, proVer, peerID, fileID, i, repDeg, chunks.get(i));
			mdbChannel.send(msg);

			// Wait for a few seconds
			System.out.println("[ CHUNK #" + i + " ] " + chunks.get(i).length + " bytes sent");
			try { Thread.sleep(waitInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }

			// Check if current replication degree matches the desired one
			System.out.println(Arrays.toString(storedConfirmations.get(fileID)));
			if (storedConfirmations.get(fileID)[i] < repDeg && retries < Utils.MAX_RETRIES) {
				i--;
				retries++;
				waitInterval *= 2;
			} else {
				retries = 1;
				waitInterval = Utils.INITIAL_WAIT_INTERVAL;
			}
		}

		return true;
	}

	/** Thread that is constantly processing STORED type messages */
	Thread processStored = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// TODO: once it writes to the disk things will be handled differently
				// Receive data if its there to be received
				String str = null;
				do { str = mcChannel.receive(Utils.STORED_INT); }
				while (str == null);

				// Split it
				String[] args = str.split(" ");

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					// Parse fileID and chunkNo
					String fileID = args[3];
					int chunkNo = Integer.parseInt(args[4]);

					// Check if it contains the specified file
					if (storedConfirmations.containsKey(fileID)) {
						int[] temp = storedConfirmations.get(fileID);
						temp[chunkNo]++;
						storedConfirmations.put(fileID, temp);
					}
				}
			}
		}
	});

	/** Thread that is constantly processing PUTCHUNK type messages */
	Thread processPutchunk = new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO: once it writes to the disk things will be handled differently
			// Store what chunks from what files have been stored - FileID -> LinkedList(int) where int = chunkNo
			HashMap<String, LinkedList<Integer>> putChunkConfirmations = new HashMap<String, LinkedList<Integer>>();

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

					// Check if this chunk has already been stored
					if (putChunkConfirmations.containsKey(fileID)) {
						if (putChunkConfirmations.get(fileID).contains(chunkNo)) {
							continue;
						} else {
							LinkedList<Integer> temp = putChunkConfirmations.get(fileID);
							temp.add(chunkNo);
							putChunkConfirmations.put(fileID, temp);
						}
					} else {
						LinkedList<Integer> temp = new LinkedList<Integer>();
						temp.add(chunkNo);
						putChunkConfirmations.put(fileID, temp);
					}

					// Create STORED message and send it
					byte[] reply = Utils.createMessage(Utils.STORED_STRING, proVer, peerID, fileID, chunkNo, repDeg, new byte[] {});
					mcChannel.send(reply);
				}
			}
		}
	});
}
