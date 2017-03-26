package Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import Channels.MCChannel;
import Channels.MDBChannel;
import Channels.MDRChannel;
import Protocols.BackupProtocol;
import Protocols.DeleteProtocol;
import Protocols.ReclaimProtocol;
import Protocols.RestoreProtocol;
import Protocols.StateProtocol;
import Utils.Utils;

public class PeerRunnable implements Runnable {

	// Instance variables
	private int tcpPort;
	private PrintWriter out;
	private BufferedReader in;
	
	// TCP sockets
	private Socket clientSocket;
	private ServerSocket serverSocket;
	
	// Multicast channels
	private MCChannel mcChannel;
	private MDBChannel mdbChannel;
	private MDRChannel mdrChannel;
	
	// Protocols
	private StateProtocol stateProtocol;
	private BackupProtocol backupProtocol;
	private DeleteProtocol deleteProtocol;
	private RestoreProtocol restoreProtocol;
	private ReclaimProtocol reclaimProtocol;
	
	/**
	 * Creates a PeerRunnable instance
	 * @param port port where the TCP server will open
	 * @param mc string array with the arguments for the control channel
	 * @param mdb string array with the arguments for the data backup channel
	 * @param mdr string array with the arguments for the data recovery channel
	 */
	public PeerRunnable(int tcpPort, String[] mc, String[] mdb, String[] mdr) {
		this.tcpPort = tcpPort;
		
		mcChannel = new MCChannel(mc[0], Integer.parseInt(mc[1]));
		mdbChannel = new MDBChannel(mdb[0], Integer.parseInt(mdb[1]));
		mdrChannel = new MDRChannel(mdr[0], Integer.parseInt(mdr[1]));
		
		stateProtocol = new StateProtocol();
		backupProtocol = new BackupProtocol();
		deleteProtocol = new DeleteProtocol();
		restoreProtocol = new RestoreProtocol();
		reclaimProtocol = new ReclaimProtocol();
	}
	
	/**
	 * Parses the string received as a request
	 * @param request string to be parsed
	 */
	private int parseRequest(String request) {
		if (request.contains(" ")) {
			String[] args = request.split(" ");
			
			if (args.length == 2) {
				if (args[0].equals(Utils.RESTORE_STRING))
					return 2;
				else if (args[0].equals(Utils.DELETE_STRING))
					return 4;
				else if (args[0].equals(Utils.RECLAIM_STRING))
					return 5;
			} else if (args.length == 3 && args[0].equals(Utils.BACKUP_STRING)) {
				return 3;
			}
		} else if (request.equals(Utils.STATE_STRING)) {
			return 1;
		}
		
		return 0;
	}
	
	@Override
	public void run() {
		// Initialize TCP server
		try {
			serverSocket = new ServerSocket(tcpPort);
		} catch (IOException e) {
			System.out.println("Failed to initalize TCP server.");
			System.exit(-1);
		}

		while (true) {
			try {
				// Wait for a connection
				System.out.println("\nWaiting for a Client...");
				
				clientSocket = serverSocket.accept();
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				System.out.println("A Client connected successfully.");
			} catch (IOException e) {
				System.out.println("Error on setup.");
				System.exit(-1);
			}
			
			try {
				// A connection was made. Wait and parse request
				String request = in.readLine();
				int res = parseRequest(request);
				
				// Call for the corresponding protocol
				switch (res) {
				case Utils.STATE_INT:
					// TODO
					break;
				case Utils.RESTORE_INT:
					// TODO
					break;
				case Utils.BACKUP_INT:
					// TODO
					break;
				case Utils.DELETE_INT:
					// TODO
					break;
				case Utils.RECLAIM_INT:
					// TODO
					break;
				default:
					System.out.println("Invalid request received. Cancelling connection...");
					out.println("ERROR");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
