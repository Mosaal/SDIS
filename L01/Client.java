import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	
	// Args
	private static int portNumber;
	private static String hostname;
	private static String plateNumber;
	private static String ownerName;
	
	// Consts
	private static final byte HOST = 0;
	private static final byte PORT = 1;
	private static final byte OPER = 2;
	private static final byte PLATE = 3;
	private static final byte OWNER = 4;
	private static final int MAX_SIZE = 256;
	
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
	
	public static boolean procArgs(String[] args) {
		if (args.length != 4 && args.length != 5)
			return false;
		
		if (args[OPER].equals("register") && args.length == 5) {
			hostname = args[HOST];
			portNumber = Integer.parseInt(args[PORT]);
			plateNumber = args[PLATE];
			ownerName = args[OWNER];

			return true;
		} else if (args[OPER].equals("lookup") && args.length == 4) {
			hostname = args[HOST];
			portNumber = Integer.parseInt(args[PORT]);
			plateNumber = args[PLATE];
			
			return true;
		}
		
		return false;
	}

	public static void main(String[] args) throws IOException {
		if (!procArgs(args))
			printUsage();
		
		byte[] rBuf = new byte[MAX_SIZE];
		byte[] sBuf = new byte[MAX_SIZE];
		
		String request = null;
		if (args[OPER].equals("register"))
			request = "REGISTER:" + plateNumber + ":" + ownerName;
		else if (args[OPER].equals("lookup"))
			request = "LOOKUP:" + plateNumber;
		
		sBuf = request.getBytes();
		
		// Open socket, get address and create packet to store sent data
		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName(hostname);
		DatagramPacket sPacket = new DatagramPacket(sBuf, sBuf.length, address, portNumber);
		socket.send(sPacket);
		
		// Create packet to store received data
		DatagramPacket rPacket = new DatagramPacket(rBuf, rBuf.length);
		socket.receive(rPacket);
		
		// Process response
		String response = new String(rPacket.getData());
		System.out.println("REPLY: " + response);
		socket.close();
	}
	
}
