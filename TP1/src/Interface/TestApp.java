package Interface;

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
		
		System.out.println("Hello");
	}
}
