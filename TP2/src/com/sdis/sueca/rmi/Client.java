package com.sdis.sueca.rmi;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import com.sdis.sueca.game.Card;

public class Client extends UnicastRemoteObject implements ClientInterface {
	
	private static final long serialVersionUID = -2354958749573669303L;
	
	// Instance variables
	private int ID, roomID;
	private ServerInterface stub;
	
	private int points;
	private boolean turn;
	private int teamPoints;
	private ArrayList<Card> cardsOnHand;
	
	private String trump;
	private int[] cardsCount;
	private boolean gameOver;
	private HashMap<Integer, Card> tableCards;

	/**
	 * Creates a Client instance
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException 
	 */
	public Client(boolean connect, String ipAddress) throws RemoteException, NotBoundException {
		// Set points
		points = 0;
		trump = "";
		turn = false;
		teamPoints = 0;
		gameOver = false;
		
		// Set ID and card sets
		cardsOnHand = new ArrayList<Card>();
		cardsCount = new int[] { 0, 0, 0, 0 };
		tableCards = new HashMap<Integer, Card>();

		// Start client
		if (connect) {
			Registry registry = LocateRegistry.getRegistry(ipAddress);
			stub = (ServerInterface) registry.lookup("SUECA_SERVER");
			stub.joinRoom((ClientInterface) this);
		}
	}

	// Instance methods
	/** Returns the client's ID */
	public int getID() { return ID; }
	
	/** Returns the client's turn */
	public boolean isTurn() { return turn; }
	
	/** Returns the client's room ID */
	public int getRoomID() { return roomID; }
	
	@Override
	public int getPoints() throws RemoteException { return points; }
	
	/** Returns the game's current trump */
	public String getTrump() { return trump; }
	
	/** Returns the state of the game */
	public boolean isGameOver() { return gameOver; }
	
	/** Returns the player's team points */
	public int getTeamPoints() { return teamPoints; }
	
	/** Returns the amount of cards on all of player's hands */
	public int[] getCardsCount() { return cardsCount; }
	
	/** Returns the amount of cards on hand */
	public int countCardsOnHand() { return cardsOnHand.size(); }
	
	@Override
	public ArrayList<Card> getCardsOnHand() throws RemoteException { return cardsOnHand; }
	
	/** Returns the cards currently on the table */
	public HashMap<Integer, Card> getCardsOnTable() { return tableCards; }
	
	@Override
	public Card getCardByIndex(int index) throws RemoteException { return cardsOnHand.get(index); }
	
	/** Try to join a room */
	public boolean joinRoom() {
		try { return stub.joinRoom((ClientInterface) this); }
		catch (RemoteException e) { e.printStackTrace(); return false; }
	}
	
	/**
	 * Check if a given card can be placed on the table
	 * @param card the card to be placed
	 */
	public boolean canPlaceCard(int cardIndex) {
		try { return stub.canPlaceCard(roomID, ID, cardIndex); }
		catch (RemoteException e) { return false; }
	}
	
	/** Quit game */
	public void quitGame() {
		try { stub.quitGame(roomID, ID); }
		catch (RemoteException e) { return; }
	}
	
	/** Leave game */
	public void gameOver() {
		try { stub.gameOver(roomID, ID); }
		catch (RemoteException e) { return; }
	}
	
	@Override
	public void setID(int ID) throws RemoteException { this.ID = ID; }
	
	@Override
	public void setTurn(boolean turn) throws RemoteException { this.turn = turn; }
	
	@Override
	public void setTrump(String trump) throws RemoteException { this.trump = trump; }
	
	@Override
	public void setRoomID(int roomID) throws RemoteException { this.roomID = roomID; }
	
	@Override
	public void addCardToHand(Card card) throws RemoteException { cardsOnHand.add(card); }
	
	@Override
	public void setPoints(int points) throws RemoteException { this.points = points; }
	
	@Override
	public void setGameOver(boolean gameOver) throws RemoteException { this.gameOver = gameOver; }
	
	@Override
	public void setTeamPoints(int teamPoints) throws RemoteException { this.teamPoints = teamPoints; }
	
	@Override
	public void setCardsCount(int[] cardsCount) throws RemoteException { this.cardsCount = cardsCount; }
	
	@Override
	public void removeCardFromHandByIndex(int cardIndex) throws RemoteException { cardsOnHand.remove(cardIndex); }
	
	@Override
	public void setCardsOnTable(HashMap<Integer, Card> tableCards) throws RemoteException { this.tableCards = tableCards; }
	
	@Override
	public boolean hasSuitOnHand(String suit) throws RemoteException {
		for (Card card: cardsOnHand)
			if (card.getSuit().equals(suit))
				return true;
		return false;
	}
	
	@Override
	public String toString() {
		String str = "";
		
		str += "ID: " + ID + "\n";
		str += "RoomID: " + roomID + "\n";
		str += "On hand: " + cardsOnHand.toString() + "\n";
		
		return str;
	}
}
