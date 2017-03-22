package Peers;

public class Peer {

	/** Prints the correct way to execute this class */
	public static void printUsage() {
		System.out.println("Usage: java -cp ./bin/ Peers.Peer <options>");
		System.out.println("Where:");
	}

	/**
	 * Processes the arguments passed on the command line terminal 
	 * @param args string array of arguments to be processed
	 */
	private static boolean procArgs(String[] args) {
		return true;
	}

	public static void main(String[] args) {
		if (!procArgs(args)) {
			printUsage();
			return;
		}
	}
}