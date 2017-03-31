package Storage;

import java.io.File;

public class FileManager {

	// Static variables
	private static final String PEER = "Peer#";
	private static final String CHUNK = "Chunk#";
	private static final String STORAGE = "Storage";

	// Static methods
	/**
	 * Creates a peer's parent directory<br><br>
	 * 
	 * PeerID [DIR]<br>
	 * |-Storage [DIR]<br>
	 * |   |-FileID#0 [DIR]<br>
	 * |   |   |-Chunk#0 [FILE]<br>
	 * |   |   |-Chunk#1 [FILE]<br>
	 * |   |   |-...<br>
	 * |   |-FileID#1 [DIR]<br>
	 * |   |   |-Chunk#0 [FILE]<br>
	 * |   |   |-Chunk#1 [FILE]<br>
	 * |   |   |-...<br>
	 * |   |-...<br>
	 * |-<br>
	 * 
	 * @param peerID the name of the peer's parent directory
	 */
	public static void createPeerDirectory(int peerID) {
		File parent = new File(PEER + Integer.toString(peerID));
		
		// Create directory if it doesn't exist
		if (!parent.exists() || !parent.isDirectory()) {
			parent.mkdir();
			
			// Create Storage
			new File(parent.getName() + "/" + STORAGE).mkdir();
			
			// Create the rest
		} else {
			// Check for Storage
			File storage = new File(parent.getName() + "/" + STORAGE);
			if (!storage.exists() || !storage.isDirectory())
				storage.mkdir();
			
			// Check for the rest
		}
	}
	
	/**
	 * Returns all of the files currently in storage
	 * @param peerID ID of the directory to read from
	 */
	public static String[] getFiles(int peerID) {
		String[] files = null;
		File dir = new File(PEER + Integer.toString(peerID) + "/" + STORAGE);
		
		// Check if storage exists
		if (dir.exists() && dir.isDirectory())
			return dir.list();
		
		return files;
	}
		
	/**
	 * Deletes a directory with a given ID
	 * @param peerID name of the parent folder
	 * @param fileID ID of the directory to be deleted
	 */
	public static boolean deleteFile(int peerID, String fileID) {
		String parentPath = PEER + Integer.toString(peerID) + "/" + STORAGE + "/" + fileID;
		File dir = new File(parentPath);
		
		// Check if it exists
		if (dir.exists() && dir.isDirectory()) {
			// Delete all chunks
			String[] chunks = dir.list();
			for (int i = 0; i < chunks.length; i++)
				new File(parentPath + "/" + chunks[i]).delete();
			
			// Than delete folder
			dir.delete();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Stored a given chunk in its corresponding storage
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file the chunk belongs to
	 * @param chunkNo the number of the number to be stored
	 * @param data the data to be written to the file
	 */
	public static boolean storeChunk(int peerID, String fileID, int chunkNo, byte[] data) {
		String parentPath = PEER + Integer.toString(peerID) + "/" + STORAGE + "/" + fileID;
		File dir = new File(parentPath);
		
		// Check if file storage already exists
		if (dir.exists() && dir.isDirectory()) {
			// Add chunk file
			File chunk = new File(parentPath + "/" + CHUNK + Integer.toString(chunkNo));
			// TODO: Write data to the file
		} else {
			// Create it if it doesn't
			dir.mkdir();
			
			// Add chunk file
			File chunk = new File(parentPath + "/" + CHUNK + Integer.toString(chunkNo));
			// TODO: Write data to the file
		}
		
		return true;
	}
}
