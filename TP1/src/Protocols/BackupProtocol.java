package Protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

import Channels.MCChannel;
import Channels.MDBChannel;
import Utils.Utils;

public class BackupProtocol extends Protocol {

	// Instance variables
	private MDBChannel mdbChannel;
	
	/**
	 * Creates a BackupProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 * @param mdbChannel multicast data backup channel this protocol subscribes to
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
		String fileID = null;
		try { fileID = filePath + Files.getOwner(Paths.get(filePath)).getName() + new File(filePath).lastModified(); }
		catch (IOException e1) { e1.printStackTrace(); }
		fileID = Utils.encryptString(fileID);
		
		// Split file into chunks
		LinkedList<byte[]> chunks = Utils.splitIntoChinks(filePath);
		
		// Send them one by one
		int waitInterval = 1000;
		for (int i = 0; i < chunks.size(); i++) {
			// Create message and send it
			byte[] msg = Utils.createMessage(Utils.PUTCHUNK_STRING, proVer, peerID, fileID, i, repDeg, chunks.get(i));
			mdbChannel.send(msg);
			
			// Wait for a few seconds
			try { Thread.sleep(waitInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }
			
			// Check if actual replication degree matches the desired one
			// if (currRepDeg < repDeg && retries < 5)
			// { i--; retries++; waitInterval *= 2; }
			// else
			// { retries = 0; currRepDeg = 0; waitInterval = 1000; }
		}

		return true;
	}
	
	/** Thread that is constantly processing STORED type messages */
	Thread processStored = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				byte[] data = null;
				do { data = mcChannel.receive(Utils.STORED_INT); }
				while (data == null);
				
				// Process it
				String str = new String(data, 0, data.length);
				String[] temp = str.split(" ");
				
				// Check who it belongs to
				if (Integer.parseInt(temp[2]) != peerID) {
					
				}
			}
		}
	});
	
	/** Thread that is constantly processing PUTCHUNK type messages */
	Thread processPutchunk = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				byte[] data = null;
				do { data = mdbChannel.receive(); }
				while (data == null);
				
				// Process it
				String str = new String(data, 0, data.length);
				String[] temp = str.split(" ");
				
				// Check who it belongs to
				if (Integer.parseInt(temp[2]) != peerID) {
					
				}
			}
		}
	});
}
