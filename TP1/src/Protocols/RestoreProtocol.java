package Protocols;

import java.util.ArrayList;
import java.util.HashMap;

import Channels.MCChannel;
import Channels.MDRChannel;
import Utils.FileManager;
import Utils.Utils;

public class RestoreProtocol extends Protocol {

	// Instance variables
	private MDRChannel mdrChannel;
	private volatile boolean stop;
	private volatile boolean sendNext;
	private volatile ArrayList<String> currStoredFiles;
	private volatile HashMap<String, ArrayList<byte[]>> chunkConfirmations; // FileID -> ([i] = chunk where i = chunkNo)

	/**
	 * Creates a RestoreProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 * @param mdrChannel multicast data recovery channel this protocol subscribes to
	 */
	public RestoreProtocol(String proVer, int peerID, MCChannel mcChannel, MDRChannel mdrChannel) {
		super(proVer, peerID, mcChannel);

		this.stop = false;
		this.sendNext = true;
		this.mdrChannel = mdrChannel;
		currStoredFiles = new ArrayList<String>();
		chunkConfirmations = new HashMap<String, ArrayList<byte[]>>();

		processChunk.start();
		processGetchunk.start();
	}

	// Instance methods
	/** Returns the multicast data recovery channel */
	public MDRChannel getMDRChannel() { return mdrChannel; }

	/** Returns whether it should send the next message or not */
	public boolean getSendNext() { return sendNext; }

	/** Returns the hashmap of currently stored chunks of each file */
	public ArrayList<String> getCurrStoredChunks() { return currStoredFiles; }

	/** Returns the hashmap of confirmed chunks received */
	public HashMap<String, ArrayList<byte[]>> getChunkConfirmations() { return chunkConfirmations; }

	/**
	 * Restores a file to his original state
	 * @param fileName name of the file to be restored
	 */
	public String restoreFile(String fileName) {
		// Get the most up to date information
		currStoredFiles = FileManager.getFiles(peerID);

		// Get file ID
		String fileID = FileManager.getFileID(peerID).get(fileName);
		if (fileID == null) {
			System.out.println("Cannot order the restoration of a file this Peer hasn't backed up.");
			return Utils.ERROR_MESSAGE;
		}

		// Store fileID for later confirmation
		ArrayList<byte[]> temp = new ArrayList<byte[]>();
		temp.add(null);
		chunkConfirmations.put(fileID, temp);

		// Send a GETCHUNK message until it receives all of the chunks
		int chunkNo = 0;
		while (!stop) {
			if (sendNext) {
				// Send GETCHUNK request
				System.out.println("[ GETCHUNK ] Chunk#" + chunkNo);
				byte[] msg = Utils.createMessage(Utils.GETCHUNK_STRING, proVer, peerID, fileID, chunkNo, 0, new byte[] {});
				mcChannel.send(msg);

				// Set variables for next chunk
				chunkNo++;
				sendNext = false;
			}
		}

		// Reset variables for next restore request
		stop = false;
		sendNext = true;

		// Once we have all the chunks, restore the file
		if (!FileManager.restoreFile(fileName, chunkConfirmations.get(fileID))) {
			System.out.println("A problem ocurred trying to restore the file '" + fileName + "'.");
			return Utils.ERROR_MESSAGE;
		}

		return Utils.SUCCESS_MESSAGE;
	}

	/** Thread that is constantly processing CHUNK type messages */
	Thread processChunk = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				String str = null;
				do { str = mdrChannel.receive(); }
				while (str == null);

				// Split it
				String[] args = str.split(" ");

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					String fileID = args[3];
					int chunkNo = Integer.parseInt(args[4]);

					// Check if it is waiting for the given chunk
					if (chunkConfirmations.containsKey(fileID)) {
						if (chunkConfirmations.get(fileID).get(chunkNo) == null) {
							byte[] chunk = Utils.getChunkData(str);

							// Check the size of the chunk
							if (chunk.length < Utils.CHUNK_MAX_SIZE) {
								ArrayList<byte[]> temp = chunkConfirmations.get(fileID);
								temp.set(chunkNo, chunk);
								chunkConfirmations.put(fileID, temp);
								stop = true;
							} else {
								ArrayList<byte[]> temp = chunkConfirmations.get(fileID);
								temp.set(chunkNo, chunk);
								temp.add(null);
								chunkConfirmations.put(fileID, temp);
								sendNext = true;
							}
						}
					}
				}
			}
		}
	});

	/** Thread that is constantly processing GETCHUNK type messages */
	Thread processGetchunk = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				String str = null;
				do { str = mcChannel.receive(Utils.GETCHUNK_INT); }
				while (str == null);

				// Split it
				String[] args = str.split(" ");

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					// Check if it has the given chunk of the file
					String fileID = args[3];
					int chunkNo = Integer.parseInt(args[4]);
					
					// Get the most up to date info
					currStoredFiles = FileManager.getFiles(peerID);

					if (currStoredFiles.contains(fileID)) {
						byte[] chunk = FileManager.getChunk(peerID, fileID, chunkNo);
						// It does
						if (chunk != null) {
							// Wait for a random delay
							try { Thread.sleep(Utils.randomDelay()); }
							catch (InterruptedException e) { e.printStackTrace(); }

							// Then send reply
							System.out.println("[ CHUNK ] " + chunk.length + "B");
							byte[] reply = Utils.createMessage(Utils.CHUNK_STRING, proVer, peerID, fileID, chunkNo, 0, chunk);
							mdrChannel.send(reply);
						}
					}
				}
			}
		}
	});
}
