package Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
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
	
	public static final int BUFFER_MAX_SIZE = 64000;
	
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
	 * Split a given file into chunks
	 * @param filePath path of the file to be split
	 */
	public static LinkedList<byte[]> splitIntoChinks(String filePath) {
		LinkedList<byte[]> chunks = new LinkedList<byte[]>();
		
		try {
			byte[] buf = null;
			long fileSize = new File(filePath).length();
			int totalChunks = (int) Math.ceil((double) fileSize / (double) BUFFER_MAX_SIZE);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
			
			long holder = fileSize;
			for (int i = 0; i < totalChunks; i++) {
				if (holder < BUFFER_MAX_SIZE) {
					buf = new byte[(int) holder];
				} else {
					holder -= BUFFER_MAX_SIZE;
					buf = new byte[BUFFER_MAX_SIZE];
				}
				
				bis.read(buf);
				chunks.add(buf);
			}
			
			if (fileSize % BUFFER_MAX_SIZE == 0)
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
			header += chunkNo + " " + repDeg + " "; // CRLFx2 and Body
		} else if (type.equals(CHUNK_STRING)) {
			header += chunkNo + " "; // CRLFx2 and Body
		} else if (type.equals(DELETE_STRING)) {
			header += ""; // CRLFx2
		} else if (type.equals(STORED_STRING) || type.equals(GETCHUNK_STRING) || type.equals(REMOVED_STRING)) {
			header += chunkNo + " "; // CRLFx2
		}
		
		byte[] temp = header.getBytes();
		byte[] sep = new byte[] { 0xD, 0xA };
		byte[] message = new byte[temp.length + sep.length + body.length];
		
		System.arraycopy(temp, 0, message, 0, temp.length);
		System.arraycopy(sep, 0, message, temp.length, sep.length);
		System.arraycopy(body, 0, message, temp.length + sep.length, body.length);
		
		return message;
	}
}
