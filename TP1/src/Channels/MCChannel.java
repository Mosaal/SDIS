package Channels;

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
		}
	});
}
