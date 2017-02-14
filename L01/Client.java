public class Client {
	
	// Args
	private static String hostname;
	private static String portNumber;
	private static String plateNumber;
	private static String ownerName;
	
	// Consts
	private static final byte HOST = 0;
	private static final byte PORT = 1;
	private static final byte OPER = 2;
	private static final byte PLATE = 3;
	private static final byte OWNER = 4;
	
	public static boolean procArgs(String[] args) {
		if (args.length != 4 && args.length != 5)
			return false;
		
		if (args[OPER].equals("register") && args.length == 5) {
			hostname = args[HOST];
			portNumber = args[PORT];
			plateNumber = args[PLATE];
			ownerName = args[OWNER];

			return true;
		} else if (args[OPER].equals("lookup") && args.length == 4) {
			hostname = args[HOST];
			portNumber = args[PORT];
			plateNumber = args[PLATE];
			
			return true;
		}
		
		return false;
	}
	
	public static void printUsage() {
		System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd>");
		System.out.println("Where:");
		System.out.println("\t<host_name> is the name of the host running the server");
		System.out.println("\t<port_number> is the server port");
		System.out.println("\t<oper> is either 'register' or 'lookup'");
		System.out.println("\nAnd where <opnd> is a list of arguments:");
		System.out.println("\t<plate number> <owner name> for register");
		System.out.println("\t<plate number> for lookup");
	}

	public static void main(String[] args) {
		if (!procArgs(args))
			printUsage();

		// send request
		// receive response
		// process response
		// tou a testar umas coisas
	}
	
}
