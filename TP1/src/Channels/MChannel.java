package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class MChannel {

	// Instance variables
	protected int port;
	protected InetAddress mcastAddress;
	protected MulticastSocket mcastSocket;

	/**
	 * Creates a MChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MChannel(String ipAddress, int port) {
		this.port = port;

		try {
			// Initialize address
			mcastAddress = InetAddress.getByName(ipAddress);

			// Initialize multicast socket
			mcastSocket = new MulticastSocket(port);
			mcastSocket.setTimeToLive(1);
			mcastSocket.joinGroup(mcastAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Instance methods
	/** Returns the port number the multicast socket is on */
	public int getPort() { return port; }

	/** Returns the address of the corresponding socket */
	public InetAddress getMCastAddress() { return mcastAddress; }

	/** Returns the multicast socket */
	public MulticastSocket getMCastSocket() { return mcastSocket; }

	/**
	 * Sends a given message to the corresponding channel
	 * @param message message to be sent
	 */
	public boolean send(byte[] message) {
		try { mcastSocket.send(new DatagramPacket(message, message.length, mcastAddress, port)); }
		catch (IOException e) { return false; }
		return true;
	}
}
