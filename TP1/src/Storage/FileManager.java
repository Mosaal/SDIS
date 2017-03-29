package Storage;

import java.io.File;
import java.io.IOException;

public class FileManager {

	// Instance variables
	private int peerID;
	
	/**
	 * Creates a FileManager instance
	 * @param peerID unique identifier of the Peer
	 */
	public FileManager(int peerID) {
		this.peerID = peerID;
		
		// Create directory if it doesn't exist
		File parent = new File(Integer.toString(peerID));
		if (!parent.exists()) {
			parent.mkdir();
			
			// Create subdirectories and subfolders
			new File(parent.getName() + "/Chunk Storage").mkdir();
			try {
				new File(parent.getName() + "/Metadata.txt").createNewFile();
				new File(parent.getName() + "/ChunksReplication.txt").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Instance methods
	/** Returns the ID */
	public int getPeerID() { return peerID; }
}
