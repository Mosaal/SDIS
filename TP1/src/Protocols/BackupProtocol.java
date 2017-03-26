package Protocols;

import Channels.MCChannel;
import Channels.MDBChannel;

public class BackupProtocol extends Protocol {

	// Instance variables
	private MDBChannel mdbChannel;
	
	/**
	 * Creates a BackupProtocol instance
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public BackupProtocol(MCChannel mcChannel, MDBChannel mdbChannel) {
		super(mcChannel);
		this.mdbChannel = mdbChannel;
	}
	
	// Instance methods
	/** Returns the multicast data backup channel */
	public MDBChannel getMDBChannel() { return mdbChannel; }
}
