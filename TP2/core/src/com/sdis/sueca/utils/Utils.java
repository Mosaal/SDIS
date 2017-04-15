package com.sdis.sueca.utils;

public class Utils {

	// Static methods
	/**
	 * Converts 0-255 RGB to 0-1 RGB
	 * @param r the R component
	 * @param g the G component
	 * @param b the B component
	 */
	public static float[] toUnaryRGB(int r, int g, int b) { return new float[] { r / 255f, g / 255f, b / 255f }; }
}
