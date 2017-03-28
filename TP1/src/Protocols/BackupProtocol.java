package Protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
	
	public boolean backupFile(String filePath, int repDeg) {
		// Separate data on chunks
		try {
			byte[] data = Files.readAllBytes(Paths.get(filePath));
			byte[][] chunks = new byte[1][];
			int i = 0;
			for(int aux = 0;aux < data.length; aux+=64000){
				if((aux+64000) < data.length) {
					chunks[i]= Arrays.copyOfRange(data,aux,aux+64000);
				}
				else if((aux+64000) > data.length){
					chunks[i]= Arrays.copyOfRange(data,aux,data.length);
				}
				else {
					chunks[i]= Arrays.copyOfRange(data,aux,aux+64000);
					chunks[i+1]= null;
				}
				i++;	
			}
			//send all chunks
			for(int aux = 0;aux < chunks.length; aux++){
				mdbChannel.send(chunks[aux]);
			}
			
			//fodeu
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
}
