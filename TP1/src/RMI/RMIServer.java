package RMI;

import java.rmi.RemoteException;

public class RMIServer implements RMIInterface
{
	public RMIServer() {}

	public String sayHello() throws RemoteException {
		return "I said Hello!";
	}
}
