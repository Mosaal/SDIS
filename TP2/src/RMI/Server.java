package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ServerInterface {

	private static final long serialVersionUID = -4402955577687933238L;

	protected Server() throws RemoteException {
		super();
	}
	
}
