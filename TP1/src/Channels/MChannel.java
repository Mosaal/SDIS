package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Utils.Utils;

public abstract class MChannel {

	// Instance variables
	protected int port;
	protected byte[] buf;
	protected String ipAddress;
	
	// Multicast variables
	protected DatagramPacket packet;
	protected DatagramSocket dataSocket;
	protected MulticastSocket mcastSocket;

	/**
	 * Creates a MChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MChannel(String ipAddress, int port) {
		this.port = port;
		this.ipAddress = ipAddress;
		buf = new byte[Utils.BUFFER_MAX_SIZE];
		
		try {
			dataSocket = new DatagramSocket();
			packet = new DatagramPacket(buf, Utils.BUFFER_MAX_SIZE);
			
			mcastSocket = new MulticastSocket(port);
			mcastSocket.joinGroup(InetAddress.getByName(ipAddress));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Instance methods
	/** Returns the buffer used in the packet */
	public byte[] getBuffer() { return buf; }
	
	/** Returns the port number the multicast socket is on */
	public int getPort() { return port; }
	
	/** Returns the IP address */
	public String getAddress() { return ipAddress; }
	
	/** Returns the data packet */
	public DatagramPacket getPacket() { return packet; }
	
	/** Returns the data socket */
	public DatagramSocket getDataSocket() { return dataSocket; }
	
	/** Returns the multicast socket */
	public MulticastSocket getMCastSocket() { return mcastSocket; }
	
	/**
	 * Sends a given message to the corresponding channel
	 * @param message message to be sent
	 */
	public boolean send(byte[] message) {
		try { dataSocket.send(new DatagramPacket(message, message.length, InetAddress.getByName(ipAddress), port)); }
		catch (IOException e) { return false; }
		return true;
	}
}
