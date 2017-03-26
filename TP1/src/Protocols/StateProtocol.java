package Protocols;

import Channels.MCChannel;

public class StateProtocol extends Protocol {

	/**
	 * Create a StateProtocol instance
	 * @param mcChannel multicast channel all protocols subscribe to
	 */
	public StateProtocol(MCChannel mcChannel) {
		super(mcChannel);
	}
}
