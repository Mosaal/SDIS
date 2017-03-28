package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
	
	public boolean send(byte[] chunk){
		try {
			DatagramPacket temp = new DatagramPacket(chunk, chunk.length, InetAddress.getByName(ipAddress), port);
			dataSocket.send(temp);
		} catch (IOException e) {
			return false;
		}
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
					
					// Process string and its data
					if (str.contains(Utils.PUTCHUNK_STRING)) {
						String[] temp = str.split(" ");
						String temp1= temp[5].substring(9);

						//Save data
						
						// Send STORED message
						System.out.println("STORED " + temp[0] + temp[1] + temp[2] + temp[3] + temp[5].substring(0,8));
						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
}
