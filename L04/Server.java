import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server implements Database {

    // Const
	private static final byte REMT = 0;

	// Args
	private static HashMap<String, String> dataBase;

    public static void printUsage() {
        System.out.println("Usage: java Server <remote_object_name>");
        System.out.println("Where:\n\t<remote_object_name> is the name the server bound the remote object to");
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
    
	@Override
	public String makeRequest(String request) {
		System.out.println("RECEIVED: " + request);
		String reply = parseReceivedData(request);
		System.out.println("SENT: " + reply);
		return reply;
	}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("ERROR: Wrong number of arguments.");
            printUsage();
            return;
        }

        loadDataBase();

		// Start server
		Server obj = new Server();
	    Database stub = (Database) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry();
	    registry.bind(args[REMT], stub);
    }
}