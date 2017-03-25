package RMI;

import java.rmi.RemoteException;

public class RMIServer implements RMIInterface {

	@Override
	public String sayHello() throws RemoteException {
		return "I said Hello!";
	}
}
