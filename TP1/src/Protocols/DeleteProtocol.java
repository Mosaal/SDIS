package Protocols;

import Channels.MCChannel;
import Utils.Utils;

public class DeleteProtocol extends Protocol {

	/**
	 * Creates a DeleteProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public DeleteProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
		processDelete.start();
	}
	
	// Instance methods
	public boolean deleteFile(String fileName) {
		return true;
	}
	
	/** Thread that is constantly processing DELETE type messages */
	Thread processDelete = new Thread(new Runnable() {
		@Override
		public void run() {
			// Receive data if its there to be received
			String str = null;
			do { str = mcChannel.receive(Utils.DELETE_INT); }
			while (str == null);
			
			// Split it
			String[] args = str.split(" ");
			
			// Check who it belongs to
			if (Integer.parseInt(args[2]) != peerID) {
				
			}
		}
	});
}
