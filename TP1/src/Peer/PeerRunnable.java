package Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//import Channels.MCChannel;
//import Channels.MDBChannel;
//import Channels.MDRChannel;

public class PeerRunnable implements Runnable {

	// Instance variables
	private int tcpPort;
	private PrintWriter out;
	private BufferedReader in;
	
	// TCP sockets
	private Socket clientSocket;
	private ServerSocket serverSocket;
	
	// Multicast channels
//	private MCChannel mcChannel;
//	private MDBChannel mdbChannel;
//	private MDRChannel mdrChannel;
	
	/**
	 * Creates a PeerRunnable instance
	 * @param port port where the TCP server will open
	 * @param mc string array with the arguments for the control channel
	 * @param mdb string array with the arguments for the data backup channel
	 * @param mdr string array with the arguments for the data recovery channel
	 */
	public PeerRunnable(int tcpPort, String[] mc, String[] mdb, String[] mdr) {
		this.tcpPort = tcpPort;
//		this.mcChannel = new MCChannel(mc[0], Integer.parseInt(mc[1]));
//		this.mdbChannel = new MDBChannel(mdb[0], Integer.parseInt(mdb[1]));
//		this.mdrChannel = new MDRChannel(mdr[0], Integer.parseInt(mdr[1]));
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
				// A connection was made. Wait for request
				String request = in.readLine();
				
				// Parse request
				if (request.contains(" ")) {
					System.out.println("Request received. Processing...");
					out.println("OK");
				} else {
					System.out.println("Invalid request received. Cancelling connection...");
					out.print("ERROR");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
