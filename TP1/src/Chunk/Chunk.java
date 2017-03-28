package Chunk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import Utils.Utils;

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
	
	/**
	 * Split a given file into chunks
	 * @param filePath path of the file to be split
	 */
	public static ArrayList<Chunk> splitIntoChinks(String filePath) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		
		try {
			// Split file into array of bytes
			byte[] buf = new byte[Utils.BUFFER_MAX_SIZE];
			byte[] data = Files.readAllBytes(Paths.get(filePath));
			int temp = (int) Math.ceil((double) data.length / (double) Utils.BUFFER_MAX_SIZE);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
			
			for (int i = 0; i < temp; i++) {
				bis.read(buf);
				chunks.add(new Chunk(i, "lol", buf));
				buf = new byte[Utils.BUFFER_MAX_SIZE];
			}
			
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return chunks;
	}
}
