package com.sdis.sueca.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	
	/**
	 * A Client's request to join a room
	 * @param player the player trying to join a room
	 * @throws RemoteException
	 */
	boolean joinRoom(ClientInterface player) throws RemoteException;
	
	/**
	 * A Client's request to place a card
	 * @param roomID the ID of the room the player belongs to
	 * @param playerID the ID of the player placing the card
	 * @param cardIndex the index of the card to be placed
	 * @throws RemoteException
	 */
	boolean canPlaceCard(int roomID, int playerID, int cardIndex) throws RemoteException;
	
	/**
	 * A Client's request to quit leave a room
	 * @param roomID the ID of the room the player belongs to
	 * @param playerID the ID of the player quitting the game
	 * @throws RemoteException
	 */
	public void quitGame(int roomID, int playerID) throws RemoteException;
	
	/**
	 * A Client's request to leave the room when the game is over
	 * @param roomID the ID of the room the player belongs to
	 * @param playerID the ID of the player leaving the game
	 * @throws RemoteException
	 */
	public void gameOver(int roomID, int playerID) throws RemoteException;
}
