package Peer;

import RMI.RMIInterface;
import RMI.RMIServer;
import Utils.Utils;
import java.io.PrintStream;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class Peer
{
	public Peer() {}

	public static void printUsage()
	{
		System.out.println("Usage: java -cp ./bin Peers.Peer <protocol_version> <server_id> <access_point> <mc_channel_ip>:<port> <mdb_channel_ip>:<port> <mdr_channel_ip>:<port>");
		System.out.println("Where:");
		System.out.println("\t<protocol_version> is the version of the protocol used");
		System.out.println("\t<server_id> is the unique identifier of the server (must be an integer)");
		System.out.println("\t<remote_object_name> is the name the server bound the remote object to");
		System.out.println("\t<mc_channel_ip>:<port> is the IP address of Multicast Control Channel followed by its Port");
		System.out.println("\t<mdb_channel_ip>:<port> is the IP address of Multicast Data Backup Channel followed by its Port");
		System.out.println("\t<mdr_channel_ip>:<port> is the IP address of Multicast Data Recovery Channel followed by its Port");
	}





	private static boolean procArgs(String[] paramArrayOfString)
	{
		if (paramArrayOfString.length != 6) {
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}


		if (!Utils.isStringInteger(paramArrayOfString[1])) {
			System.out.println("ERROR: Argument '" + paramArrayOfString[1] + "' is not an integer.");
			return false;
		}


		if ((paramArrayOfString[3].contains(":")) && (paramArrayOfString[4].contains(":")) && (paramArrayOfString[5].contains(":"))) {
			if (!Utils.isStringInteger(paramArrayOfString[3].split(":")[1])) {
				System.out.println("ERROR: Port '" + paramArrayOfString[3].split(":")[1] + "' is not an integer.");
				return false; }
			if (!Utils.isStringInteger(paramArrayOfString[4].split(":")[1])) {
				System.out.println("ERROR: Port '" + paramArrayOfString[4].split(":")[1] + "' is not an integer.");
				return false; }
			if (!Utils.isStringInteger(paramArrayOfString[5].split(":")[1])) {
				System.out.println("ERROR: Port '" + paramArrayOfString[5].split(":")[1] + "' is not an integer.");
				return false;
			}
		} else {
			System.out.println("ERROR: One of the last three arguments does not have the correct format (IP:Port)");
			return false;
		}

		return true;
	}





	public static void main(String[] paramArrayOfString)
	{
		try
		{
			RMIServer localRMIServer = new RMIServer();
			RMIInterface localRMIInterface = (RMIInterface)java.rmi.server.UnicastRemoteObject.exportObject(localRMIServer, 0);


			Registry localRegistry = java.rmi.registry.LocateRegistry.getRegistry();
			localRegistry.bind("kappa", localRMIInterface);
		} catch (RemoteException|AlreadyBoundException localRemoteException) {
			localRemoteException.printStackTrace();

			printUsage();
			System.exit(-1);
		}
	}
}
