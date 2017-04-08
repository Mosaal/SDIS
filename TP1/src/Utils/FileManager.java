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
import java.util.ArrayList;
import java.util.HashMap;

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
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file the chunk belongs to
	 * @param chunkNo the number of the chunk
	 * @param dRD the desired replication degree
	 * @param pRD the perceived replication degree
	 */
	public static void storePerceivedReplication(int peerID, String fileID, int chunkNo, int dRD, int pRD) {
		ArrayList<String> lines = new ArrayList<String>();
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
	 * Updates the perceived replication degree of a given chunk
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file the chunk belongs to
	 * @param chunkNo the number of the chunk
	 */
	public static int[] updatePerceivedReplication(int peerID, String fileID, int chunkNo) {
		String perPath = PEER + Integer.toString(peerID) + "/" + REPLICATION;
		File perFile = new File(perPath);

		// Check if it exists
		if (perFile.exists()) {
			try {
				int[] ret = new int[] { -1, 0 };
				ArrayList<String> lines = new ArrayList<String>();

				// Retrieve data already in file
				for (String line: Files.readAllLines(Paths.get(perFile.getPath()))) {
					if (line.isEmpty()) continue;

					if (!line.contains(fileID + ":" + chunkNo)) {
						lines.add(line);
					} else {
						String[] res = line.split(":");
						int desRD = Integer.parseInt(res[2]);
						int newPerRD = Integer.parseInt(res[3]);
						newPerRD--;

						// Return 0 if desired went bellow perceived, 1 if it didn't
						if (newPerRD < desRD)
							ret = new int[] { 0, desRD };
						else
							ret = new int[] { 1, desRD };

						// Check if it's greater than 0
						if (newPerRD > 0) {
							lines.add(res[0] + ":" + res[1] + ":" + res[2] + ":" + Integer.toString(newPerRD));
						} else {
							// Check it it has the chunk
							if (getChunk(peerID, fileID, chunkNo) != null)
								lines.add(res[0] + ":" + res[1] + ":" + res[2] + ":1");
						}
					}
				}

				// Write new data to the file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(perFile.getPath(), false)));
				for (int i = 0; i < lines.size(); i++)
					out.println(lines.get(i));
				out.close();

				return ret;
			} catch (IOException e) {
				return new int[] { -1, 0 };
			}
		} else {
			// Create it if it doesn't
			try { perFile.createNewFile(); }
			catch (IOException e) { return new int[] { -1, 0 }; }
		}

		return new int[] { -1, 0 };
	}

	/**
	 * Deletes the all of the lines referencing a given fileID
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file whose reference is going to get deleted
	 */
	public static void deletePerceivedReplication(int peerID, String fileID) {
		ArrayList<String> lines = new ArrayList<String>();
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
	 * @param peerID the name of the peer's parent directory
	 */
	public static ArrayList<String> getPerceivedReplication(int peerID) {
		ArrayList<String> lines = new ArrayList<String>();
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
	 * @param peerID the name of the peer's parent directory
	 */
	public static ArrayList<String> getStoredFiles(int peerID) {
		ArrayList<String> files = new ArrayList<String>();
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
	 * @param peerID the name of the peer's parent directory
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
				ArrayList<String> temp = new ArrayList<String>();
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
	public static boolean restoreFile(String fileName, ArrayList<byte[]> chunks) {
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
	 * @param peerID the name of the peer's parent directory
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
	 * @param peerID the name of the peer's parent directory
	 * @param fileName the name of the file
	 * @param fileID the ID generated for the file
	 */
	public static void storeFileID(int peerID, String fileName, String fileID) {
		String fileMapPath = PEER + Integer.toString(peerID) + "/" + FILE_MAP;
		File fileMap = new File(fileMapPath);

		try {
			// Check if it exists
			if (fileMap.exists()) {
				// Check if it is in file already
				for (String line: Files.readAllLines(Paths.get(fileMap.getPath()))) {
					if (line.isEmpty()) continue;

					// Don't add it if it is
					if (line.equals(fileName + ":" + fileID))
						return;
				}

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
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file whose relation is going to get deleted
	 */
	public static void deleteFileID(int peerID, String fileID) {
		String fileMapPath = PEER + Integer.toString(peerID) + "/" + FILE_MAP;
		File fileMap = new File(fileMapPath);

		try {
			// Check if it exists
			if (fileMap.exists()) {
				// Delete references from FileMap file
				ArrayList<String> temp = new ArrayList<String>();
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
	 * Deletes a specified chunk
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file the chunk belongs
	 * @param chunkNo the number of the chunk
	 */
	public static boolean deleteChunk(int peerID, String fileID, int chunkNo) {
		String filePath = PEER + Integer.toString(peerID) + "/" + STORAGE + "/" + fileID;
		String chunkPath = filePath + "/" + CHUNK + Integer.toString(chunkNo);
		File fileDir = new File(filePath);

		// Check if the file exists
		if (fileDir.exists() && fileDir.isDirectory()) {
			File chunk = new File(chunkPath);

			// Check if the chunk exists
			if (chunk.exists())
				chunk.delete();

			// Update stored chunks file
			updateStoredChunks(peerID, fileID, chunkNo);

			// Check if it was the last chunk
			if (fileDir.list().length == 0)
				fileDir.delete();

			return true;
		}

		return false;
	}

	/**
	 * Returns a hashmap with the chunk's numbers of the stored files
	 * @param peerID the name of the peer's parent directory
	 */
	public static HashMap<String, ArrayList<Integer>> getStoredChunks(int peerID) {
		File storedFile = new File(PEER + Integer.toString(peerID) + "/" + STORED);
		HashMap<String, ArrayList<Integer>> storedChunks = new HashMap<String, ArrayList<Integer>>();

		try {
			for (String line: Files.readAllLines(Paths.get(storedFile.getPath()))) {
				// Check if it is empty
				if (line.isEmpty()) continue;

				// Split it
				String[] info = line.split(":");
				String fileID = info[0];

				// Add to hashmap
				if (storedChunks.containsKey(fileID)) {
					ArrayList<Integer> temp = storedChunks.get(fileID);
					temp.add(Integer.parseInt(info[1]));
					storedChunks.put(fileID, temp);
				} else {
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.add(Integer.parseInt(info[1]));
					storedChunks.put(fileID, temp);
				}
			}
		} catch (IOException e) {
			return storedChunks;
		}

		return storedChunks;
	}

	/**
	 * Updates the information in the stored chunks file
	 * @param peerID the name of the peer's parent directory
	 * @param fileID the ID of the file the chunk belongs to
	 * @param chunkNo the number of the chunk
	 */
	public static boolean updateStoredChunks(int peerID, String fileID, int chunkNo) {
		String storedPath = PEER + Integer.toString(peerID) + "/" + STORED;
		File storedFile = new File(storedPath);

		// Check if it exists
		if (storedFile.exists()) {
			try {
				ArrayList<String> temp = new ArrayList<String>();

				// Delete reference from file
				for (String line: Files.readAllLines(Paths.get(storedFile.getPath()))) {
					if (line.isEmpty()) continue;

					if (!line.equals(fileID + ":" + chunkNo))
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
		} else {
			// Create it if it doesn't
			try { storedFile.createNewFile(); }
			catch (IOException e) { return false; }
			return true;
		}

		return false;
	}
}
