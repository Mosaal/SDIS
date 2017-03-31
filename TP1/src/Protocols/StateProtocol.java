package Protocols;

import Channels.MCChannel;

public class StateProtocol extends Protocol {

	/**
	 * Creates a StateProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public StateProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
		// processState.start();
	}
	
	// Instance methods
	public boolean getState() {
		return true;
	}
	
	/**  */
//	Thread processState = new Thread(new Runnable() {
//		@Override
//		public void run() {
//			// TODO: que caralho se faz aqui
//		}
//	});
}
