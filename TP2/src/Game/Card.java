package Game;

public class Card {

	// Instance variables
	private final String set;
	private final String rank;
	
	// Static variables
	public static final String ACE = "ACE";
	public static final String TWO = "TWO";
	public static final String THREE = "THREE";
	public static final String FOUR = "FOUR";
	public static final String FIVE = "FIVE";
	public static final String SIX = "SIX";
	public static final String SEVEN = "SEVEN";
	public static final String EIGHT = "EIGHT";
	public static final String NINE = "NINE";
	public static final String TEN = "TEN";
	public static final String JACK = "JACK";
	public static final String QUEEN = "QUEEN";
	public static final String KING = "KING";
	
	public static final String CLUBS = "CLUBS";
	public static final String HEARTS = "HEARTS";
	public static final String SPADES = "SPADES";
	public static final String DIAMONDS = "DIAMONDS";
	
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
