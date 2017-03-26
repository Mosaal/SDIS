package Channels;

public abstract class MChannel {

	// Instance variables
	private final int port;
	private final String ipAddress;

	/**
	 * Creates a MChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MChannel(final String ipAddress, final int port) {
		this.port = port;
		this.ipAddress = ipAddress;
	}

	// Instance methods
	/** Returns the port number */
	public final int getPort() { return port; }

	/** Returns the IP address */
	public final String getIPAddress() { return ipAddress; }
}
