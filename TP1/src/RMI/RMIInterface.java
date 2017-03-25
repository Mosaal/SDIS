package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface
extends Remote
{
	public abstract String sayHello()
			throws RemoteException;
}