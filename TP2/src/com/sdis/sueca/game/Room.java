package com.sdis.sueca.game;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import com.sdis.sueca.rmi.Client;
import com.sdis.sueca.rmi.ClientInterface;

/**
 * An object of type Room represents an 'isolated' place where exactly four
 * users are playing the game 'Sueca'. It handles all of the logic for that game
 * in specific.
 */
public class Room {

	// Instance variables
	private int ID, turn;
	private boolean[] takenIDs;
	private final String trump;
	private int firstCardPlayerID;

	private ArrayList<Card> fullDeck;
	private HashMap<Integer, Card> tableCards;
	private HashMap<Integer, ClientInterface> players;

	// Final variables
	private final int PLAYER0 = 0;
	private final int PLAYER1 = 1;
	private final int PLAYER2 = 2;
	private final int PLAYER3 = 3;

	/** Creates a Room instance */
	public Room(int ID) {
		// Set turn and IDs
		turn = 0;
		this.ID = ID;
		firstCardPlayerID = 0;
		takenIDs = new boolean[] { false, false, false, false };

		// Set players and cards
		tableCards = new HashMap<Integer, Card>();
		players = new HashMap<Integer, ClientInterface>();

		// Set cards and trump
		generateFullDeck();
		trump = fullDeck.get(ThreadLocalRandom.current().nextInt(0, fullDeck.size())).getSuit();
	}

	// Instance methods
	/** Returns the room's ID */
	public int getID() { return ID; }

	/** Returns the room's current turn */
	public int getTurn() { return turn; }

	/** Returns the room's trump suit */
	public String getTrump() { return trump; }

	/** Returns the room's taken IDs */
	public boolean[] getTakenIDs() { return takenIDs; }

	/** Returns the room's full deck */
	public ArrayList<Card> getFullDeck() { return fullDeck; }

	/** Returns the room's table cards */
	public HashMap<Integer, Card> getTableCards() { return tableCards; }

	/** Returns the room's players */
	public HashMap<Integer, ClientInterface> getPlayers() { return players; }

	/**
	 * Returns a player with a given ID
	 * @param playerID the ID of the player to be returned
	 */
	public ClientInterface getPlayerByID(int playerID) { return players.get(playerID); }

	/**
	 * Adds a new player to the room
	 * @param player the new player to be added
	 * @throws RemoteException 
	 */
	public boolean addPlayer(ClientInterface player) throws RemoteException {
		for (int i = 0; i < takenIDs.length; i++) {
			if (takenIDs[i] == false) {
				player.setID(i);
				player.setRoomID(ID);
				player.setTrump(trump);

				if (i == 0)
					player.setTurn(true);
				else
					player.setTurn(false);

				takenIDs[i] = true;
				players.put(i, player);

				if (players.size() == 4)
					distributeCards();

				return true;
			}
		}

		return false;
	}

	/**
	 * Removes a given player from the room
	 * @param playerID the ID of the player to be removed
	 */
	public void removePlayer(int playerID) {
		if (players.containsKey(playerID)) {
			players.remove(playerID);
			takenIDs[playerID] = false;
		}
	}

	/** Generate a 'full' deck of cards */
	private void generateFullDeck() {
		fullDeck = new ArrayList<Card>();

		// Create cards 2 to 7
		for (int i = 2; i < 8; i++)
			for (int j = 0; j < Card.SUITS.length; j++)
				fullDeck.add(new Card(Integer.toString(i), Card.SUITS[j]));

		// Create aces
		for (int i = 0; i < Card.SUITS.length; i++)
			fullDeck.add(new Card("ace", Card.SUITS[i]));

		// Create kings
		for (int i = 0; i < Card.SUITS.length; i++)
			fullDeck.add(new Card("king", Card.SUITS[i]));

		// Create jacks
		for (int i = 0; i < Card.SUITS.length; i++)
			fullDeck.add(new Card("jack", Card.SUITS[i]));

		// Create queens
		for (int i = 0; i < Card.SUITS.length; i++)
			fullDeck.add(new Card("queen", Card.SUITS[i]));

		// Shuffle the deck
		Collections.shuffle(fullDeck, new Random(System.nanoTime()));
	}

	/** Distribute the cards among the players */
	public void distributeCards() throws RemoteException {
		// Serve the cards
		for (int i = 0; i < fullDeck.size(); i += 0)
			for (Entry<Integer, ClientInterface> p: players.entrySet())
				p.getValue().addCardToHand(fullDeck.get(i++));

		// Set number of cards
		int[] temp = new int[] { 10, 10, 10, 10 };
		for (Entry<Integer, ClientInterface> p: players.entrySet())
			p.getValue().setCardsCount(temp);
	}

	/** Returns the latest round's winner */
	private int roundWinner() {
		// Get first placed card
		int winner = 0;
		Card bestCard = tableCards.get(firstCardPlayerID);

		// Check for trumps
		for (Entry<Integer, Card> c: tableCards.entrySet()) {
			if (c.getValue().isTrump(trump)) {
				bestCard = c.getValue();
				break;
			}
		}

		// Compare the trump or other card to the rest
		for (Entry<Integer, Card> c: tableCards.entrySet())
			if (c.getValue().getSuit().equals(bestCard.getSuit()) && c.getValue().getPower() > bestCard.getPower())
				bestCard = c.getValue();

		// Find out who the card belongs to
		for (Entry<Integer, Card> c: tableCards.entrySet())
			if (c.getValue().equals(bestCard))
				winner = c.getKey().intValue();

		return winner;
	}

