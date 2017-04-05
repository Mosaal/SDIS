package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;

import Utils.Utils;

public class MDBChannel extends MChannel {

	// Instance variables
	private LinkedList<String> messageQueue;

	/**
	 * Creates a MDBChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MDBChannel(String ipAddress, int port) {
		super(ipAddress, port);
		messageQueue = new LinkedList<String>();
		mcastThread.start();
	}

	// Instance methods
	/** Returns a message from the head of the queue */
	public synchronized String receive() {
		return (messageQueue.size() > 0) ? messageQueue.removeFirst() : null;
	}

	/** Thread that is constantly listening for PUTCHUNK type messages */
	Thread mcastThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					// Receive packet
					packet = new DatagramPacket(buf, Utils.BUFFER_MAX_SIZE);
					mcastSocket.receive(packet);

					// Get data and turn it into string
					String str = new String(packet.getData(), 0, packet.getLength());

					// Parse string and its data
					if (str.contains(Utils.PUTCHUNK_STRING))
						messageQueue.add(str);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
