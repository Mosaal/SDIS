package Channels;

public class MDRChannel extends MChannel {

	/**
	 * Creates a MDRChannel instance
	 * @param ipAddress IP address for the multicast socket
	 * @param port port number for the multicast socket
	 */
	public MDRChannel(final String ipAddress, final int port) {
		super(ipAddress, port);
	}
}
