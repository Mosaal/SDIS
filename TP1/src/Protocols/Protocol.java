package Protocols;

import Channels.MCChannel;

public abstract class Protocol {

	// Instance variables
	private MCChannel mcChannel;
	
	/**
	 * Creates a Protocol instance
	 * @param mcChannel multicast channel all protocols subscribe to
	 */
	public Protocol(MCChannel mcChannel) {
		this.mcChannel = mcChannel;
	}
	
	// Instance methods
	/** Returns the multicast control channel */
	public MCChannel getMCChannel() { return mcChannel; }
}
