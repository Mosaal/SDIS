package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.LinkedList;

import Utils.Utils;

public class MCChannel extends MChannel {

	// Instance variables
	private HashMap<Integer, LinkedList<String>> messageQueue;

	/**
	 * Creates a MCChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MCChannel(String ipAddress, int port) {
		super(ipAddress, port);

		messageQueue = new HashMap<Integer, LinkedList<String>>();
		messageQueue.put(Utils.STORED_INT, new LinkedList<String>());
		messageQueue.put(Utils.GETCHUNK_INT, new LinkedList<String>());
		messageQueue.put(Utils.DELETE_INT, new LinkedList<String>());
		messageQueue.put(Utils.REMOVED_INT, new LinkedList<String>());

		mcastThread.start();
	}

	// Instance methods
	/**
	 * Returns a message from the head of the queue depending on the protocol
	 * @param protocol protocol trying to retrieve a message
	 */
	public synchronized String receive(int protocol) {
		return (messageQueue.get(protocol).size() > 0) ? messageQueue.get(protocol).removeFirst() : null;
	}

	/** Thread that is constantly listening for STORED, GETCHUNK, DELETE and REMOVED type messages */
	Thread mcastThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					// Receive packet
					DatagramPacket packet = new DatagramPacket(new byte[Utils.BUFFER_MAX_SIZE], Utils.BUFFER_MAX_SIZE);
					mcastSocket.receive(packet);
					
					// Get data
					byte[] data = new byte[packet.getLength()];
					System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());

					// Turn it into string
					String str = new String(data, 0, data.length);

					// Add it to the queue
					if (str.contains(Utils.STORED_STRING))
						messageQueue.get(Utils.STORED_INT).add(str);
					else if (str.contains(Utils.GETCHUNK_STRING))
						messageQueue.get(Utils.GETCHUNK_INT).add(str);
					else if (str.contains(Utils.DELETE_STRING))
						messageQueue.get(Utils.DELETE_INT).add(str);
					else if (str.contains(Utils.REMOVED_STRING))
						messageQueue.get(Utils.REMOVED_INT).add(str);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
