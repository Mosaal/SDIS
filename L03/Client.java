import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	// Const
	private static final byte HOST = 0;
	private static final byte PORT = 1;
	private static final byte OPER = 2;
	private static final byte PLATE = 3;
	private static final byte OWNER = 4;

	public static void printUsage() {
		System.out.println("java Client <host_name> <port_number> <oper> <opnd>");
		System.out.println("Where:");
		System.out.println("\t<host_name> is the name of the host where the server runs");
		System.out.println("\t<port_number> is the port number where the server provides the service");
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
		
		// Create socket and writer and send request
		Socket clientSocket = new Socket(args[HOST], Integer.parseInt(args[PORT]));
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		System.out.println("SENT REQUEST: " + request);
		out.println(request);
		
		// Receive response
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		System.out.println("RECEIVED REPLY: " + in.readLine());
		
		// Close everything
		in.close();
		out.close();
		clientSocket.close();
	}
}