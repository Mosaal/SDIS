package Peer;

import Utils.Utils;

public class Peer {

	/** Prints the correct way to initialize and execute an instance of this class */
	public static void printUsage() {
		System.out.println("Usage: java -cp ./bin Peers.Peer <protocol_version> <server_id> <tcp_port> <mc_channel_ip>:<port> <mdb_channel_ip>:<port> <mdr_channel_ip>:<port>");
		System.out.println("Where:");
		System.out.println("\t<protocol_version> is the version of the protocol used");
		System.out.println("\t<server_id> is the unique identifier of the server");
		System.out.println("\t<tcp_port> is the TCP Port to which the TestApp shall connect to");
		System.out.println("\t<mc_channel_ip>:<port> is the IP address of Multicast Control Channel followed by its Port");
		System.out.println("\t<mdb_channel_ip>:<port> is the IP address of Multicast Data Backup Channel followed by its Port");
		System.out.println("\t<mdr_channel_ip>:<port> is the IP address of Multicast Data Recovery Channel followed by its Port");
	}

	/**
	 * Processes the arguments passed on the command line 
	 * @param args string array of arguments to be processed
	 */
	private static boolean procArgs(String[] args) {
		// Check for minimum amount of arguments
		if (args.length != 6) {
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}

		// Check if server_id and tcp_port are integers
		if (!Utils.isStringInteger(args[1])) {
			System.out.println("ERROR: Argument '" + args[1] + "' is not an integer.");
			return false;
		}
		
		if (!Utils.isStringInteger(args[2])) {
			System.out.println("ERROR: Argument '" + args[2] + "' is not an integer.");
			return false;
		}

		// Check the format of the remaining arguments
		if (args[3].contains(":") && args[4].contains(":") && args[5].contains(":")) {
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
		
		System.out.println("Hi");
	}
}
