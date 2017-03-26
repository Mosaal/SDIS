package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Utils.Utils;

public abstract class MChannel {

	// Instance variables
	private byte[] data;
	private final int port;
	private final String ipAddress;
	
	// Multicast variables
	private Thread mcastThread;
	private DatagramPacket packet;
	private DatagramSocket dataSocket;
	private MulticastSocket mcastSocket;

	/**
	 * Creates a MChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MChannel(final String ipAddress, final int port) {
		this.port = port;
		this.ipAddress = ipAddress;
		data = new byte[Utils.BUFFER_MAX_SIZE];
		
		try {
			dataSocket = new DatagramSocket();
			packet = new DatagramPacket(data, Utils.BUFFER_MAX_SIZE);
			
			mcastSocket = new MulticastSocket(port);
			mcastSocket.joinGroup(InetAddress.getByName(ipAddress));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
