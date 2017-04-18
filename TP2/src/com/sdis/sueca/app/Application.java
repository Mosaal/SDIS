package com.sdis.sueca.app;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.sdis.sueca.gamestate.GameStateManager;

public class Application extends JFrame implements Runnable {

	private static final long serialVersionUID = -7892107447202655300L;

	// Instance variables
	private Thread thread;
	public boolean isRunning;
	private GameStateManager gsm;

	private final int FPS = 60;
	private final int TARGET_TIME = 1000 / FPS;

	/** Creates an Application instance */
	public Application() {
		// Set the frame's title
		super("Sueca");

		// Set the game's settings
		isRunning = true;
		gsm = new GameStateManager(this);

		// Set the frame's settings
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(getWidth(), getHeight()));

		// Set the frame's location
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int) screenSize.getWidth() / 2;
		int centerY = (int) screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);

		// Start up game loop
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		long wait, start, elapsed;

		while(isRunning) {
			start = System.nanoTime();

			gsm.handleInput();
			gsm.update();
			gsm.render();

			elapsed = System.nanoTime() - start;
			wait = TARGET_TIME - elapsed / 1000000;
			if (wait < 0) wait = 5;

			try { Thread.sleep(wait); }
			catch (Exception e) { e.printStackTrace(); }
		}
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
