import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	
	// Consts
	private static final byte HOST = 0;
	private static final byte PORT = 1;
	private static final byte OPER = 2;
	private static final byte PLATE = 3;
	private static final byte OWNER = 4;
	private static final int MAX_SIZE = 1024;
	
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
		if (args.length != 3 && args.length != 4 && args.length != 5)
			return false;
		
		if (args[OPER].equals("register") && args.length == 5)
			return true;
		else if (args[OPER].equals("lookup") && args.length == 4)
			return true;
		else if (args[OPER].equals("quit") && args.length == 3)
			return true;
		
		return false;
	}

	public static void main(String[] args) throws IOException {
		if (!procArgs(args)) {
			printUsage();
			return;
		}
		
		// Creste request
		String request = null;
		if (args[OPER].equals("register"))
			request = "REGISTER:" + args[PLATE] + ":" + args[OWNER];
		else if (args[OPER].equals("lookup"))
			request = "LOOKUP:" + args[PLATE];
		else if (args[OPER].equals("quit"))
			request = "QUIT";
		
		// Create socket and get address
		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName(args[HOST]);
		
		// Send request
		DatagramPacket output = new DatagramPacket(request.getBytes(), request.getBytes().length, address, Integer.parseInt(args[PORT]));
		System.out.println("SENT REQUEST: " + request);
		socket.send(output);
		
		if (!args[OPER].equals("quit")) {
			// Receive reply
			byte[] buf = new byte[MAX_SIZE];
			DatagramPacket incoming = new DatagramPacket(buf, buf.length);
			socket.receive(incoming);
			
			// Print out details of reply
			String temp = new String(buf, 0, incoming.getLength());
			System.out.println("RECEIVED REPLY: " + temp);
		}
		
		socket.close();
	}
	
}
