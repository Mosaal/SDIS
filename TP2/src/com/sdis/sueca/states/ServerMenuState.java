package com.sdis.sueca.states;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.sdis.sueca.main.Sueca;
import com.sdis.sueca.rmi.Server;
import com.sdis.sueca.utils.Utils;

public class ServerMenuState extends BasicGameState {

	// Instance variables
	private Sueca root;
	private Server server;

	private boolean hoverBack;
	private UnicodeFont backFont;

	private String[] info;
	private UnicodeFont infoFont;

	/**
	 * Creates an InputIPAddrState instance
	 * @param root the root of the state
	 */
	public ServerMenuState(Sueca root) { this.root = root; }

	// Instance methods
	@Override
	public void init(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Load font
		backFont = Utils.loadFont(40);
		infoFont = Utils.loadFont(60);
	}

	@Override
	public void enter(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Reset button
		hoverBack = false;

		// Start server
		try { server = new Server(); }
		catch (RemoteException | UnknownHostException e) { e.printStackTrace(); }
		
		// Set info for now
		info = new String[4];
		info[0] = "Server Status: Online";
		info[1] = "IP Address: " + server.getIPAddress();
		info[2] = "Number of Active Rooms: " + server.getActiveRooms().size();
		info[3] = "Number of Active Players: " + server.getNumActivePlayers();
	}

	@Override
	public void update(GameContainer c, StateBasedGame sbg, int dt) throws SlickException {
		// Reset button
		hoverBack = false;

		// Update data on the screen
		info[2] = "Number of Active Rooms: " + server.getActiveRooms().size();
		info[3] = "Number of Active Players: " + server.getNumActivePlayers();

		// Check if Back is being hovered over
		int fW = backFont.getWidth("Back");
		int fH = backFont.getHeight("Back");
		int fO = backFont.getYOffset("Back");

		if (c.getInput().getMouseX() > 30 && c.getInput().getMouseX() < 30 + fW) {
			if (c.getInput().getMouseY() > 25 + fO && c.getInput().getMouseY() < 25 + fH) {
				// Hovering
				hoverBack = true;

				// Check for click
				if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))
					sbg.enterState(States.MAIN_MENU_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			}
		}
	}

	@Override
	public void render(GameContainer c, StateBasedGame sbg, Graphics g) throws SlickException {
		// Draw background
		root.backGroundImg.draw(0, 0, c.getWidth(), c.getHeight());

		// Set color and draw Back button
		if (hoverBack)
			backFont.drawString(30, 25, "Back", Utils.gold);
		else
			backFont.drawString(30, 25, "Back", Color.white);

		// Draw the info
		for (int i = 0; i < info.length; i++)
			infoFont.drawString((c.getWidth() - infoFont.getWidth(info[i])) / 2, 215 + (60 * i), info[i], Color.white);
	}
	
	@Override
	public void leave(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Shut the server down
		if (server != null)
			server.shutDown();
	}

	@Override
	public int getID() { return States.SERVER_MENU_STATE.ordinal(); }
}
