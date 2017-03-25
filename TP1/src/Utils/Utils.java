package Utils;

import java.io.File;

public class Utils {

	public static boolean isStringInteger(String str) {
		try { Integer.parseInt(str); }
		catch (NumberFormatException e) { return false; }
		return true;
	}

	public static boolean fileExists(String path) { return new File(path).exists(); }
}
