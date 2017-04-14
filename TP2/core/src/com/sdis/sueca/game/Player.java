package com.sdis.sueca.game;

import java.util.ArrayList;

/**
 * An object of type Player represents an user
 * currently playing the game. Each Player has an
 * unique identifier and a set of cards.
 */
public class Player {

	// Instance variables
	private final int ID;
	private ArrayList<Card> cards;

	// Static variables
	public static final int PLAYER_ONE = 0;
	public static final int PLAYER_TWO = 1;
	public static final int PLAYER_THREE = 2;
	public static final int PLAYER_FOUR = 3;

	/**
	 * Creates a Player instance
	 * @param ID the unique identifier of the player
	 */
	public Player(final int ID) { this.ID = ID; }

	// Instance methods
	/** Returns the ID of the player */
	public final int getID() { return ID; }
	
	/** Returns the cards in the hand of the player */
	public ArrayList<Card> getCards() { return cards; }

	/** Returns the number of cards in the hand of the player */
	public final int getNumberOfCards() { return cards.size(); }
	
	/**
	 * Returns a given card from the player's hand
	 * @param index the index the card is located at
	 */
	public Card getCard(final int index) { return cards.get(index); }

	/**
	 * Sets the cards in the hand of the player
	 * @param cards the cards to be set in the hand
	 */
	public void setCards(ArrayList<Card> cards) { this.cards = cards; }
}
