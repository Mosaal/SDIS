package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

public class FileManager {

	// Static variables
	private static final String PEER = "Peer#";
	private static final String CHUNK = "Chunk#";
	private static final String STORAGE = "Storage";
	private static final String FILE_MAP = "FileIDMap.txt";
	private static final String STORED = "StoredChunks.txt";
	private static final String REPLICATION = "ChunkReplication.txt";

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
	 * |-FileIDMap [FILE]<br>
	 * |-StoredChunks [FILE]<br>
	 * |-ChunkInformation [FILE]<br>
	 * 
	 * @param peerID the name of the peer's parent directory
	 * @throws IOException 
	 */
	public static void createPeerDirectory(int peerID) throws IOException {
		File parent = new File(PEER + Integer.toString(peerID));

		// Create main directory if it doesn't exist
		if (!parent.exists() || !parent.isDirectory()) {
			parent.mkdir();

			// Create sub-directories and sub-files
			new File(parent.getName() + "/" + STORAGE).mkdir();
			new File(parent.getName() + "/" + STORED).createNewFile();
			new File(parent.getName() + "/" + FILE_MAP).createNewFile();
			new File(parent.getName() + "/" + REPLICATION).createNewFile();
		} else {
			// Check for sub-directories and sub-files
			File storage = new File(parent.getName() + "/" + STORAGE);
			if (!storage.exists() || !storage.isDirectory()) storage.mkdir();

			File repInfo = new File(parent.getName() + "/" + REPLICATION);
			if (!repInfo.exists()) repInfo.createNewFile();

			File fileMap = new File(parent.getName() + "/" + FILE_MAP);
			if (!fileMap.exists()) fileMap.createNewFile();

			File stored = new File(parent.getName() + "/" + STORED);
			if (!stored.exists()) stored.createNewFile();
		}
	}

	/**
	 * Stores the perceived replication degree for a given chunk
	 * @param peerID the name of the main directory
	 * @param fileID the ID of the file the chunk belongs to
	 * @param chunkNo the number of the chunk
	 * @param dRD the desired replication degree
	 * @param pRD the perceived replication degree
	 */
	public static void storePerceivedReplication(int peerID, String fileID, int chunkNo, int dRD, int pRD) {
		LinkedList<String> lines = new LinkedList<String>();
		File perFile = new File(PEER + Integer.toString(peerID) + "/" + REPLICATION);

		// Check if it exists
		try { if (!perFile.exists()) perFile.createNewFile(); }
		catch (IOException e) { return; }

		try {
			// Retrieve data already in file
			for (String line: Files.readAllLines(Paths.get(perFile.getPath()))) {
				if (line.isEmpty()) continue;

				if (!line.contains(fileID + ":" + chunkNo))
					lines.add(line);
			}

			// Add the new line
			lines.add(fileID + ":" + Integer.toString(chunkNo) + ":" + Integer.toString(dRD) + ":" + Integer.toString(pRD));

			// Write new data to the file
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(perFile.getPath(), false)));
			for (int i = 0; i < lines.size(); i++)
				out.println(lines.get(i));
			out.close();
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * Deletes the all of the lines referencing a given fileID
	 * @param peerID the name of the main directory
	 * @param fileID the ID of the file whose reference is going to get deleted
	 */
	public static void deletePerceivedReplication(int peerID, String fileID) {
		LinkedList<String> lines = new LinkedList<String>();
		String perPath = PEER + Integer.toString(peerID) + "/" + REPLICATION;
		File perFile = new File(perPath);

		// Check if it exists
		if (perFile.exists()) {
			try {
				// Retrieve data already in file
				for (String line: Files.readAllLines(Paths.get(perFile.getPath()))) {
					if (line.isEmpty()) continue;

					if (!line.contains(fileID))
						lines.add(line);
				}

				// Write new data to the file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(perFile.getPath(), false)));
				for (int i = 0; i < lines.size(); i++)
					out.println(lines.get(i));
				out.close();
			} catch (IOException e) {
				return;
			}
		} else {
			// Create it if it doesn't
			try { perFile.createNewFile(); }
			catch (IOException e) { return; }
		}
	}

