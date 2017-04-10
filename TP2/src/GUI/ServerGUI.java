package GUI;

import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JPanel;

// import RMI.Server;

public class ServerGUI extends JFrame {

	// Serial Version ID
	private static final long serialVersionUID = -4826945628298567633L;

	// Instance variables
	// private Server server;
	private JPanel mainPanel;
	// private int currActiveRooms;
	// private int currActivePlayers;
	
	/** Creates a ServerGUI instance */
	public ServerGUI() throws RemoteException {
		super("Sueca - Server");
		
		// currActiveRooms = 0;
		// currActivePlayers = 0;
		
		// server = new Server();
		mainPanel = new JPanel();
		
		setSize(600, 600);
		setContentPane(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
