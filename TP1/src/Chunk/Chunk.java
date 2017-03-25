package Chunk;

public class Chunk {

	private final int chunkNo;
	private final String fileID;
	private final byte[] data;

	public Chunk(final int chunkNo, final String fileID, final byte[] data) {
		this.chunkNo = chunkNo;
		this.fileID = fileID;
		this.data = data;
	}

	public final int getChunkNo() { return chunkNo; }

	public final String getFileID() { return fileID; }

	public final byte[] getData() { return data; }
}
