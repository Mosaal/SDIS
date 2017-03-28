package Channels;

import java.io.IOException;
import java.net.DatagramPacket;

import Utils.Utils;

public class MDBChannel extends MChannel {

	/**
	 * Creates a MDBChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MDBChannel(final String ipAddress, final int port) {
		super(ipAddress, port);
		mcastThread.start();
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
					byte[] data = packet.getData();
					String str = new String(data, 0, packet.getLength());
					
					// Parse string and its data
					if (str.contains(Utils.PUTCHUNK_STRING))
						messageQueue.get(Utils.PUTCHUNK_INT).add(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
