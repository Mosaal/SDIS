package Chunk;

public class Chunk {

	// Instance variables
	private final int chunkNo;
	private final String fileID;
	private final byte[] data;

	/**
	 * Creates a Chunk instance
	 * @param fileID the identifier of the file the chunk belongs to
	 * @param chunkNo the number of the chunk
	 * @param data the data to be stored
	 */
	public Chunk(final int chunkNo, final String fileID, final byte[] data) {
		this.chunkNo = chunkNo;
		this.fileID = fileID;
		this.data = data;
	}

	// Instance methods
	/** Returns the number of the chunk */
	public final int getChunkNo() { return chunkNo; }

	/** Returns the the identifier of the file the chunk belongs to */
	public final String getFileID() { return fileID; }

	/** Returns the data array */
	public final byte[] getData() { return data; }
}
