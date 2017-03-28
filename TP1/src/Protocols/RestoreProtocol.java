package Protocols;

import Channels.MCChannel;
import Channels.MDRChannel;

public class RestoreProtocol extends Protocol {

	// Instance variables
	private MDRChannel mdrChannel;
	
	/**
	 * Create a RestoreProtocol instance
	 * @param mcChannel multicast channel all protocols subscribe to
	 */
	public RestoreProtocol(MCChannel mcChannel, MDRChannel mdrChannel) {
		super(mcChannel);
		this.mdrChannel = mdrChannel;
	}
	
	// Instance methods
	/** Returns the multicast data recovery channel */
	public MDRChannel getMDRChannel() { return mdrChannel; }
	
	public boolean restoreFile(String fileName) {
		return true;
	}
}
