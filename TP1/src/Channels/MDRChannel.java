package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;

import Utils.Utils;

public class MDRChannel extends MChannel {

	// Instance variables
	private LinkedList<byte[]> messageQueue;

	/**
	 * Creates a MDRChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MDRChannel(String ipAddress, int port) {
		super(ipAddress, port);
		messageQueue = new LinkedList<byte[]>();
		mcastThread.start();
	}

	// Instance methods
	/** Returns a message from the head of the queue */
	public synchronized byte[] receive() {
		return (messageQueue.size() > 0) ? messageQueue.removeFirst() : null;
	}

	/** Thread that is constantly listening for CHUNK type messages */
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
					if (str.contains(Utils.CHUNK_STRING))
						messageQueue.add(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
