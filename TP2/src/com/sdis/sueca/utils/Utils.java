package com.sdis.sueca.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Utils {

	public static BufferedImage loadImage(String imagePath) {
		try { return ImageIO.read(new File(imagePath)); }
		catch (IOException e) { return null; }
	}
	
}
