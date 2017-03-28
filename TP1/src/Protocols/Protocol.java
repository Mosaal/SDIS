package Protocols;

import Channels.MCChannel;

public abstract class Protocol {

	// Instance variables
	protected int peerID;
	protected String proVer;
	protected MCChannel mcChannel;

	/**
	 * Creates a Protocol instance
	 * @param peerID the unique identifier of the Peer
	 * @param mcChannel multicast channel all protocols subscribe to
	 */
	public Protocol(String proVer, int peerID, MCChannel mcChannel) {
		this.proVer = proVer;
		this.peerID = peerID;
		this.mcChannel = mcChannel;
	}

	// Instance methods
	/** Returns the ID of the Peer */
	public int getPeerID() { return peerID; }
	
	/** Returns the protocol's version */
	public String getProVer() { return proVer; }
	
	/** Returns the multicast control channel */
	public MCChannel getMCChannel() { return mcChannel; }
}
