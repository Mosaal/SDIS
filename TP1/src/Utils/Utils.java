package Utils;

import java.io.File;

public class Utils
{
	public Utils() {}

	public static boolean isStringInteger(String paramString)
	{
		try
		{
			Integer.parseInt(paramString);
		} catch (NumberFormatException localNumberFormatException) {
			return false;
		}
		return true;
	}



	public static boolean fileExists(String paramString)
	{
		return new File(paramString).exists();
	}
}