	/**
	 * Places a given card on the table
	 * @param playerID the ID of the player placing the card
	 * @param cardIndex the index of the card to be placed
	 */
	private void placeCard(int playerID, int cardIndex) throws RemoteException {
		// Place the card
		tableCards.put(playerID, players.get(playerID).getCardByIndex(cardIndex));
		players.get(playerID).removeCardFromHandByIndex(cardIndex);

		// Update cards count
		int[] temp = new int[4];
		for (int i = 0; i < temp.length; i++)
			temp[i] = players.get(i).getCardsOnHand().size();

		// Notify all the players
		for (int i = 0; i < temp.length; i++) {
			players.get(i).setCardsCount(temp);
			players.get(i).setCardsOnTable(tableCards);
		}

		// Check if the round is over
		if (tableCards.size() == 4) {
			// Start timer
			Timer timer = new Timer();

			// Schedule task
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// Verify winner
					int winner = roundWinner();

					// The winner begins the next round
					turn = winner;
					
					// Notify players
					for (Entry<Integer, ClientInterface> c: players.entrySet()) {
						try {
							if (c.getKey().intValue() == turn)
								c.getValue().setTurn(true);
							else
								c.getValue().setTurn(false);
						} catch (RemoteException e) { return; }
					}

					// Calculate personal points
					int perPoints = 0;
					for (Entry<Integer, Card> c: tableCards.entrySet())
						perPoints += c.getValue().getPoints();

					try {
						// Set personal points
						players.get(winner).setPoints(perPoints + players.get(winner).getPoints());

						// Set team points
						if (winner == PLAYER0) {
							players.get(PLAYER0).setTeamPoints(players.get(PLAYER0).getPoints() + players.get(PLAYER2).getPoints());
							players.get(PLAYER2).setTeamPoints(players.get(PLAYER0).getPoints() + players.get(PLAYER2).getPoints());
						} else if (winner == PLAYER1) {
							players.get(PLAYER1).setTeamPoints(players.get(PLAYER1).getPoints() + players.get(PLAYER3).getPoints());
							players.get(PLAYER3).setTeamPoints(players.get(PLAYER1).getPoints() + players.get(PLAYER3).getPoints());
						} else if (winner == PLAYER2) {
							players.get(PLAYER2).setTeamPoints(players.get(PLAYER2).getPoints() + players.get(PLAYER0).getPoints());
							players.get(PLAYER0).setTeamPoints(players.get(PLAYER2).getPoints() + players.get(PLAYER0).getPoints());
						} else if (winner == PLAYER3) {
							players.get(PLAYER3).setTeamPoints(players.get(PLAYER3).getPoints() + players.get(PLAYER1).getPoints());
							players.get(PLAYER1).setTeamPoints(players.get(PLAYER3).getPoints() + players.get(PLAYER1).getPoints());
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					// Clear table
					tableCards.clear();

					// Notify players
					for (int i = 0; i < temp.length; i++) {
						try { players.get(i).setCardsOnTable(tableCards); }
						catch (RemoteException e) { e.printStackTrace(); }
					}
				}
			}, 2000);

			// Check if the game is over
			if (isGameOver()) {	
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						// Notify all players the game is over
						for (Entry<Integer, ClientInterface> c: players.entrySet()) {
							try { c.getValue().setGameOver(true); }
							catch (RemoteException e) { e.printStackTrace(); }
						}

						// Close timer
						timer.cancel();
						timer.purge();
					}
				}, 4000);
			}
		} else {
			// Next turn
			turn = (turn < 3) ? turn + 1 : 0;
			
			// Notify players
			for (Entry<Integer, ClientInterface> c: players.entrySet()) {
				if (c.getKey().intValue() == turn)
					c.getValue().setTurn(true);
				else
					c.getValue().setTurn(false);
			}
		}
	}

	/**
	 * Checks whether a given card can be placed on the table
	 * @param playerID the ID of the player placing the card
	 * @param cardIndex the index of the card to be placed
	 */
	public boolean canPlaceCard(int playerID, int cardIndex) throws RemoteException {
		// Check if it is the first entry
		if (tableCards.isEmpty())
			firstCardPlayerID = playerID;

		// Check if it is this player's turn
		if (playerID == turn) {
			// Check if the table is empty
			if (tableCards.isEmpty()) {
				placeCard(playerID, cardIndex);
				return true;
			} else if (tableCards.size() < 4) {
				// Check if the player has THE suit if it isn't
				if (players.get(playerID).hasSuitOnHand(tableCards.get(firstCardPlayerID).getSuit())) {
					// Check if the suit is the same
					if (players.get(playerID).getCardByIndex(cardIndex).getSuit().equals(tableCards.get(firstCardPlayerID).getSuit())) {
						placeCard(playerID, cardIndex);
						return true;
					}
				} else {
					placeCard(playerID, cardIndex);
					return true;
				}
			}
		}

		return false;
	}

	/** Checks if the game is over */
	public boolean isGameOver() throws RemoteException {
		for (Entry<Integer, ClientInterface> p: players.entrySet())
			if (!p.getValue().getCardsOnHand().isEmpty())
				return false;
		return true;
	}

	@Override
	public String toString() {
		String str = "";

		str += "Table: " + tableCards.toString() + "\n\n";
		for (Entry<Integer, ClientInterface> p: players.entrySet())
			str += ((Client) p.getValue()).toString() + "\n";

		return str;
	}
}