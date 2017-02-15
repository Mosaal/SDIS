import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {
	
	// Args
	private static String reply;
	private static int portNumber;
	private static HashMap<String, String> dataBase;
	
	// Const
	private static final byte PORT = 0;
	private static final int MAX_SIZE = 256;
	
	public static void printUsage() {
		System.out.println("java Server <port_number>");
		System.out.println("Where:");
		System.out.println("\t<port_number> is the port number on which the server waits for requests");
	}
	
	public static void loadDataBase() {
		try {
			String line = null;
			dataBase = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader("res/db.txt"));
			
			while ((line = reader.readLine()) != null)
				dataBase.put(line.split(":")[0], line.split(":")[1]);
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void parseReceivedData(String data) {
		String[] temp = data.split(":");
		
		if (temp[0].equals("REGISTER")) {
			dataBase.put(temp[1], temp[2]);
			reply = "" + dataBase.size();
			return;
		} else if (temp[0].equals("LOOKUP")) {
			if (dataBase.get(temp[1]) != null) { // TODO
				reply = temp[1] + dataBase.get(temp[1]);
				return;
			}
		}
		
		reply = "-1";
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1)
			printUsage();

		loadDataBase();
		byte[] rBuf = new byte[MAX_SIZE];
		byte[] sBuf = new byte[MAX_SIZE];
		
		// Open socket
		portNumber = Integer.parseInt(args[PORT]);
		DatagramSocket socket = new DatagramSocket(portNumber);

		while (true) {
			// Create packet to store receive data
			DatagramPacket rPacket = new DatagramPacket(rBuf, rBuf.length);
			socket.receive(rPacket);
			
			// Process received data
			String rDataString = new String(rPacket.getData());
			System.out.println("REQUEST: " + rDataString);
			parseReceivedData(rDataString);
			
			// Send a response
			sBuf = reply.getBytes();
			int packetPort = rPacket.getPort();
			InetAddress address = rPacket.getAddress();
			DatagramPacket sPacket = new DatagramPacket(sBuf, sBuf.length, address, packetPort);
			socket.send(sPacket);
		}
	}
	
}
