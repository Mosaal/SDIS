package com.sdis.sueca.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import com.sdis.sueca.game.Room;

public class Server extends UnicastRemoteObject implements ServerInterface {

	// Serial Version ID
	private static final long serialVersionUID = -4402955577687933238L;

	// Instance variables
	private HashMap<Integer, Room> activeRooms;
	
	// Static variables
	public static int LAST_ROOM_ID = 0;
	public static final String SERVER_NAME = "SUECA_SERVER";
	
	/** Creates a Server instance */
	public Server() throws RemoteException {
		super();
		
		// Initialize rooms
		activeRooms = new HashMap<Integer, Room>();
		
		// Create registry
		LocateRegistry.createRegistry(1099);
		
		// Initialize server
		ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);
		
		// Bind the remote object's stub in the registry
		Registry registry = LocateRegistry.getRegistry();
        registry.rebind(SERVER_NAME, stub);
	}
	
	// Instance methods
	/** Returns this Server's map of currently active rooms */
	public HashMap<Integer, Room> getActiveRooms() { return activeRooms; }
}
