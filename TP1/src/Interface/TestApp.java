package Interface;

import RMI.RMIInterface;
import Utils.Utils;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

	/** Prints the correct way to initialize and execute an instance of this class */
	private static void printUsage() {
		System.out.println("Usage: java -cp ./bin Interface.TestApp <hostname>:<remote_object_name> <sub_protocol> <opnd_1> <opnd_2>");
		System.out.println("Where:");
		System.out.println("\t<hostname>:<remote_object_name> is the name of the host where the server runs followed by the name the server bound the remote object to");
		System.out.println("\t<sub_protocol> is the operation the peer of the backup service must execute (BACKUP, RESTORE, DELETE, RECLAIM, STATE)");
		System.out.println("\t<opnd_1> is either the path name of the file to BACKUP/RESTORE/DELETE or the amount of space to reclaim (in KByte)");
		System.out.println("\t<opnd_2> is an integer that specifies the desired replication degree and applies only to the BACKUP protocol");
	}

	/**
	 * Processes the arguments passed on the command line 
	 * @param args string array of arguments to be processed
	 */
	private static boolean procArgs(String[] args) {
		if (args.length < 2) {
			System.out.println("ERROR: Wrong number of arguments.");
			return false;
		}

		if (!args[0].contains(":")) {
			System.out.println("ERROR: The first argument has an incorrect format.");
			return false;
		}

		switch (args.length) {
		case 2: 
			if (!args[1].equals("STATE")) {
				System.out.println("ERROR: '" + args[1] + "' is not a valid argument.");
				return false;
			}
			break;
		case 3: 
			if ((args[1].equals("RESTORE")) || (args[1].equals("DELETE")) || (args[1].equals("RECLAIM"))) {
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

		try {
			Registry registry = LocateRegistry.getRegistry("localhost");
			RMIInterface stub = (RMIInterface) registry.lookup("kappa");

			String str = stub.sayHello();
			System.out.println(str);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			printUsage();
			System.exit(-1);
		}
	}
}
