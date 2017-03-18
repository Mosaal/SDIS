import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    // Const
	private static final byte HOST = 0;
	private static final byte REMT = 1;
	private static final byte OPER = 2;
	private static final byte PLATE = 3;
	private static final byte OWNER = 4;

    public static void printUsage() {
		System.out.println("java Client <host_name> <remote_object_name> <oper> <opnd>");
		System.out.println("Where:");
		System.out.println("\t<host_name> is the name of the host where the server runs");
		System.out.println("\t<remote_object_name> is the name the server bound the remote object to");
		System.out.println("\t<oper> is 'register' or 'lookup', depending on the operation to invoke");
		System.out.println("\nAnd where <opnd> is a list of arguments:");
		System.out.println("\t<plate number> <owner name> for register");
		System.out.println("\t<plate number> for lookup");
	}

    public static boolean procArgs(String[] args) {
		if (args.length != 4 && args.length != 5)
			return false;

		if (args[OPER].equals("register") && args.length == 5)
			return true;
		else if (args[OPER].equals("lookup") && args.length == 4)
			return true;

		return false;
	}

    public static void main(String[] args) throws Exception {
        if (!procArgs(args)) {
            System.out.println("ERROR: Wrong number of arguments.");
            printUsage();
			return;
        }

        // Create request
		String request = null;
		if (args[OPER].equals("register"))
			request = "REGISTER:" + args[PLATE] + ":" + args[OWNER];
		else if (args[OPER].equals("lookup"))
			request = "LOOKUP:" + args[PLATE];
		
		// Locate registry
		Registry reg = LocateRegistry.getRegistry(args[HOST]);
		Database stub = (Database) reg.lookup(args[REMT]);

		// Send request and receive response
		String reply = stub.makeRequest(request);
		System.out.println("SENT: " + request);
		System.out.println("RECEIVED: " + reply);
    }
}