package Protocols;

import java.util.ArrayList;

import Channels.MCChannel;
import Utils.FileManager;
import Utils.Utils;

public class ReclaimProtocol extends Protocol {

	/**
	 * Creates a ReclaimProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public ReclaimProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
		processReclaim.start();
	}

	// Instance methods
	/**
	 * Returns the chunks whose perceived replication degree is greater than the desired one
	 * @param perRep all of the known replication values
	 */
	private ArrayList<String> PRDgreaterDRD(ArrayList<String> perRep) {
		ArrayList<String> toBeDeleted = new ArrayList<String>();

		for (int i = 0; i < perRep.size(); i++) {
			String[] args = perRep.get(i).split(":");
			int dRD = Integer.parseInt(args[2]);
			int pRD = Integer.parseInt(args[3]);

			// Add if PRD is greater than DRD
			if (pRD > dRD)
				toBeDeleted.add(args[0] + ":" + args[1]);
		}

		return toBeDeleted;
	}

	/**
	 * Returns the chunks whose perceived replication degree equals the desired one
	 * @param perRep all of the known replication values
	 */
	private ArrayList<String> PRDequalsDRD(ArrayList<String> perRep) {
		ArrayList<String> toBeDeleted = new ArrayList<String>();

		for (int i = 0; i < perRep.size(); i++) {
			String[] args = perRep.get(i).split(":");
			int dRD = Integer.parseInt(args[2]);
			int pRD = Integer.parseInt(args[3]);

			// Add if PRD is greater than DRD
			if (pRD == dRD)
				toBeDeleted.add(args[0] + ":" + args[1]);
		}

		return toBeDeleted;
	}

	/**
	 * Calculate the amount of reclaimable space
	 * @param list chunks whose size is going to be checked
	 */
	private int reclaimableSize(ArrayList<String> list, ArrayList<String> files) {
		int size = 0;

		// Sum the amount of each file
		for (int i = 0; i < list.size(); i++) {
			String[] args = list.get(i).split(":");

			// Check if file is in storage
			if (files.contains(args[0]))
				size += FileManager.getChunk(peerID, args[0], Integer.parseInt(args[1])).length;
		}

		return size;
	}

	/**
	 * Processes the reclaim request in terms of deleting chunks and sending messages
	 * @param list list of chunks to be deleted
	 */
	private boolean processReclaim(ArrayList<String> list) {
		/**
		 * TODO
		 * Store what chunks are going to be ignored
		 * Update their perceived count on this peer
		 * Delete the actual chunks
		 * Send the REMOVED type messages
		 */
		for (int i = 0; i < list.size(); i++) {
			// Tell BackupProtocol not to store more of this file
			BackupProtocol.toBeIgnored.add(list.get(i).split(":")[0]);
			
			// TODO
			// Update the perceived count
			// Something in the FileManager
			// Keep what's there, update the new ones, remove them if they reach zero
			
			// TODO
			// Delete the chunk
			// Something in the FileManager
			// If its the last chunk, delete its parent folder
			
			// TODO
			// Send a REMOVED type message
			// Wait 0 to 400ms before sending the next
		}

		return true;
	}

	/**
	 * Reclaims the desired amount of space
	 * @param spaceToReclaim the amount of space to be reclaimed (in KBytes)
	 */
	public String reclaimSpace(int spaceToReclaim) {
		// Get the perceived replication for the backed up files
		boolean isEnough = false;
		ArrayList<String> files = FileManager.getStoredFiles(peerID);

		// End it if there are no files in storage
		if (files.isEmpty()) {
			System.out.println("There are no files in storage thus no space to reclaim.");
			return Utils.SUCCESS_MESSAGE;
		}

		// Get the most up to date information
		ArrayList<String> perRep = FileManager.getPerceivedReplication(peerID);		
		ArrayList<String> greater = PRDgreaterDRD(perRep);
		ArrayList<String> equals = PRDequalsDRD(perRep);

		// Check if there is greater
		int totalSize = 0;
		if (!greater.isEmpty() && !isEnough) {
			totalSize += reclaimableSize(greater, files) / 1000;

			// Check if it is enough
			if (totalSize >= spaceToReclaim)
				if (processReclaim(greater))
					isEnough = true;
		}

		// Check if there is equals and it is enough
		if (!equals.isEmpty() && !isEnough) {
			totalSize += reclaimableSize(equals, files) / 1000;

			// Check if it is enough
			if (totalSize >= spaceToReclaim)
				if (processReclaim(equals))
					isEnough = true;
		}

		// Check if is still not enough
		if (!isEnough) {
			// TODO: choose chunks until it is enough
			// Than reclaim space
		} else {
			// TODO: reclaim space
		}

		// TODO: instead of all of this I can choose random chunks
		// Send the necessary removed messages
		// And deal with it on the other side

		return Utils.SUCCESS_MESSAGE;
	}

	/** Thread that is constantly processing REMOVED type messages */
	Thread processReclaim = new Thread(new Runnable() {
		@Override
		public void run() {
			// Receive data if its there to be received
			String str = null;
			do { str = mcChannel.receive(Utils.REMOVED_INT); }
			while (str == null);

			// Split it
			String[] args = str.split(" ");

			// Check who it belongs to
			if (Integer.parseInt(args[2]) != peerID) {
				// TODO: Update perceived count
				// Check if it goes bellow desired
				// Send PUTCHUNKs if it does
				// Do nothing if it doesn't
			}
		}
	});
}
