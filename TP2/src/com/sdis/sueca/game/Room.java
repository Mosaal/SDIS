package com.sdis.sueca.game;

import java.util.HashMap;

/**
 * An object of type Room represents an
 * 'isolated' place where exactly four users
 * are playing the game 'Sueca'. It handles all of the
 * logic for that game in specific.
 */
public class Room {

	// Instance variables
	private HashMap<Integer, Player> players;

	/** Creates a Room instance */
	public Room() {
		players = new HashMap<Integer, Player>();
	}

	// Instance methods	
	/** Returns the players in the room */
	public HashMap<Integer, Player> getPlayers() { return players; }

	/** Returns the number of players in the room */
	public final int getNumberOfPlayers() { return players.size(); }

	/**
	 * Returns the player with a given ID
	 * @param playerID the ID of the player to be returned
	 */
	public Player getPlayerByID(final int playerID) {
		if (players.containsKey(playerID))
			return players.get(playerID);
		return null;
	}
	
	/**
	 * Removes a given player from the room
	 * @param playerID the ID of the player to be removed
	 */
	public void removePlayer(final int playerID) {
		if (players.containsKey(playerID))
			players.remove(playerID);
	}

	/**
	 * Adds a new player to the room
	 * @param newPlayer player to be added to the room
	 */
	public void setPlayer(Player newPlayer) {
		if (!players.containsKey(newPlayer.getID()))
			players.put(newPlayer.getID(), newPlayer);
	}
}