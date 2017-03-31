package Protocols;

import Channels.MCChannel;
import Utils.Utils;

public class ReclaimProtocol extends Protocol {

	/**
	 * Creates a ReclaimProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public ReclaimProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
		// processReclaim.start();
	}

	// Instance methods
	public boolean reclaimSpace(int spaceToReclaim) {
		return true;
	}

	/** Thread that is constantly processing REMOVED type messages */
	Thread processReclaim = new Thread(new Runnable() {
		@Override
		public void run() {
			// Receive data if its there to be received
			String str = null;
			do { str = mcChannel.receive(Utils.REMOVED_INT); }
			while (str == null);

			// Split it
			String[] args = str.split(" ");

			// Check who it belongs to
			if (Integer.parseInt(args[2]) != peerID) {

			}
		}
	});
}
