package Interface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Utils.Utils;

public class TestApp {

	// Static constant variables
	private static final int TCP = 0;
	private static final int PROT = 1;
	private static final int FILE = 2;
	private static final int SPAC = 2;
	private static final int REPL = 3;

	/** Prints the correct way to initialize and execute an instance of this class */
	private static void printUsage() {
		System.out.println("Usage: java TestApp <ip_address>:<port> <sub_protocol> <file_path/space_to_reclaim> <rep_degree>");
		System.out.println("Where:");
		System.out.println("\t<ip_address>:<port> is the address of the service Peer followed by its Port");
		System.out.println("\t<sub_protocol> is the operation the peer of the backup service must execute (BACKUP, RESTORE, DELETE, RECLAIM, STATE)");
		System.out.println("\t<file_path/space_to_reclaim> is either the path name of the file to BACKUP/RESTORE/DELETE or the amount of space to RECLAIM (in KByte)");
		System.out.println("\t<rep_degree> is an integer that specifies the desired replication degree and applies only to the BACKUP protocol (between 1 and 9)");
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
		if (!args[TCP].contains(":")) {
			System.out.println("ERROR: The first argument has an incorrect format.");
			return false;
		} else if (!Utils.isStringInteger(args[TCP].split(":")[1])) {
			System.out.println("ERROR: The port '" + args[TCP].split(":")[1] + "' is not an integer.");
			return false;
		}

		// Check remaining arguments
		switch (args.length) {
		case 2:
			if (!args[PROT].equals(Utils.STATE_STRING)) {
				System.out.println("ERROR: '" + args[PROT] + "' is not a valid argument.");
				return false;
			}
			break;
		case 3:
			if (args[PROT].equals(Utils.RECLAIM_STRING)) {
				if (!Utils.isStringInteger(args[SPAC])) {
					System.out.println("ERROR: Argument '" + args[SPAC] + "' is not an integer.");
					return false;
				}
			} else if (!args[PROT].equals(Utils.RESTORE_STRING) && !args[PROT].equals(Utils.DELETE_STRING)) {
				System.out.println("ERROR: '" + args[PROT] + "' is not a valid argument.");
				return false;
			}
			break;
		case 4:
			if (args[PROT].equals(Utils.BACKUP_STRING)) {
				if (!Utils.isStringInteger(args[REPL])) {
					System.out.println("ERROR: Argument '" + args[REPL] + "' is not an integer.");
					return false;
				} else if (Integer.parseInt(args[REPL]) < 1 && Integer.parseInt(args[REPL]) > 9) {
					System.out.println("ERROR: Replication degree must be in the following interval [1, 9]");
					return false;
				}
			} else {
				System.out.println("ERROR: '" + args[PROT] + "' is not a valid argument.");
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
		String address = args[TCP].split(":")[0];
		String port = args[TCP].split(":")[1];

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
		String request = args[PROT];
		switch (args[PROT]) {
		case Utils.BACKUP_STRING:
			request += " " + args[FILE] + " " + args[REPL];
			break;
		case Utils.RESTORE_STRING:
			request += " " + args[FILE];
			break;
		case Utils.DELETE_STRING:
			request += " " + args[FILE];
			break;
		case Utils.RECLAIM_STRING:
			request += " " + args[SPAC];
			break;
		}

		// Send request to Peer
		out.println(request);

		try {
			// Wait and parse reply
			String reply = null;
			while ((reply = in.readLine()) != null) {
				if (reply.equals(Utils.SUCCESS_MESSAGE)) {
					System.out.println("Request successful.");
				} else if (reply.equals(Utils.ERROR_MESSAGE)) {
					System.out.println("An error ocurred while processing the request.");
				} else if (reply.equals(Utils.END_MESSAGE)) {
					break; // Get out of the Loop
				} else {
					System.out.println(reply);
				}
			}
		} catch (IOException e) {
			System.out.println("Failed to read reply from the Peer.");
			System.exit(-1);
		}

		try {
			// Close everything
			System.out.println("Shutting down...");
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Failed to shut down correctly. Exiting anyway...");
			System.exit(-1);
		}
	}
}
