package Protocols;

import Channels.MCChannel;

public class StateProtocol extends Protocol {

	/**
	 * Create a StateProtocol instance
	 * @param mcChannel multicast channel all protocols subscribe to
	 */
	public StateProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
	}
	
	// Instance methods
	public boolean getState() {
		return true;
	}
}
