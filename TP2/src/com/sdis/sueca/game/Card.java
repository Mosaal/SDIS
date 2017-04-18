package com.sdis.sueca.game;

/**
 * An object of type Card represents a playing card from a
 * standard card deck. The card has a suit, which
 * can be spades, hearts, diamonds, clubs. A spade, heart,
 * diamond, or club has one of the 13 values: ace, 2, 3, 4, 5, 6, 7,
 * 8, 9, 10, jack, queen, or king.
 */
public class Card {

	// Instance variables
	private int x, y;
	private final int value;
	private final int suit;

	// Static variables
	// Values
	public static final int ACE = 1;
	public final static int JACK = 11;
	public final static int QUEEN = 12;
	public final static int KING = 13;
	public final static String[] VALUES = new String[] { "ace", "jack", "queen", "king" };

	// Suits
	public final static int CLUBS = 0;
	public final static int SPADES = 1;
	public final static int HEARTS = 2;
	public final static int DIAMONDS = 3;
	public final static String[] SUITS = new String[] { "clubs", "spades", "hearts", "diamonds" };

	/**
	 * Creates a Card instance
	 * @param x the x coordinate of the card
	 * @param y the y coordinate of the card
	 * @param value the value of the card
	 * @param suit the suit of the card
	 * @param value
	 * @param suit
	 */
	public Card(int x, int y, final int value, final int suit) {
		this.x = x;
		this.y = y;
		this.value = value;
		this.suit = suit;
	}
	
	// Instance methods
	/** Returns the x coordinate of the card */
	public int getX() { return x; }

	/** Returns the y coordinate of the card */
	public int getY() {	return y; }

	/** Returns the value of the card */
	public final int getValue() { return value; }

	/** Returns the suit of the card */
	public final int getSuit() { return suit; }
	
	/**
	 * Sets the card's position
	 * @param x the x coordinate of the card
	 * @param y the y coordinate of the card
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
}