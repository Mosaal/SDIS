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
		if (args.length != 4 && args.length != 5)
			return false;
		
		if (args[OPER].equals("register") && args.length == 5)
			return true;
		else if (args[OPER].equals("lookup") && args.length == 4)
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
		
		// Create socket and get address
		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName(args[HOST]);
		
		// Send request
		DatagramPacket output = new DatagramPacket(request.getBytes(), request.getBytes().length, address, Integer.parseInt(args[PORT]));
		System.out.println("SENT REQUEST: " + request);
		socket.send(output);
		
		// Receive reply
		byte[] buf = new byte[MAX_SIZE];
		DatagramPacket incoming = new DatagramPacket(buf, buf.length);
		socket.receive(incoming);
		
		// Print out details of reply
		String temp = new String(buf, 0, incoming.getLength());
		System.out.println("RECEIVED REPLY: " + temp);
		socket.close();
		
		/*byte[] rBuf = new byte[MAX_SIZE];
		byte[] sBuf = new byte[MAX_SIZE];
		
		String request = null;
		if (args[OPER].equals("register"))
			request = "REGISTER:" + plateNumber + ":" + ownerName;
		else if (args[OPER].equals("lookup"))
			request = "LOOKUP:" + plateNumber;
		
		System.out.println("SENT REQUEST: " + request);
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
		System.out.println("RECEIVED REPLY: " + response);
		socket.close();*/
	}
	
}
