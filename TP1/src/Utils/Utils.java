package Utils;

import java.io.File;
import java.util.Random;

public class Utils {

	// Static variables
	public static final int STATE_INT = 1;
	public static final int RESTORE_INT = 2;
	public static final int BACKUP_INT = 3;
	public static final int DELETE_INT = 4;
	public static final int RECLAIM_INT = 5;
	public static final int BUFFER_MAX_SIZE = 64000;
	
	public static final String STATE_STRING = "STATE";
	public static final String RESTORE_STRING = "RESTORE";
	public static final String BACKUP_STRING = "BACKUP";
	public static final String DELETE_STRING = "DELETE";
	public static final String RECLAIM_STRING = "RECLAIM";
	
	public static final String PUTCHUNK_STRING = "PUTCHUNK";
	public static final String STORED_STRING = "STORED";
	public static final String GETCHUNK_STRING = "GETCHUNK";
	public static final String CHUNK_STRING = "CHUNK";
	public static final String REMOVED_STRING = "REMOVED";
	
	// Static methods
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
	 * Checks if a file with a given path exists
	 * @param path path of the file to be checked
	 */
	public static boolean fileExists(String path) { return new File(path).exists(); }
	
	/** Returns a random number between 0 and 400 */
	public static int randomDelay() { return new Random().nextInt(401); }
}
