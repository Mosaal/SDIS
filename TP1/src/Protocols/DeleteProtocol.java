package Protocols;

import java.util.ArrayList;

import Channels.MCChannel;
import Utils.FileManager;
import Utils.Utils;

public class DeleteProtocol extends Protocol {

	// Instance variables
	private volatile ArrayList<String> currStoredFiles;

	/**
	 * Creates a DeleteProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public DeleteProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
		currStoredFiles = new ArrayList<String>();
		processDelete.start();
	}

	// Instance methods
	/** Returns the hashmap of currently stored chunks of each file */
	public ArrayList<String> getCurrStoredChunks() { return currStoredFiles; }

	/**
	 * Deletes all of the chunks of a given file
	 * @param fileName name of file to be deleted
	 */
	public String deleteFile(String fileName) {
		// Get file ID
		String fileID = FileManager.getFileID(peerID).get(fileName);
		if (fileID == null) {
			System.out.println("Cannot order the deletion of a file this Peer hasn't backed up.");
			return Utils.ERROR_MESSAGE;
		} else {
			FileManager.deleteFileID(peerID, fileID);
			FileManager.deletePerceivedReplication(peerID, fileID);
		}

		// Check if this Peer doesn't have that file himself
		FileManager.deleteFile(peerID, fileID);

		// Send a DELETE type message 5 times
		System.out.println("[ DELETE ] " + fileName);
		for (int i = 0; i < Utils.MAX_RETRIES; i++) {
			// Create message and send it
			byte[] msg = Utils.createMessage(Utils.DELETE_STRING, proVer, peerID, fileID, i, 0, new byte[] {});
			mcChannel.send(msg);

			// Introduce a random delay in order not to flood the other Peers
			try { Thread.sleep(Utils.randomDelay()); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}

		return Utils.SUCCESS_MESSAGE;
	}

	/** Thread that is constantly processing DELETE type messages */
	Thread processDelete = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				byte[] data = null;
				do { data = mcChannel.receive(Utils.DELETE_INT); }
				while (data == null);

				// Make it a string
				String str = new String(data, 0, data.length);

				// Split it
				String[] args = str.split(" ");

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					// Get file ID and update files list
					String fileID = args[3];
					currStoredFiles = FileManager.getStoredFiles(peerID);

					// Check if it has any chunk of this file
					if (currStoredFiles.contains(fileID)) {
						if (FileManager.deleteFile(peerID, fileID)) {
							FileManager.deletePerceivedReplication(peerID, fileID);
							currStoredFiles.remove(fileID);
						}
					}
				}
			}
		}
	});
}
