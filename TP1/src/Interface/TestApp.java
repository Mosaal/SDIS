package Interface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Utils.Utils;

public class TestApp {

	/** Prints the correct way to initialize and execute an instance of this class */
	private static void printUsage() {
		System.out.println("Usage: java -cp ./bin Interface.TestApp <ip_address>:<port> <sub_protocol> <file_path> <rep_degree>");
		System.out.println("Where:");
		System.out.println("\t<ip_address>:<port> is the address of the service Peer followed by its Port");
		System.out.println("\t<sub_protocol> is the operation the peer of the backup service must execute (BACKUP, RESTORE, DELETE, RECLAIM, STATE)");
		System.out.println("\t<file_path> is either the path name of the file to BACKUP/RESTORE/DELETE or the amount of space to RECLAIM (in KByte)");
		System.out.println("\t<rep_degree> is an integer that specifies the desired replication degree and applies only to the BACKUP protocol");
	}

	/**
	 * Processes the arguments passed on the command line 
	 * @param args string array of arguments to be processed
	 */
	private static boolean procArgs(String[] args) {
		// Check for minimum amount of arguments
		if (args.length < 2) {
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}

		// Check if the first argument has the correct format
		if (!args[0].contains(":")) {
			System.out.println("ERROR: The first argument has an incorrect format.");
			return false;
		}

		// Check remaining arguments
		switch (args.length) {
		case 2:
			if (!args[1].equals("STATE")) {
				System.out.println("ERROR: '" + args[1] + "' is not a valid argument.");
				return false;
			}
			break;
		case 3:
			if (args[1].equals("RECLAIM")) {
				if (!Utils.isStringInteger(args[2])) {
					System.out.println("ERROR: Argument '" + args[2] + "' is not an integer.");
					return false;
				}
			} else if (args[1].equals("RESTORE") || args[1].equals("DELETE")) {
				if (!Utils.fileExists(args[2])) {
					System.out.println("ERROR: The file '" + args[2] + "' does not exist.");
					return false;
				}
			} else {
				System.out.println("ERROR: '" + args[1] + "' is not a valid argument.");
				return false;
			}
			break;
		case 4:
			if (args[1].equals("BACKUP")) {
				if (!Utils.fileExists(args[2])) {
					System.out.println("ERROR: The file '" + args[2] + "' does not exist.");
					return false;
				}

				if (!Utils.isStringInteger(args[3])) {
					System.out.println("ERROR: Argument '" + args[3] + "' is not an integer.");
					return false;
				}
			} else {
				System.out.println("ERROR: '" + args[1] + "' is not a valid argument.");
				return false;
			}
			break;
		default: 
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		if (!procArgs(args)) {
			printUsage();
			return;
		}

		// Create TCP client
		PrintWriter out = null;
		BufferedReader in = null;
		Socket clientSocket = null;

		// Parse address and port
		String address = args[0].split(":")[0];
		String port = args[0].split(":")[1];

		// Initialize and try to connect to Peer
		try {
			clientSocket = new Socket(address, Integer.parseInt(port));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Connection to service Peer successful.");
		} catch (IOException e) {
			System.out.println("Failed to connect to " + address + ":" + port);
			System.exit(-1);
		}

		// Create message to be sent
		String request = args[1];
		switch (args[1]) {
		case "BACKUP":
			request += " " + args[2] + " " + args[3];
			break;
		case "RESTORE":
			request += " " + args[2];
			break;
		case "DELETE":
			request += " " + args[2];
			break;
		case "RECLAIM":
			request += " " + args[2];
			break;
		}

		// Send request to Peer
		out.println(request);

		try {
			// Wait and parse reply
			String reply = in.readLine();
			if (reply.equals("OK")) {
				System.out.println("Request successful.");
			} else if (reply.equals("ERROR")) {
				System.out.println("An error ocurred while making the request.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// Close everything
			System.out.println("Shutting down...");
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
