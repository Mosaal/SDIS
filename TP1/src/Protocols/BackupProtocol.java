package Protocols;

import Channels.MCChannel;
import Channels.MDBChannel;
import Chunk.Chunk;
import Utils.Utils;

public class BackupProtocol extends Protocol {

	// Instance variables
	private MDBChannel mdbChannel;
	
	/**
	 * Creates a BackupProtocol instance
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public BackupProtocol(String proVer, int peerID, MCChannel mcChannel, MDBChannel mdbChannel) {
		super(proVer, peerID, mcChannel);
		this.mdbChannel = mdbChannel;
		
		processStored.start();
		processPutchunk.start();
	}
	
	// Instance methods
	/** Returns the multicast data backup channel */
	public MDBChannel getMDBChannel() { return mdbChannel; }
	
	/**
	 * Backup a given file
	 * @param filePath path of the file to be backed up
	 * @param repDeg replication degree for the backup
	 */
	public boolean backupFile(String filePath, int repDeg) {
		// Get file ID
		// TODO: apply SHA256 to some bit string
		String fileID = "lol";
		
		// Separate data on chunks
		Chunk[] chunks = Chunk.splitIntoChinks(filePath, fileID);
		
		// Send them one by one
		for (int i = 0; i < chunks.length; i++) {
			// Save metadata
			// TODO: Dont forget to change arguments
			mdbChannel.send(Utils.createMessage(Utils.PUTCHUNK_STRING, proVer, peerID, fileID, i, repDeg, chunks[i].getData()));
		}

		return true;
	}
	
	/** Thread that is constantly processing STORED messages */
	Thread processStored = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				byte[] data = null;
				do { data = mdbChannel.receive(Utils.STORED_INT); }
				while (data == null);
				
				// Process it
				String str = new String(data, 0, data.length);
				String[] temp = str.split(" ");
				
				if (Integer.parseInt(temp[2]) != peerID)
					System.out.println("Peer: " + temp[2] + " sent a " + temp[0]);
				
				// If valid reply
			}
		}
	});
	
	/** Thread that is constantly processing PUTCHUNK messages */
	Thread processPutchunk = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				byte[] data = null;
				do { data = mdbChannel.receive(Utils.PUTCHUNK_INT); }
				while (data == null);
				
				// Process it
				String str = new String(data, 0, data.length);
				String[] temp = str.split(" ");
				
				if (Integer.parseInt(temp[2]) != peerID)
					System.out.println("Peer: " + temp[2] + " sent a " + temp[0]);
				
				// If valid reply
			}
		}
	});
}
