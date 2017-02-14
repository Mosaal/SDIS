public class Server {
	
	// Arg
	private static String portNumber;
	
	// Const
	private static final byte PORT = 0;
	
	public static void printUsage() {
		System.out.println("java Server <port_number>");
		System.out.println("Where:");
		System.out.println("\t<port_number> is the port number on which the server waits for requests");
	}

	public static void main(String[] args) {
		if (args.length != 1)
			printUsage();
		else
			portNumber = args[PORT];

		while (true) {
			// receive request
			// process request
			// respond
		}
	}
	
}
