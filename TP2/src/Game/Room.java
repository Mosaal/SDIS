package Game;

import java.util.HashMap;

public class Room {

	// Instance variables
	private HashMap<Integer, Player> players;
	
	/** Creates a Room instance */
	public Room() { players = new HashMap<Integer, Player>(); }
	
	// Instance methods
	/** Returns the amount a active players in this Room */
	public final int getNumberPlayers() { return players.size(); }
	
	/** Returns the array of this Room's currently active players */
	public HashMap<Integer, Player> getPlayers() { return players; }
		
	/**
	 * Removes a given Player from the Room
	 * @param playerID the ID of the Player to be removed
	 */
	public void removePlayer(int playerID) { players.remove(playerID); }
	
	/**
	 * Adds a new Player to the Room
	 * @param newPlayer player to be added to the Room
	 */
	public void setPlayer(Player newPlayer) { players.put(newPlayer.getID(), newPlayer); }
}
