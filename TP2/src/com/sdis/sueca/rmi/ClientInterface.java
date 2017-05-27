package com.sdis.sueca.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sdis.sueca.game.Card;

public interface ClientInterface extends Remote {

	/**
	 * Sets the client's ID
	 * @param ID the ID to be set
	 */
	void setID(int i) throws RemoteException;

	/**
	 * Sets the client's turn
	 * @param turn the turn to be set
	 */
	void setTurn(boolean turn) throws RemoteException;

	/**
	 * Sets the client's room ID
	 * @param roomID the ID of the room the client belongs to
	 */
	void setRoomID(int iD) throws RemoteException;

	/**
	 * Sets the game's current trump
	 * @param trump the trump to be set
	 */
	void setTrump(String trump) throws RemoteException;
	
	/**
	 * Adds a new card to the player's hand
	 * @param card the new card to be added
	 */
	void addCardToHand(Card card) throws RemoteException;
	
	/**
	 * Sets the player's personal points
	 * @param points the points to be set
	 */
	void setPoints(int points) throws RemoteException;
	
	/**
	 * Sets the player's team points
	 * @param teamPoints the points to be set
	 */
	void setTeamPoints(int teamPoints) throws RemoteException;
	
	/**
	 * Sets the amount of cards on all of player's hands
	 * @param cardsCount the array with the amount of cards
	 */
	void setCardsCount(int[] cardsCount) throws RemoteException;
	
	/**
	 * Sets the cards currently on the table
	 * @param tableCards the cards to be set
	 */
	void setCardsOnTable(HashMap<Integer, Card> tableCards) throws RemoteException;
	
	/**
	 * Sets the game as over
	 * @param gameOver the state of the game
	 */
	void setGameOver(boolean gameOver) throws RemoteException;
	
	/**
	 * Returns a card with a given index
	 * @param index the index of the card to be returned
	 */
	Card getCardByIndex(int cardIndex) throws RemoteException;

	/**
	 * Removes a card with a given index of the player's hand
	 * @param card the index of the card to be removed
	 */
	void removeCardFromHandByIndex(int cardIndex) throws RemoteException;

	/**
	 * Checks if the player has a card of the given suit
	 * @param suit the suit to be checked for
	 */
	boolean hasSuitOnHand(String suit) throws RemoteException;

	/** Returns the player's cards on hand */
	ArrayList<Card> getCardsOnHand() throws RemoteException;
	
	/** Returns the player's points */
	int getPoints() throws RemoteException;
}
