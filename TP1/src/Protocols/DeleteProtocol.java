package Protocols;

import Channels.MCChannel;

public class DeleteProtocol extends Protocol {

	/**
	 * Creates a DeleteProtocol instance
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public DeleteProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
	}
	
	// Instance methods
	public boolean deleteFile(String fileName) {
		return true;
	}
}
