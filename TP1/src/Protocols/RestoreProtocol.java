package Protocols;

import Channels.MCChannel;
import Channels.MDRChannel;
import Utils.Utils;

public class RestoreProtocol extends Protocol {

	// Instance variables
	private MDRChannel mdrChannel;

	/**
	 * Creates a RestoreProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 * @param mdrChannel multicast data recovery channel this protocol subscribes to
	 */
	public RestoreProtocol(String proVer, int peerID, MCChannel mcChannel, MDRChannel mdrChannel) {
		super(proVer, peerID, mcChannel);
		this.mdrChannel = mdrChannel;

		processChunk.start();
		processGetchunk.start();
	}

	// Instance methods
	/** Returns the multicast data recovery channel */
	public MDRChannel getMDRChannel() { return mdrChannel; }

	public boolean restoreFile(String fileName) {
		return true;
	}

	/** Thread that is constantly processing GETCHUNK type messages */
	Thread processChunk = new Thread(new Runnable() {
		@Override
		public void run() {
			// Receive data if its there to be received
			String str = null;
			do { str = mcChannel.receive(Utils.GETCHUNK_INT); }
			while (str == null);

			// Split it
			String[] args = str.split(" ");

			// Check who it belongs to
			if (Integer.parseInt(args[2]) != peerID) {

			}
		}
	});

	/** Thread that is constantly processing CHUNK type messages */
	Thread processGetchunk = new Thread(new Runnable() {
		@Override
		public void run() {
			// Receive data if its there to be received
			String str = null;
			do { str = mdrChannel.receive(); }
			while (str == null);
			
			// Split it
			String[] args = str.split(" ");
			
			// Check who it belongs to
			if (Integer.parseInt(args[2]) != peerID) {
				
			}
		}
	});
}