	/**
	 * Returns the information in the replication file
	 * @param peerID the name of the main directory
	 */
	public static LinkedList<String> getPerceivedReplication(int peerID) {
		LinkedList<String> lines = new LinkedList<String>();
		File perFile = new File(PEER + Integer.toString(peerID) + "/" + REPLICATION);

		// Check if it exists
		if (perFile.exists()) {
			try {
				// Retrieve data in file
				for (String line: Files.readAllLines(Paths.get(perFile.getPath()))) {
					if (line.isEmpty()) continue;
					lines.add(line);
				}
			} catch (IOException e) {
				return lines;
			}
		} else {
			// Create it if it doesn't
			try { perFile.createNewFile(); }
			catch (IOException e) { return lines; }
		}

		return lines;
	}

	/**
	 * Returns all of the files currently in storage
	 * @param peerID ID of the directory to read from
	 */
	public static LinkedList<String> getFiles(int peerID) {
		LinkedList<String> files = new LinkedList<String>();
		File storedFile = new File(PEER + Integer.toString(peerID) + "/" + STORED);

		try {
			// Add to list if it isn't there already
			for (String line: Files.readAllLines(Paths.get(storedFile.getPath()))) {
				if (line.isEmpty()) continue;

				String fileID = line.split(":")[0];
				if (!files.contains(fileID))
					files.add(fileID);
			}
		} catch (IOException e) {
			return files;
		}

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
		File storedFile = new File(PEER + Integer.toString(peerID) + "/" + STORED);

		// Check if it exists
		if (dir.exists() && dir.isDirectory()) {
			try {
				// Delete references from Stored file
				LinkedList<String> temp = new LinkedList<String>();
				for (String line: Files.readAllLines(Paths.get(storedFile.getPath()))) {
					if (line.isEmpty()) continue;

					if (!line.contains(fileID))
						temp.add(line);
				}

				// Rewrite to stored file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(storedFile.getPath(), false)));
				for (int i = 0; i < temp.size(); i++)
					out.println(temp.get(i));
				out.close();
			} catch (IOException e) {
				return false;
			}

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
	 * Restores a file with its given chunks
	 * @param fileName name of the file to be restored
	 * @param chunks list of byte arrays with the data of the file to be restored
	 */
	public static boolean restoreFile(String fileName, LinkedList<byte[]> chunks) {
		try {
			// Write each byte array to the file
			FileOutputStream fos = new FileOutputStream(fileName);

			for (int i = 0; i < chunks.size(); i++)
				fos.write(chunks.get(i));

			fos.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * Gets the fileName to fileID hashmap of the backed up files
	 * @param peerID the name of the main directory
	 */
	public static HashMap<String, String> getFileID(int peerID) {
		HashMap<String, String> temp = new HashMap<String, String>();
		String fileMapPath = PEER + Integer.toString(peerID) + "/" + FILE_MAP;
		File fileMap = new File(fileMapPath);

		// Check if it exists
		if (fileMap.exists()) {
			try {
				// Add them to the hashmap: FileName -> FileID
				for (String line: Files.readAllLines(Paths.get(fileMap.getPath()))) {
					if (line.isEmpty()) continue;

					String[] info = line.split(":");
					temp.put(info[0], info[1]);
				}
			} catch (IOException e) {
				return temp;
			}
		} else {
			// Create it if it doesn't
			try { fileMap.createNewFile(); }
			catch (IOException e) { return temp; }
		}

		return temp;
	}

	/**
	 * Stored the fileName to fileID relation in the corresponding file
	 * @param peerID the name of the main directory
	 * @param fileName the name of the file
	 * @param fileID the ID generated for the file
	 */
	public static void storeFileID(int peerID, String fileName, String fileID) {
		String fileMapPath = PEER + Integer.toString(peerID) + "/" + FILE_MAP;
		File fileMap = new File(fileMapPath);

		try {
			// Check if it exists
			if (fileMap.exists()) {
				// Write data to the file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileMapPath, true)));
				out.println(fileName + ":" + fileID);
				out.close();
			} else {
				// Create it if it doesn't
				fileMap.createNewFile();

				// Write data to the file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileMapPath, true)));
				out.println(fileName + ":" + fileID);
				out.close();
			}
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * Delete the fileName to fileID relation in the corresponding file
	 * @param peerID the name of the main directory
	 * @param fileID the ID of the file whose relation is going to get deleted
	 */
	public static void deleteFileID(int peerID, String fileID) {
		String fileMapPath = PEER + Integer.toString(peerID) + "/" + FILE_MAP;
		File fileMap = new File(fileMapPath);

		try {
			// Check if it exists
			if (fileMap.exists()) {
				// Delete references from FileMap file
				LinkedList<String> temp = new LinkedList<String>();
				for (String line: Files.readAllLines(Paths.get(fileMap.getPath()))) {
					if (line.isEmpty()) continue;

					if (!line.contains(fileID))
						temp.add(line);
				}

				// Rewrite to stored file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileMap.getPath(), false)));
				for (int i = 0; i < temp.size(); i++)
					out.println(temp.get(i));
				out.close();
			} else {
				// Create it if it doesn't
				fileMap.createNewFile();
				return;
			}
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * Retrieves a given chunk of a file
	 * @param fileID ID of the file the chunk belongs to
	 * @param chunkNo the number of the chunk to be retrieved
	 */
	public static byte[] getChunk(int peerID, String fileID, int chunkNo) {
		// Get chunk file
		String parentPath = PEER + Integer.toString(peerID) + "/" + STORAGE + "/" + fileID;
		File file = new File(parentPath + "/" + CHUNK + Integer.toString(chunkNo));

		// Return null if chunk doesn't exist
		if (!file.exists())
			return null;

		try {
			// Create byte array and file reader
			byte[] data = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(parentPath + "/" + CHUNK + Integer.toString(chunkNo));

			// Read file data
			fis.read(data);
			fis.close();

			return data;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Stored a given chunk in its corresponding storage
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file the chunk belongs to
	 * @param chunkNo the number of the number to be stored
	 * @param data the data to be written to the file
	 */
	public static boolean storeChunk(int peerID, String fileID, int chunkNo, byte[] data) {
		String filePath = PEER + Integer.toString(peerID) + "/" + STORAGE + "/" + fileID;
		String storedPath = PEER + Integer.toString(peerID) + "/" + STORED;
		File dir = new File(filePath);

		// Check if file storage already exists
		if (dir.exists() && dir.isDirectory()) {
			// Add chunk file
			try {
				// Write to Stored file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(storedPath, true)));
				out.println(fileID + ":" + chunkNo);
				out.close();

				// Create chunk name
				String chunkName = filePath + "/" + CHUNK + Integer.toString(chunkNo);
				FileOutputStream fos = new FileOutputStream(chunkName);

				// Write data to file
				fos.write(data);
				fos.close();
			} catch (IOException e) {
				return false;
			}
		} else {
			// Create it if it doesn't
			dir.mkdirs();

			// Add chunk file
			try {
				// Write to Stored file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(storedPath, true)));
				out.println(fileID + ":" + chunkNo);
				out.close();

				// Create chunk name
				String chunkName = filePath + "/" + CHUNK + Integer.toString(chunkNo);
				FileOutputStream fos = new FileOutputStream(chunkName);

				// Write data to file
				fos.write(data);
				fos.close();
			} catch (IOException e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns a hashmap with the chunk's numbers of the stored files
	 * @param peerID name of the parent folder
	 */
	public static HashMap<String, LinkedList<Integer>> getStoredChunks(int peerID) {
		File storedFile = new File(PEER + Integer.toString(peerID) + "/" + STORED);
		HashMap<String, LinkedList<Integer>> storedChunks = new HashMap<String, LinkedList<Integer>>();

		try {
			for (String line: Files.readAllLines(Paths.get(storedFile.getPath()))) {
				// Check if it is empty
				if (line.isEmpty()) continue;

				// Split it
				String[] info = line.split(":");
				String fileID = info[0];

				// Add to hashmap
				if (storedChunks.containsKey(fileID)) {
					LinkedList<Integer> temp = storedChunks.get(fileID);
					temp.add(Integer.parseInt(info[1]));
					storedChunks.put(fileID, temp);
				} else {
					LinkedList<Integer> temp = new LinkedList<Integer>();
					temp.add(Integer.parseInt(info[1]));
					storedChunks.put(fileID, temp);
				}
			}
		} catch (IOException e) {
			return storedChunks;
		}

		return storedChunks;
	}
}
