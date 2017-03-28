package Protocols;

import Channels.MCChannel;

public class DeleteProtocol extends Protocol {

	/**
	 * Creates a DeleteProtocol instance
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public DeleteProtocol(MCChannel mcChannel) {
		super(mcChannel);
	}
	
	// Instance methods
	public boolean deleteFile(String fileName) {
		return true;
	}
}
