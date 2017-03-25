package Utils;

import java.io.File;

public class Utils {

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
	 * Checks if a file with a given path exists
	 * @param path path of the file to be checked
	 */
	public static boolean fileExists(String path) { return new File(path).exists(); }
}
