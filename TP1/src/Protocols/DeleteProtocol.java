package Protocols;

import java.io.File;
import java.util.LinkedList;

import Channels.MCChannel;
import Storage.FileManager;
import Utils.Utils;

public class DeleteProtocol extends Protocol {

	// Instance variables
	private volatile LinkedList<String> currStoredFiles;

	/**
	 * Creates a DeleteProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public DeleteProtocol(String proVer, int peerID, LinkedList<String> currStoredFiles, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
		this.currStoredFiles = currStoredFiles;
		processDelete.start();
	}

	// Instance methods
	/** Returns the hashmap of currently stored chunks of each file */
	public LinkedList<String> getCurrStoredChunks() { return currStoredFiles; }

	/**
	 * Deletes all of the chunks of a given file
	 * @param fileName name of file to be deleted
	 */
	public boolean deleteFile(String fileName) {
		// Get file ID
		File file = new File(fileName);
		String fileID = Utils.encryptString(file.getName() + file.length() + file.lastModified());
		// TODO: not supposed to generate ID again
		// Must store them in a file in relation to the file name

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

		return true;
	}

	/** Thread that is constantly processing DELETE type messages */
	Thread processDelete = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				String str = null;
				do { str = mcChannel.receive(Utils.DELETE_INT); }
				while (str == null);

				// Split it
				String[] args = str.split(" ");

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					// Get file ID and update files list
					String fileID = args[3];
					currStoredFiles = FileManager.getFiles(peerID);

					// Check if it has any chunk of this file
					if (currStoredFiles.contains(fileID)) {
						if (FileManager.deleteFile(peerID, fileID))
							currStoredFiles.remove(fileID);
					}
				}
			}
		}
	});
}
