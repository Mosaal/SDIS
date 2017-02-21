import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Server {

	// Args
	private static HashMap<String, String> dataBase;
	
	// Const
	private static final byte SRVC_PORT = 0;
	private static final byte MCAST_ADDR = 1;
	private static final byte MCAST_PORT = 2;
	private static final int MAX_SIZE = 1024;

	public static void printUsage() {
		System.out.println("java Server <srvc_port> <mcast_addr> <mcast_port>");
		System.out.println("Where:");
		System.out.println("\t<srvc_port> is the port number where the server provides the service");
		System.out.println("\t<mcast_addr> is the IP address of the multicast group used by the server to advertise its service");
		System.out.println("\t<mcast_port> is the multicast group port number used by the server to advertise its service");
	}
	
	public static void loadDataBase() {
		try {
			String line = null;
			dataBase = new HashMap<String, String>();
			BufferedReader reader = new BufferedReader(new FileReader("db.txt"));

			while ((line = reader.readLine()) != null)
				dataBase.put(line.split(":")[0], line.split(":")[1]);

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(String plate, String owner) {
		try (FileWriter fw = new FileWriter("db.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw)) {
			out.print("\n" + plate + ":" + owner);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String parseReceivedData(String data) {
		String[] temp = data.split(":");

		if (temp[0].equals("REGISTER")) {
			if (!dataBase.containsKey(temp[1])) {
				dataBase.put(temp[1], temp[2]);
				writeToFile(temp[1], temp[2]);
				return String.valueOf(dataBase.size());
			}
		} else if (temp[0].equals("LOOKUP")) {
			if (dataBase.containsKey(temp[1])) {
				return temp[1] + " -> " + dataBase.get(temp[1]);
			}
		}

		return "ERROR";
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		//if (args.length != 3) {
			//printUsage();
			//return;
		//}
		
		// loadDataBase();
		
		InetAddress address = InetAddress.getByName("228.5.6.7");
		MulticastSocket socket = new MulticastSocket(Integer.parseInt("6789"));
		socket.joinGroup(address);
		
		while (true) {
			byte[] buf = new byte[MAX_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			
			String data = new String(packet.getData(), 0, packet.getLength());
			System.out.println("RECEIVED: " + data);
			if (data.equals("quit")) break;
		}
		
		socket.leaveGroup(address);
		socket.close();
	}
}