package com.sdis.sueca.game;

import java.io.Serializable;

/**
* An object of type Card represents a playing card from a standard card deck.
* The card has a suit, which can be spades, hearts, diamonds, clubs. A spade,
* heart, diamond, or club has one of the 13 values: ace, 2, 3, 4, 5, 6, 7, jack, queen or king.
*/
public class Card implements Serializable {

	private static final long serialVersionUID = -5910897295174014510L;
	
	// Instance variables
	private int x, y;
	private final String suit;
	private final String value;
	private final int power, points;
	
	// Static variable
	public static final String[] SUITS = { "clubs", "hearts", "spades", "diamonds" };
	
	/**
	 * Creates a Card instance
	 * @param value the value of the card
	 * @param suit the suit of the card
	 */
	public Card(final String value, final String suit) {
		// Set card
		this.suit = suit;
		this.value = value;
		
		// Set points and power
		if (value.equals("ace")) {
			power = 11;
			points = 11;
		} else if (value.equals("7")) {
			power = 10;
			points = 10;
		} else if (value.equals("king")) {
			power = 9;
			points = 4;
		} else if (value.equals("jack")) {
			power = 8;
			points = 3;
		} else if (value.equals("queen")) {
			power = 7;
			points = 2;
		} else {
			points = 0;
			power = Integer.parseInt(value);
		}
	}

	// Instance methods
	/** Returns the card's x coordinate */
	public int getX() { return x; }

	/** Returns the card's y coordinate */
	public int getY() { return y; }
	
	/** Returns the card's power */
	public int getPower() { return power; }
	
	/** Returns the card's points */
	public int getPoints() { return points; }

	/** Returns the card's suit */
	public String getSuit() { return suit; }

	/** Returns the card's value */
	public String getValue() { return value; }
	
	/**
	 * Checks if the card is a trump
	 * @param trump the suit of the trump
	 */
	public boolean isTrump(String trump) { return suit.equals(trump); }
	
	/**
	 * Set the card's x and y coordinates
	 * @param x the card's x coordinate
	 * @param y the card's y coordinate
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		
		Card card = (Card) obj;
		if (toString().equals(card.toString()))
			return true;
		
		return false;
	}
	
	@Override
	public String toString() { return value + "_of_" + suit; }
}