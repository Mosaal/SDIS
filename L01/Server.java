import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class Server {

	// Args
	private static HashMap<String, String> dataBase;

	// Const
	private static final byte PORT = 0;
	private static final int MAX_SIZE = 1024;

	public static void printUsage() {
		System.out.println("java Server <port_number>");
		System.out.println("Where:");
		System.out.println("\t<port_number> is the port number on which the server waits for requests");
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

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			printUsage();
			return;
		}

		loadDataBase();

		// Create socket
		DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[PORT]));

		// Buffer to receive incoming data
		byte[] buf = new byte[MAX_SIZE];
		DatagramPacket incoming = new DatagramPacket(buf, buf.length);

		// Communication loop
		while (true) {
			// Receive data
			socket.receive(incoming);
			byte[] data = incoming.getData();
			String temp = new String(data, 0, incoming.getLength());

			// Print out details of request
			if (temp.equals("QUIT")) break;
			System.out.println("RECEIVED REQUEST: " + temp);
			String reply = parseReceivedData(temp);

			// Send reply
			System.out.println("SENT REPLY: " + reply);
			DatagramPacket output = new DatagramPacket(reply.getBytes(), reply.getBytes().length, incoming.getAddress(), incoming.getPort());
			socket.send(output);
		}
		
		socket.close();
	}

}
