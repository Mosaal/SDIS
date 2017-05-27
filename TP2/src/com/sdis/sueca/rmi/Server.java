package com.sdis.sueca.rmi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;

import com.sdis.sueca.game.Room;

public class Server extends UnicastRemoteObject implements ServerInterface {

	// Serial Version ID
	private static final long serialVersionUID = -4402955577687933238L;

	// Instance variables
	private int lastRoomID;
	private Registry registry;
	private final String ipAddress;
	private HashMap<Integer, Room> activeRooms;

	/**
	 * Creates a Server instance
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public Server() throws RemoteException, UnknownHostException {
		// Set server settings
		lastRoomID = 0;
		activeRooms = new HashMap<Integer, Room>();
		ipAddress = InetAddress.getLocalHost().getHostAddress();

		// Initialize server
		registry = LocateRegistry.createRegistry(1099);
		registry.rebind("SUECA_SERVER", this);
	}

	// Instance methods
	/** Returns the ID of the last created room */
	public int getLastRoomID() { return lastRoomID; }

	/** Returns the server's IP address */
	public String getIPAddress() { return ipAddress; }

	/** Returns this Server's map of currently active rooms */
	public HashMap<Integer, Room> getActiveRooms() { return activeRooms; }

	/** Returns the number of active players */
	public int getNumActivePlayers() {
		int total = 0;

		for (Entry<Integer, Room> r: activeRooms.entrySet())
			total += r.getValue().getPlayers().size();

		return total;
	}

	/** Shuts the server down */
	public void shutDown() {
		try {
			// Notify all players server is down
			for (Entry<Integer, Room> r: activeRooms.entrySet())
				for (Entry<Integer, ClientInterface> c: r.getValue().getPlayers().entrySet())
					c.getValue().setGameOver(true);

			// Shut the server down
			registry.unbind("SUECA_SERVER");
			UnicastRemoteObject.unexportObject(registry, true);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized boolean joinRoom(ClientInterface player) throws RemoteException {
		// Check if a room is available
		for (Entry<Integer, Room> r: activeRooms.entrySet())
			if (r.getValue().addPlayer(player))
				return true;

		// Create a new room otherwise
		Room room = new Room(lastRoomID++);
		activeRooms.put(room.getID(), room);
		return room.addPlayer(player);
	}

	@Override
	public synchronized boolean canPlaceCard(int roomID, int playerID, int cardIndex) throws RemoteException {
		return activeRooms.get(roomID).canPlaceCard(playerID, cardIndex);
	}

	@Override
	public synchronized void quitGame(int roomID, int playerID) throws RemoteException {
		// Check if the game has begun on that room
		if (activeRooms.get(roomID).getPlayers().size() == 4) {
			// Notify all players the game is over
			for (Entry<Integer, ClientInterface> c: activeRooms.get(roomID).getPlayers().entrySet())
				c.getValue().setGameOver(true);
		}

		// Remover the player
		activeRooms.get(roomID).removePlayer(playerID);

		// And remove the room if it is empty
		if (activeRooms.get(roomID).getPlayers().isEmpty())
			activeRooms.remove(roomID);
	}

	@Override
	public synchronized void gameOver(int roomID, int playerID) throws RemoteException {
		// Remover the player
		activeRooms.get(roomID).removePlayer(playerID);

		// And remove the room if it is empty
		if (activeRooms.get(roomID).getPlayers().isEmpty())
			activeRooms.remove(roomID);
	}
}
