package Game;

import java.util.ArrayList;

public class Player {

	// Instance variables
	private final int ID;
	private ArrayList<Card> cards;
	
	/**
	 * Creates a Player instance
	 * @param ID the unique identifier of the player
	 */
	public Player(final int ID) { this.ID = ID; }
	
	// Instance methods
	/** Returns the ID of the player */
	public final int getID() { return ID; }
	
	/** Returns the cards currently in the hand of the player */
	public ArrayList<Card> getCards() { return cards; }
	
	/** Sets the cards of the player */
	public void setCards(ArrayList<Card> cards) { this.cards = cards; }
}
