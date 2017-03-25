package Chunk;

public class Chunk
{
	private final int chunkNo;

	private final String fileID;

	private final byte[] data;


	public Chunk(int paramInt, String paramString, byte[] paramArrayOfByte)
	{
		chunkNo = paramInt;
		fileID = paramString;
		data = paramArrayOfByte;
	}

	public final int getChunkNo()
	{
		return chunkNo;
	}

	public final String getFileID() { return fileID; }

	public final byte[] getData() {
		return data;
	}
}
