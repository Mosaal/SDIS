package com.sdis.sueca.app;

import javax.swing.JFrame;

public class Application extends JFrame implements Runnable {

	private static final long serialVersionUID = -7892107447202655300L;

	/** Creates an Application instance */
	public Application() {
		super("Sueca");
		
		pack();
		setSize(800, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void run() {
		
	}
	
	/**
	 * The code's starting point
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Application app = new Application();
		app.setVisible(true);
	}
}
