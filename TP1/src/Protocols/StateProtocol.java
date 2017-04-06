package Protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Channels.MCChannel;
import Utils.FileManager;

public class StateProtocol extends Protocol {

	/**
	 * Creates a StateProtocol instance
	 * @param proVer protocol version
	 * @param peerID the ID of the Peer
	 * @param mcChannel multicast control channel all protocols subscribe to
	 */
	public StateProtocol(String proVer, int peerID, MCChannel mcChannel) {
		super(proVer, peerID, mcChannel);
	}

	// Instance methods
	/**
	 * Parses the information retrieved from the replication file
	 * @param repInfo list with the information to be parsed
	 */
	private HashMap<String, HashMap<Integer, int[]>> parseInfo(ArrayList<String> repInfo) {
		HashMap<String, HashMap<Integer, int[]>> temp = new HashMap<String, HashMap<Integer, int[]>>();

		for (int i = 0; i < repInfo.size(); i++) {
			String[] res = repInfo.get(i).split(":");

			// Check if it already contains it
			if (temp.containsKey(res[0])) {
				HashMap<Integer, int[]> chunkToRep = temp.get(res[0]);
				chunkToRep.put(Integer.parseInt(res[1]), new int[] { Integer.parseInt(res[2]), Integer.parseInt(res[3]) });
				temp.put(res[0], chunkToRep);
			} else {
				// Add new if it doesn't
				HashMap<Integer, int[]> chunkToRep = new HashMap<Integer, int[]>();
				chunkToRep.put(Integer.parseInt(res[1]), new int[] { Integer.parseInt(res[2]), Integer.parseInt(res[3]) });
				temp.put(res[0], chunkToRep);
			}
		}

		return temp;
	}

	/**
	 * Returns information about the files whose backup this Peer initiated
	 * @param parsedInfo the information about each of the files
	 * @param fileNames the hashmap containing the file's names
	 */
	private String backedUpFilesInfo(HashMap<String, HashMap<Integer, int[]>> parsedInfo, HashMap<String, String> fileNames) {
		String res = "";
		
		// Check if its empty
		if (fileNames.isEmpty())
			return "    This Peer hasn't initiated a Backup yet.\n";

		// Check only the files the Peer has backed up
		for (Entry<String, String> map: fileNames.entrySet()) {
			if (parsedInfo.containsKey(map.getValue())) {
				res += "   * Name of the file: " + map.getKey() + "\n";
				res += "     - ID of the file: " + map.getValue() + "\n";
				res += "       * Chunks:\n";

				for (Entry<Integer, int[]> secMap: parsedInfo.get(map.getValue()).entrySet()) {
					res += "         - ID: " + secMap.getKey().intValue() + "\n";
					res += "         - Desired RD: " + secMap.getValue()[0] + "\n";
					res += "         - Perceived RD: " + secMap.getValue()[1] + "\n";
				}
			}
		}

		return res;
	}
	
	/**
	 * Returns information about the files this Peer has in storage
	 * @param storedFiles list of stored files
	 * @return
	 */
	private String storedFilesInfo(HashMap<String, ArrayList<Integer>> storedChunks) {
		String res = "";
		
		// Check if its empty
		if (storedChunks.isEmpty())
			return "    This Peer has no files in storage yet.\n";
		
		// Add info about files in storage
		for (Entry<String, ArrayList<Integer>> temp: storedChunks.entrySet()) {
			res += "   * ID of the file: " + temp.getKey() + "\n";
			res += "     * Chunks:\n";
			
			// Add info about each chunk
			ArrayList<Integer> chunks = temp.getValue();
			for (int i = 0; i < chunks.size(); i++)
				res += "       - ID: " + chunks.get(i) + " - Size: " + FileManager.getChunk(peerID, temp.getKey(), chunks.get(i)).length + "\n";
		}
		
		return res;
	}

	/** Retrieves information about the internal state of the Peer */
	public String getState() {
		String reply = "";

		// Retrieve the most up to date information
		HashMap<String, String> fileNames = FileManager.getFileID(peerID);
		ArrayList<String> repInfo = FileManager.getPerceivedReplication(peerID);
		HashMap<String, ArrayList<Integer>> storedChunks = FileManager.getStoredChunks(peerID);

		// Parse info into hashmap: FileID -> (ChunkNo -> ([0] = DesRD, [1] = PerRD))
		HashMap<String, HashMap<Integer, int[]>> parsedInfo = parseInfo(repInfo);

		// Adds to the string info about backed up files
		reply += " = INITIATED BACKUPS = \n";
		reply += backedUpFilesInfo(parsedInfo, fileNames);
		
		// Adds to the string info about stored files
		reply += " = STORED FILES = \n";
		reply += storedFilesInfo(storedChunks);

		return reply;
	}
}
