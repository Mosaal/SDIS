package Channels;

import java.io.IOException;
import java.net.DatagramPacket;

import Utils.Utils;

public class MCChannel extends MChannel {
	
	/**
	 * Creates a MCChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MCChannel(final String ipAddress, final int port) {
		super(ipAddress, port);
		mcastThread.start();
	}
	
	Thread mcastThread = new Thread(new Runnable() {
		@Override
		public void run() {
			System.out.println("I started on the mc channel...");
			
			while (true) {
				try {
					// Receive packet
					packet = new DatagramPacket(buf, Utils.BUFFER_MAX_SIZE);
					mcastSocket.receive(packet);
					
					// Get data and turn it into string
					byte[] data = packet.getData();
					String str = new String(data, 0, packet.getLength());
					
					// Process string and its data
					if (str.contains(Utils.STORED_STRING)) {
						
					} else if (str.contains(Utils.GETCHUNK_STRING)) {
						
					} else if (str.contains(Utils.DELETE_STRING)) {
						
					} else if (str.contains(Utils.REMOVED_STRING)) {
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
