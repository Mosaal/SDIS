package Peer;

import RMI.RMIInterface;
import RMI.RMIServer;
import Utils.Utils;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer {

	public static void printUsage() {
		System.out.println("Usage: java -cp ./bin Peers.Peer <protocol_version> <server_id> <access_point> <mc_channel_ip>:<port> <mdb_channel_ip>:<port> <mdr_channel_ip>:<port>");
		System.out.println("Where:");
		System.out.println("\t<protocol_version> is the version of the protocol used");
		System.out.println("\t<server_id> is the unique identifier of the server (must be an integer)");
		System.out.println("\t<remote_object_name> is the name the server bound the remote object to");
		System.out.println("\t<mc_channel_ip>:<port> is the IP address of Multicast Control Channel followed by its Port");
		System.out.println("\t<mdb_channel_ip>:<port> is the IP address of Multicast Data Backup Channel followed by its Port");
		System.out.println("\t<mdr_channel_ip>:<port> is the IP address of Multicast Data Recovery Channel followed by its Port");
	}

	private static boolean procArgs(String[] args) {
		if (args.length != 6) {
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}

		if (!Utils.isStringInteger(args[1])) {
			System.out.println("ERROR: Argument '" + args[1] + "' is not an integer.");
			return false;
		}

		if ((args[3].contains(":")) && (args[4].contains(":")) && (args[5].contains(":"))) {
			if (!Utils.isStringInteger(args[3].split(":")[1])) {
				System.out.println("ERROR: Port '" + args[3].split(":")[1] + "' is not an integer.");
				return false; }
			if (!Utils.isStringInteger(args[4].split(":")[1])) {
				System.out.println("ERROR: Port '" + args[4].split(":")[1] + "' is not an integer.");
				return false; }
			if (!Utils.isStringInteger(args[5].split(":")[1])) {
				System.out.println("ERROR: Port '" + args[5].split(":")[1] + "' is not an integer.");
				return false;
			}
		} else {
			System.out.println("ERROR: One of the last three arguments does not have the correct format (IP:Port)");
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		if (!procArgs(args)) {
			printUsage();
			return;
		}

		try {
			RMIServer rmiServer = new RMIServer();
			RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(rmiServer, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.bind("kappa", stub);
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
			printUsage();
			System.exit(-1);
		}
	}
}
