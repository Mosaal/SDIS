package Protocols;

import Channels.MCChannel;

public class ReclaimProtocol extends Protocol {

	/**
	 * Creates a ReclaimProtocol instance
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public ReclaimProtocol(MCChannel mcChannel) {
		super(mcChannel);
	}
	
	// Instance methods
	public boolean reclaimSpace(int spaceToReclaim) {
		return true;
	}
}
