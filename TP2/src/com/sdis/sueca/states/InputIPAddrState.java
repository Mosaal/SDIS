package com.sdis.sueca.states;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Timer;
import java.util.TimerTask;

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
import com.sdis.sueca.utils.Utils;

public class InputIPAddrState extends BasicGameState {

	// Instance variables
	private Sueca root;

	private String ipAddress;
	private UnicodeFont ipFont;
	private int[] permittedKeys;

	private UnicodeFont font;
	private boolean hoverBack;
	private boolean hoverDone;
	private boolean displayWarning;

	/**
	 * Creates an InputIPAddrState instance
	 * @param root the root of the state
	 */
	public InputIPAddrState(Sueca root) { this.root = root; }

	// Instance methods
	@Override
	public void init(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Load font
		font = Utils.loadFont(40);
		ipFont = Utils.loadFont(60);

		// Set keys
		int temp = 2;
		permittedKeys = new int[10];
		permittedKeys[0] = Input.KEY_0;
		for (int i = 1; i < 10; i++)
			permittedKeys[i] = temp++;
	}
	
	@Override
	public void enter(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Reset settings
		ipAddress = "";
		hoverBack = false;
		hoverDone = false;
		displayWarning = false;
	}
	
	private void enterPlayGameState(StateBasedGame sbg) {
		// Check if server is online
		try {
			// It is
			LocateRegistry.getRegistry(ipAddress).lookup("SUECA_SERVER");
			
			// Change state
			root.ipAddress = ipAddress;
			sbg.enterState(States.PLAY_GAME_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
		} catch (RemoteException | NotBoundException e) {
			// It is not
			displayWarning = true;
			
			// Remove warning after 3 seconds
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// Set to false
					displayWarning = false;
					
					// Close timer
					timer.cancel();
					timer.purge();
				}
			}, 3000);
		}
	}

	@Override
	public void update(GameContainer c, StateBasedGame sbg, int dt) throws SlickException {
		// Reset buttons
		hoverBack = false;
		hoverDone = false;

		// Check if Back is being hovered over
		int fW = font.getWidth("Back");
		int fH = font.getHeight("Back");
		int fO = font.getYOffset("Back");

		if (c.getInput().getMouseX() > 30 && c.getInput().getMouseX() < 30 + fW) {
			if (c.getInput().getMouseY() > 25 + fO && c.getInput().getMouseY() < 25 + fH) {
				// Hovering
				hoverBack = true;

				// Check for click
				if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))
					sbg.enterState(States.MAIN_MENU_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			}
		}

		// Check if Done is being hovered over
		fW = font.getWidth("Done");
		fH = font.getHeight("Done");
		fO = font.getYOffset("Done");

		if (c.getInput().getMouseX() > (c.getWidth() - font.getWidth("Done")) / 2 && c.getInput().getMouseX() < ((c.getWidth() - font.getWidth("Done")) / 2) + fW) {
			if (c.getInput().getMouseY() > 360 + fO && c.getInput().getMouseY() < 360 + fH) {
				// Hovering
				hoverDone = true;

				// Check for click
				if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))
					if (!ipAddress.isEmpty())
						enterPlayGameState(sbg);
			}
		}

		// Check for digit input
		for (int i = 0; i < permittedKeys.length; i++)
			if (c.getInput().isKeyPressed(permittedKeys[i]))
				ipAddress += Integer.toString(i);

		// Check for period input
		if (c.getInput().isKeyPressed(Input.KEY_PERIOD))
			if (!ipAddress.isEmpty())
				ipAddress += ".";

		// Check for backspace input
		if (c.getInput().isKeyPressed(Input.KEY_BACK))
			if (!ipAddress.isEmpty())
				ipAddress = ipAddress.substring(0, ipAddress.length() - 1);

		// Check for enter input
		if (c.getInput().isKeyPressed(Input.KEY_ENTER))
			if (!ipAddress.isEmpty())
				enterPlayGameState(sbg);
	}

	@Override
	public void render(GameContainer c, StateBasedGame sbg, Graphics g) throws SlickException {
		// Draw background
		root.backGroundImg.draw(0, 0, c.getWidth(), c.getHeight());

		// Set color and draw Back button
		if (hoverBack)
			font.drawString(30, 25, "Back", Utils.gold);
		else
			font.drawString(30, 25, "Back", Color.white);

		// Set color and draw Done button
		if (hoverDone)
			font.drawString((c.getWidth() - font.getWidth("Done")) / 2, 360, "Done", Utils.gold);
		else
			font.drawString((c.getWidth() - font.getWidth("Done")) / 2, 360, "Done", Color.white);
		
		// Display warning if necessary
		if (displayWarning) {
			String text = "Warning! Connection to server failed!";
			font.drawString((c.getWidth() - font.getWidth(text)) / 2, 25, text, Color.white);
		}

		// Draw current IP
		String text = "IP Address: " + ipAddress;
		ipFont.drawString((c.getWidth() - ipFont.getWidth(text)) / 2, 300, text);
	}

	@Override
	public int getID() { return States.INPUT_IP_ADDR_STATE.ordinal(); }
}
