import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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

	@SuppressWarnings("resource")
	public static void main(String[] args) throws NumberFormatException, IOException {
		if (args.length != 3) {
			printUsage();
			return;
		}

		loadDataBase();

		// Open mCast socket
		MulticastSocket mSocket = new MulticastSocket();
		InetAddress mAddress = InetAddress.getByName(args[MCAST_ADDR]);
		mSocket.setTimeToLive(1);

		// Open server socket
		DatagramSocket sSocket = new DatagramSocket(Integer.parseInt(args[SRVC_PORT]));

		// Buffer to receive incoming data
		byte[] buf = new byte[MAX_SIZE];
		DatagramPacket incoming = new DatagramPacket(buf, buf.length);

		// Thread for advertisement
		(new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						// Create ad and send it
						int mPort = Integer.parseInt(args[MCAST_PORT]);
						DatagramPacket packet = new DatagramPacket(args[SRVC_PORT].getBytes(), args[SRVC_PORT].getBytes().length, mAddress, mPort);
						mSocket.send(packet);
						
						// Wait for 1 second
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		})).start();

		// Thread for request
		(new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						// Receive data
						sSocket.receive(incoming);
						byte[] data = incoming.getData();
						String temp = new String(data, 0, incoming.getLength());

						// Print out details of request
						System.out.println("RECEIVED REQUEST: " + temp);
						String reply = parseReceivedData(temp);

						// Send reply
						System.out.println("SENT REPLY: " + reply);
						DatagramPacket output = new DatagramPacket(reply.getBytes(), reply.getBytes().length, incoming.getAddress(), incoming.getPort());
						sSocket.send(output);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		})).start();
	}
}