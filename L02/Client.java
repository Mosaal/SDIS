
public class Client {
	
	// Const
	private static final byte ADDR = 0;
	private static final byte PORT = 1;
	private static final byte OPER = 2;
	private static final byte PLATE = 3;
	private static final byte OWNER = 4;
	private static final int MAX_SIZE = 1024;
	
	public static void printUsage() {
		System.out.println("Usage: java client <mcast_addr> <mcast_port> <oper> <opnd>");
		System.out.println("Where:");
		System.out.println("\t<mcast_addr> is the IP address of the multicast group used by the server to advertise its service");
		System.out.println("\t<mcast_port> is the port number of the multicast group used by the server to advertise its service");
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
	
	public static void main(String[] args) {
		if (!procArgs(args)) {
			printUsage();
			return;
		}
		
		
	}
}