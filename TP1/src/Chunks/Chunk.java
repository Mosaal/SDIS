package Chunks;

public class Chunk {

	// Instance variables
	private final int fileID;
	private final int chunkNo;
	private final byte[] data;

	/**
	 * Creates a Chunk instance
	 * @param fileID the identifier of the file the chunk belongs to
	 * @param chunkNo the number of the chunk
	 */
	public Chunk(final int fileID, final int chunkNo, final byte[] data) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.data = data;
	}

	// Instance methods
	/** Returns the the identifier of the file the chunk belongs to */
	public final int getFileID() { return fileID; }

	/** Returns the number of the chunk */
	public final int getChunkNo() { return chunkNo; }

	/** Returns the data array */
	public final byte[] getData() { return data; }
}