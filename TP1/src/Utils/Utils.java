package Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

public class Utils {

	// Static variables
	public static final int STATE_INT = 1;
	public static final int RESTORE_INT = 2;
	public static final int BACKUP_INT = 3;
	public static final int DELETE_INT = 4;
	public static final int RECLAIM_INT = 5;

	public static final String STATE_STRING = "STATE";
	public static final String RESTORE_STRING = "RESTORE";
	public static final String BACKUP_STRING = "BACKUP";
	public static final String DELETE_STRING = "DELETE";
	public static final String RECLAIM_STRING = "RECLAIM";

	public static final int PUTCHUNK_INT = 0;
	public static final int STORED_INT = 1;
	public static final int GETCHUNK_INT = 2;
	public static final int CHUNK_INT = 3;
	public static final int REMOVED_INT = 5;

	public static final String PUTCHUNK_STRING = "PUTCHUNK";
	public static final String STORED_STRING = "STORED";
	public static final String GETCHUNK_STRING = "GETCHUNK";
	public static final String CHUNK_STRING = "CHUNK";
	public static final String REMOVED_STRING = "REMOVED";

	public static final int MAX_RETRIES = 5;
	public static final int CHUNK_MAX_SIZE = 64000;
	public static final int BUFFER_MAX_SIZE = 65000;
	public static final int INITIAL_WAIT_INTERVAL = 1000;

	public static final String END_MESSAGE = "END";
	public static final String SUCCESS_MESSAGE = "OK";
	public static final String ERROR_MESSAGE = "ERROR";

	// Static methods
	/** Returns a random number between 0 and 400 */
	public static int randomDelay() { return new Random().nextInt(400); }

	/**
	 * Checks if a file with a given path exists
	 * @param path path of the file to be checked
	 */
	public static boolean fileExists(String path) { return new File(path).exists(); }

	/**
	 * Checks if a given string is an integer
	 * @param str string to be checked
	 */
	public static boolean isStringInteger(String str) {
		try { Integer.parseInt(str); }
		catch (NumberFormatException e) { return false; }
		return true;
	}

	/**
	 * Checks if a given string is a double
	 * @param str string to be checked
	 */
	public static boolean isStringDouble(String str) {
		try { Double.parseDouble(str); }
		catch (NumberFormatException e) { return false; }
		return true;
	}

	/**
	 * Applies SHA256, a cryptographic hash function, to some bit string
	 * @param someBitString the bit string to be encrypted
	 */
	public static String encryptString(String someBitString) {
		String ret = null;

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(someBitString.getBytes());
			byte[] bytes = digest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < bytes.length; i++)
				hexString.append(Integer.toHexString(0xFF & bytes[i]));

			ret = hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Returns the part of the message that is the chunk's data
	 * @param msg the message that contains chunk data
	 */
	public static byte[] getChunkData(String msg) {
		int index = 0;
		byte[] data = msg.getBytes();

		// Look for [ 0xD, 0xA, 0xD, 0xA ] in the byte sequence
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0xD && data[i + 1] == 0xA && data[i + 2] == 0xD && data[i + 3] == 0xA) {
				index = i + 4;
				break;
			}
		}

		// When found copy everything after it
		byte[] chunkData = new byte[data.length - index];
		System.arraycopy(data, index, chunkData, 0, chunkData.length);

		return chunkData;
	}

	/**
	 * Split a given file into chunks
	 * @param filePath path of the file to be split
	 */
	public static ArrayList<byte[]> splitIntoChunks(String filePath) {
		ArrayList<byte[]> chunks = new ArrayList<byte[]>();

		try {
			byte[] buf = null;
			long fileSize = new File(filePath).length();
			int totalChunks = (int) Math.ceil((double) fileSize / (double) CHUNK_MAX_SIZE);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));

			long holder = fileSize;
			for (int i = 0; i < totalChunks; i++) {
				if (holder < CHUNK_MAX_SIZE) {
					buf = new byte[(int) holder];
				} else {
					holder -= CHUNK_MAX_SIZE;
					buf = new byte[CHUNK_MAX_SIZE];
				}

				bis.read(buf);
				chunks.add(buf);
			}

			if (fileSize % CHUNK_MAX_SIZE == 0)
				chunks.add(new byte[] {});

			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chunks;
	}

	/**
	 * Creates a specific type of message
	 * @param type the type of message to be created
	 * @param version the version of the protocol in use
	 * @param senderID the ID of the Peer
	 * @param fileID the ID of the file
	 * @param chunkNo the number of the chunk
	 * @param repDeg the replication degree
	 * @param body the body of the message (if it has any)
	 */
	public static byte[] createMessage(String type, String version, int senderID, String fileID, int chunkNo, int repDeg, byte[] body) {
		String header = type + " " + version + " " + senderID + " " + fileID + " ";

		if (type.equals(PUTCHUNK_STRING)) {
			header += chunkNo + " " + repDeg + " ";
		} else if (type.equals(CHUNK_STRING) || type.equals(STORED_STRING) || type.equals(GETCHUNK_STRING) || type.equals(REMOVED_STRING)) {
			header += chunkNo + " ";
		}

		byte[] temp = header.getBytes();
		byte[] sep = new byte[] { 0xD, 0xA, 0xD, 0xA };
		byte[] message = new byte[temp.length + sep.length + body.length];

		System.arraycopy(temp, 0, message, 0, temp.length);
		System.arraycopy(sep, 0, message, temp.length, sep.length);
		System.arraycopy(body, 0, message, temp.length + sep.length, body.length);

		return message;
	}
}
