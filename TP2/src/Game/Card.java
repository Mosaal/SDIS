package Game;

public class Card {

	// Instance variables
	private final String set;
	private final String rank;
	
	/**
	 * Creates a Card instance
	 * @param set the set the card belongs to
	 * @param rank the rank the card belongs to
	 */
	public Card(final String set, final String rank) {
		this.set = set;
		this.rank = rank;
	}
	
	// Instance methods
	/** Returns the set the card belongs to */
	public final String getSet() { return set; }
	
	/** Returns the rank the card belongs to */
	public final String getRank() { return rank; }
}
