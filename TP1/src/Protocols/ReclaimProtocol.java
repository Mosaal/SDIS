package Protocols;

import java.util.ArrayList;
import java.util.HashMap;

import Channels.MCChannel;
import Utils.FileManager;
import Utils.Utils;

public class ReclaimProtocol extends Protocol {

	// Instance variables
	private volatile BackupProtocol backupProtocol;

	/**
	 * Creates a ReclaimProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public ReclaimProtocol(String proVer, int peerID, MCChannel mcChannel, BackupProtocol backupProtocol) {
		super(proVer, peerID, mcChannel);
		this.backupProtocol = backupProtocol;
		processReclaim.start();
	}

	// Instance methods
	/** Returns the Peer's instance of the backup protocol */
	public BackupProtocol getBackupProtocol() { return backupProtocol; }

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
	 * Returns the chunks whose perceived replication degree is lesser than the desired one
	 * @param perRep all of the known replication values
	 */
	private ArrayList<String> PRDlesserDRD(ArrayList<String> perRep) {
		ArrayList<String> toBeDeleted = new ArrayList<String>();

		for (int i = 0; i < perRep.size(); i++) {
			String[] args = perRep.get(i).split(":");
			int dRD = Integer.parseInt(args[2]);
			int pRD = Integer.parseInt(args[3]);

			// Add if PRD is greater than DRD
			if (pRD < dRD)
				toBeDeleted.add(args[0] + ":" + args[1]);
		}

		return toBeDeleted;
	}

	/**
	 * Calculate the amount of reclaimable space
	 * @param list chunks whose size is going to be checked
	 */
	private int reclaimableSize(ArrayList<String> list, HashMap<String, ArrayList<Integer>> storedChunks) {
		int size = 0;

		// Sum the amount of each file
		for (int i = 0; i < list.size(); i++) {
			String[] args = list.get(i).split(":");

			if (storedChunks.containsKey(args[0]))
				if (storedChunks.get(args[0]).contains(Integer.parseInt(args[1])))
					size += FileManager.getChunk(peerID, args[0], Integer.parseInt(args[1])).length;
		}

		return size;
	}

	/**
	 * Processes the reclaim request in terms of deleting chunks and sending messages
	 * @param list list of chunks to be deleted
	 */
	private boolean processReclaim(ArrayList<String> list, int spaceToReclaim) {
		int temp = spaceToReclaim * Utils.K;

		for (int i = 0; i < list.size(); i++) {
			if (temp > 0) {
				// Get info
				String fileID = list.get(i).split(":")[0];
				int chunkNo = Integer.parseInt(list.get(i).split(":")[1]);

				// Tell BackupProtocol not to store more of this file
				backupProtocol.addToBeIgnored(fileID);

				// Update the perceived count
				backupProtocol.setNewPerceivedValue(fileID, chunkNo);
				if (FileManager.updatePerceivedReplication(peerID, fileID, chunkNo)[0] == -1)
					return false;

				// Decrement space already reclaimed
				temp -= FileManager.getChunk(peerID, fileID, chunkNo).length;

				// Delete the chunk
				if (!FileManager.deleteChunk(peerID, fileID, chunkNo))
					return false;

				// Send a REMOVED type message
				System.out.println("[ REMOVED ] ID: " + fileID + " - Chunk#" + chunkNo);
				byte[] msg = Utils.createMessage(Utils.REMOVED_STRING, proVer, peerID, fileID, chunkNo, 0, new byte[] {});
				mcChannel.send(msg);

				// Wait for a few seconds
				try { Thread.sleep(Utils.randomDelay()); }
				catch (InterruptedException e) { e.printStackTrace(); }
			} else {
				break;
			}
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
		HashMap<String, ArrayList<Integer>> storedChunks = FileManager.getStoredChunks(peerID);

		// End it if there are no files in storage
		if (storedChunks.isEmpty()) {
			System.out.println("There are no files in storage thus no space to reclaim.");
			return Utils.SUCCESS_MESSAGE;
		}

		// Get the most up to date information
		ArrayList<String> perRep = FileManager.getPerceivedReplication(peerID);		
		ArrayList<String> greater = PRDgreaterDRD(perRep);
		ArrayList<String> equals = PRDequalsDRD(perRep);
		ArrayList<String> lesser = PRDlesserDRD(perRep);

		// Check if there is greater
		int totalSize = 0;
		if (!greater.isEmpty() && !isEnough) {
			totalSize += reclaimableSize(greater, storedChunks);

			// Check if it is enough
			if (totalSize >= (spaceToReclaim * Utils.K)) {
				if (processReclaim(equals, spaceToReclaim))
					isEnough = true;
				else
					return Utils.ERROR_MESSAGE;
			}
		}

		// Check if there is equals and it is enough
		if (!equals.isEmpty() && !isEnough) {
			// Greater wasn't enough
			ArrayList<String> newList = new ArrayList<String>(greater);
			newList.addAll(equals);
			totalSize += reclaimableSize(newList, storedChunks);

			// Check if it is enough
			if (totalSize >= (spaceToReclaim * Utils.K)) {
				if (processReclaim(newList, spaceToReclaim))
					isEnough = true;
				else
					return Utils.ERROR_MESSAGE;
			}
		}

		// Check if there is equals and it is enough
		if (!lesser.isEmpty() && !isEnough) {
			// Greater and equals weren't enough
			ArrayList<String> newList = new ArrayList<String>(greater);
			newList.addAll(equals);
			newList.addAll(lesser);

			// Check if it worked
			if (processReclaim(newList, spaceToReclaim))
				isEnough = true;
			else
				return Utils.ERROR_MESSAGE;
		}

		// Check if even then it is not enough
		if (!isEnough) {
			// Greater and equals weren't enough
			ArrayList<String> newList = new ArrayList<String>(greater);
			newList.addAll(equals);
			newList.addAll(lesser);

			// Check if it worked
			if (processReclaim(newList, spaceToReclaim))
				isEnough = true;
			else
				return Utils.ERROR_MESSAGE;
		}

		return Utils.SUCCESS_MESSAGE;
	}

	/** Thread that is constantly processing REMOVED type messages */
	Thread processReclaim = new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				// Receive data if its there to be received
				byte[] data = null;
				do { data = mcChannel.receive(Utils.REMOVED_INT); }
				while (data == null);

				// Make it a string
				String str = new String(data, 0, data.length);

				// Split it
				String[] args = str.split(" ");

				// Check who it belongs to
				if (Integer.parseInt(args[2]) != peerID) {
					// Parse fileID and chunkNo
					String fileID = args[3];
					int chunkNo = Integer.parseInt(args[4]);

					// Update perceived count
					backupProtocol.setNewPerceivedValue(fileID, chunkNo);
					int[] nPRD = FileManager.updatePerceivedReplication(peerID, fileID, chunkNo);

					// Check state of new perceived count
					if (nPRD[0] != -1 && nPRD[0] == 0) {
						byte[] chunk = FileManager.getChunk(peerID, fileID, chunkNo);

						// Back it up only if it has it
						if (chunk != null)
							backupProtocol.backupChunk(fileID, chunkNo, nPRD[1], chunk);
					}
				}
			}
		}
	});
}
