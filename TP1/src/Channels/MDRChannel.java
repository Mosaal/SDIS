package Channels;

import java.io.IOException;
import java.net.DatagramPacket;

import Utils.Utils;

public class MDRChannel extends MChannel {

	/**
	 * Creates a MDRChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MDRChannel(final String ipAddress, final int port) {
		super(ipAddress, port);
		mcastThread.start();
	}
	
	Thread mcastThread = new Thread(new Runnable() {
		@Override
		public void run() {
			System.out.println("I started on the mdr channel...");
			while (true) {
				try {
					// Receive packet
					packet = new DatagramPacket(buf, Utils.BUFFER_MAX_SIZE);
					mcastSocket.receive(packet);
					
					// Get data and turn it into string
					byte[] data = packet.getData();
					String str = new String(data, 0, packet.getLength());
					
					// Process string and its data
					if (str.contains(Utils.CHUNK_STRING)) {
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
