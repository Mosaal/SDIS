import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

	// Const
	private static final byte PORT = 0;

	// Args
	private static HashMap<String, String> dataBase;

	public static void printUsage() {
		System.out.println("java Server <srvc_port>");
		System.out.println("Where:");
		System.out.println("\t<srvc_port> is the port number where the server provides the service");
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
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			printUsage();
			return;
		}

		loadDataBase();

		// Create socket
		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[PORT]));

		while (true) {
			// Accept client connection
			Socket clientSocket = serverSocket.accept();

			// Create writer and reader
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// Print out details of request
			String temp = in.readLine();
			System.out.println("RECEIVED REQUEST: " + temp);
			String reply = parseReceivedData(temp);

			// Send reply
			System.out.println("SENT REPLY: " + reply);
			out.println(reply);
		}
	}
}
