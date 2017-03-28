package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

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
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean send(byte[] message) {
		try { dataSocket.send(new DatagramPacket(message, message.length, InetAddress.getByName(ipAddress), port)); }
		catch (IOException e) { return false; }
		return true;
	}
	
	Thread mcastThread = new Thread(new Runnable() {
		@Override
		public void run() {
			System.out.println("I started on the mdb channel...");
			while (true) {
				try {
					// Receive packet
					packet = new DatagramPacket(buf, Utils.BUFFER_MAX_SIZE);
					mcastSocket.receive(packet);
					
					// Get data and turn it into string
					byte[] data = packet.getData();
					String str = new String(data, 0, packet.getLength());
					
					System.out.println(str);
					// Process string and its data
					if (str.contains(Utils.PUTCHUNK_STRING)) {
						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
