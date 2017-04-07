package Peer;

import java.io.BufferedReader;
import java.io.File;
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
import Utils.*;

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
	public PeerRunnable(String proVer, int peerID, int tcpPort, String[] mc, String[] mdb, String[] mdr) {
		this.tcpPort = tcpPort;

		mcChannel = new MCChannel(mc[0], Integer.parseInt(mc[1]));
		mdbChannel = new MDBChannel(mdb[0], Integer.parseInt(mdb[1]));
		mdrChannel = new MDRChannel(mdr[0], Integer.parseInt(mdr[1]));

		stateProtocol = new StateProtocol(proVer, peerID, mcChannel);
		deleteProtocol = new DeleteProtocol(proVer, peerID, mcChannel);
		backupProtocol = new BackupProtocol(proVer, peerID, mcChannel, mdbChannel);
		restoreProtocol = new RestoreProtocol(proVer, peerID, mcChannel, mdrChannel);
		reclaimProtocol = new ReclaimProtocol(proVer, peerID, mcChannel, backupProtocol);
	}

	/**
	 * Parses the string received as a request
	 * @param request string to be parsed
	 */
	private String[] parseRequest(String request) {
		if (request.contains(" ")) {
			String[] args = request.split(" ");

			if (args.length == 2) {
				if (args[0].equals(Utils.RESTORE_STRING))
					return new String[] { "2", args[1] };
				else if (args[0].equals(Utils.DELETE_STRING))
					return new String[] { "4", args[1] };
				else if (args[0].equals(Utils.RECLAIM_STRING))
					return new String[] { "5", args[1] };
			} else if (args.length == 3 && args[0].equals(Utils.BACKUP_STRING)) {
				return new String[] { "3", args[1], args[2] };
			}
		} else if (request.equals(Utils.STATE_STRING)) {
			return new String[] { "1" };
		}

		return null;
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
				System.out.println("Waiting for a Client...");

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
				String[] res = parseRequest(request);

				// Check if result is null
				if (res == null) {
					System.out.println("Invalid request received. Closing connection...\n");
					out.println(Utils.ERROR_MESSAGE);
					continue;
				}

				// If backup check if file exists
				if (Integer.parseInt(res[0]) == Utils.BACKUP_INT) {
					File file = new File(res[1]);					
					if (!file.exists() || !file.isFile()) {
						System.out.println("The file '" + res[1] + "' does not exist or it is a directory. Closing connection...");
						out.println(Utils.ERROR_MESSAGE);
						continue;
					}
				}

				// Call for the corresponding protocol
				String reply = null;
				switch (Integer.parseInt(res[0])) {
				case Utils.STATE_INT:
					reply = stateProtocol.getState();
					break;
				case Utils.RESTORE_INT:
					reply = restoreProtocol.restoreFile(res[1]);
					break;
				case Utils.BACKUP_INT:
					reply = backupProtocol.backupFile(res[1], Integer.parseInt(res[2]));
					break;
				case Utils.DELETE_INT:
					reply = deleteProtocol.deleteFile(res[1]);
					break;
				case Utils.RECLAIM_INT:
					reply = reclaimProtocol.reclaimSpace(Integer.parseInt(res[1]));
					break;
				}

				// Send a reply confirming what happened
				if (reply.equals(Utils.SUCCESS_MESSAGE)) {
					System.out.println("The request was processed successfully. Closing connection...");
					out.println(Utils.SUCCESS_MESSAGE + "\n" + Utils.END_MESSAGE);
				} else if (reply.equals(Utils.ERROR_MESSAGE)) {
					System.out.println("There was an error processing the request. Closing connection...");
					out.println(Utils.ERROR_MESSAGE + "\n" + Utils.END_MESSAGE);
				} else {
					System.out.println("The request was processed successfully. Closing connection...");
					out.println(reply);
					out.println(Utils.END_MESSAGE);
				}
			} catch (IOException e) {
				System.out.println("Failed to read request from the Client.");
				System.exit(-1);
			}
		}
	}
}
