package com.sdis.sueca.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.util.ResourceLoader;

public class Utils {
	
	// Static variables
	public static final int MAX_SCORES = 10;
	public static final Color gold = new Color(212, 175, 55);
	
	// Static methods
	/**
	 * Loads the game's font with a given size
	 * @param fontSize the size of the font to load
	 */
	@SuppressWarnings("unchecked")
	public static UnicodeFont loadFont(int fontSize) {
		Font font = null;
		try { font = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("assets/font/Blackjack.ttf")); }
		catch (FontFormatException | IOException e) { e.printStackTrace(); }
		font = font.deriveFont(Font.PLAIN, fontSize);

		UnicodeFont uniFont = new UnicodeFont(font);
		uniFont.addAsciiGlyphs();
		uniFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		try { uniFont.loadGlyphs(); }
		catch (SlickException e) { e.printStackTrace(); }
		
		return uniFont;
	}
	
	/**
	 * Saves a given score to the disk<br>
	 * Format: PPP_DD-MM-YYYY_HH:MM
	 * @param score the score to be saved
	 */
	public static void saveScore(int score) {
		try {
			// Latest scores
			String[] scores = null;
			String[] temp = loadScores();
			
			// Get date and time
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
			
			// Check if it is empty
			if (temp != null && temp.length != 0) {
				// Set old scores
				scores = new String[temp.length + 1];
				System.arraycopy(temp, 0, scores, 0, temp.length);
				
				// Add the new one
				scores[temp.length] = String.format("%03d", score) + "_" + df.format(date);
				
				// Sort scores
				Arrays.sort(scores, Collections.reverseOrder());
				
				// Remove last one if it is greater than 10
				if (scores.length > MAX_SCORES) {
					// Do a backup
					temp = new String[MAX_SCORES];
					System.arraycopy(scores, 0, temp, 0, MAX_SCORES);
					
					// Restore data
					scores = new String[MAX_SCORES];
					System.arraycopy(temp, 0, scores, 0, MAX_SCORES);
				}
			} else {
				// Add the new one if it is
				String data = String.format("%03d", score) + "_" + df.format(date);
				scores = new String[] { data };
			}
			
			// Open the streams
			FileOutputStream fos = new FileOutputStream("assets/scores/scores.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// Write the data
			oos.writeObject(scores);
			
			// Close the streams
			fos.close();
			oos.close();
		} catch (IOException e) { return; }
	}
	
	/**
	 * Returns the high scores stored on the disk<br>
	 * Format: PPP_DD-MM-YYYY_HH:MM
	 */
	public static String[] loadScores() {
		try {
			// Check that file exists
			if (new File("assets/scores/scores.ser").exists()) {
				// Open the streams
				FileInputStream fis = new FileInputStream("assets/scores/scores.ser");
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				// Read the data
				String[] scores = (String[]) ois.readObject();
				
				// Close the streams
				fis.close();
				ois.close();
				
				// Return data
				return scores;
			} else {
				// Create file if it doesn't
				new File("assets/scores/scores.ser").createNewFile();
				return new String[] {};
			}
		} catch (IOException | ClassNotFoundException e) { return null; }
	}
}
