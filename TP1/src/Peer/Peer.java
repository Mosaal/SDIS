package Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import Utils.Utils;

public class Peer {

	private static String port;

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

		port = args[2];
		if (!Utils.isStringInteger(port)) {
			System.out.println("ERROR: Argument '" + port + "' is not an integer.");
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

		// Start Peer loop
		peerThread.start();

		// Give the user the option to cancel the Peer
		System.out.println("Press Enter to stop executing...");
		try { System.in.read(); } 
		catch (IOException e) { e.printStackTrace(); }
		
		System.out.println("Shutting down...");
		System.exit(0);
	}

	private static Thread peerThread = new Thread(new Runnable() {
		// Create TCP server
		public PrintWriter out = null;
		public BufferedReader in = null;
		public Socket clientSocket = null;
		public ServerSocket serverSocket = null;

		@Override
		public void run() {
			// Initialize TCP server
			try {
				serverSocket = new ServerSocket(Integer.parseInt(port));
			} catch (IOException e) {
				System.out.println("Failed to initalize TCP server.");
				System.exit(-1);
			}

			while (true) {
				// Wait for a connection
				try {
					System.out.println("Waiting for a connection...");
					clientSocket = serverSocket.accept();
					System.out.println("A connection was made to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

					out = new PrintWriter(clientSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (IOException e) {
					System.out.println("Error on setup.");
					System.exit(-1);
				}
			}
		}
	});
}
