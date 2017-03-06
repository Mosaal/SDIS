import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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

	public static void main(String[] args) throws IOException {
		if (!procArgs(args)) {
			printUsage();
			return;
		}

		// Create request
		String request = null;
		if (args[OPER].equals("register"))
			request = "REGISTER:" + args[PLATE] + ":" + args[OWNER];
		else if (args[OPER].equals("lookup"))
			request = "LOOKUP:" + args[PLATE];

		// Get address and open socket
		InetAddress mAddress = InetAddress.getByName(args[ADDR]);
		MulticastSocket mSocket = new MulticastSocket(Integer.parseInt(args[PORT]));
		mSocket.joinGroup(mAddress);

		// Receive port from Server
		byte[] buf = new byte[MAX_SIZE];
		DatagramPacket incoming = new DatagramPacket(buf, buf.length);
		mSocket.receive(incoming);

		// Parse information
		String portData = new String(incoming.getData(), 0, incoming.getLength());
		System.out.println("RECEIVED PORT: " + portData);

		// Send out request
		DatagramSocket sSocket = new DatagramSocket();
		DatagramPacket output = new DatagramPacket(request.getBytes(), request.getBytes().length, incoming.getAddress(), Integer.parseInt(portData));
		System.out.println("SENT REQUEST: " + request);
		sSocket.send(output);

		// Receive reply
		buf = new byte[MAX_SIZE];
		DatagramPacket reply = new DatagramPacket(buf, buf.length);
		sSocket.receive(reply);

		// Print out details of reply
		String temp = new String(buf, 0, reply.getLength());
		System.out.println("RECEIVED REPLY: " + temp);
		
		// Close sockets
		mSocket.close();
		sSocket.close();
	}
}