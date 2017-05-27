package com.sdis.sueca.states;

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

public class GameOverState extends BasicGameState {

	// Instance variables
	private Sueca root;

	private UnicodeFont font;
	private boolean hoverBack;
	private boolean hoverAgain;

	private String[] info;
	private UnicodeFont infoFont;

	/**
	 * Creates an InputIPAddrState instance
	 * @param root the root of the state
	 */
	public GameOverState(Sueca root) { this.root = root; }

	// Instance methods	
	@Override
	public void init(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Load font
		font = Utils.loadFont(40);
		infoFont = Utils.loadFont(60);
	}

	@Override
	public void enter(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Reset settings
		hoverBack = false;
		hoverAgain = false;

		// Set info
		info = new String[5];
		info[0] = "Game Over!";
		
		// Set winner
		if (root.teamPoints > 60)
			info[1] = "You Win!";
		else if (root.teamPoints < 60)
			info[1] = "You Lose!";
		else
			info[1] = "Tie!";
		
		// Set game time
		int sec = (int) root.elapsedTime / 1000;
		int min = sec / 60; sec %= 60;
		info[2] = "Game Time: " + String.format("%02d", min) + ":" + String.format("%02d", sec);
		
		// Set points
		info[3] = "Your Points: " + String.format("%03d", root.points);
		info[4] = "Team Points: " + String.format("%03d", root.teamPoints);
		
		// Save new score
		Utils.saveScore(root.teamPoints);
	}

	@Override
	public void update(GameContainer c, StateBasedGame sbg, int dt) throws SlickException {
		// Reset buttons
		hoverBack = false;
		hoverAgain = false;

		// Check if Back is being hovered over
		int fW = font.getWidth("Back to Main Menu");
		int fH = font.getHeight("Back to Main Menu");
		int fO = font.getYOffset("Back to Main Menu");

		if (c.getInput().getMouseX() > 30 && c.getInput().getMouseX() < 30 + fW) {
			if (c.getInput().getMouseY() > 25 + fO && c.getInput().getMouseY() < 25 + fH) {
				// Hovering
				hoverBack = true;

				// Check for click
				if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))
					sbg.enterState(States.MAIN_MENU_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			}
		}

		// Check if Try Again is being hovered over
		fW = font.getWidth("Try Again");
		fH = font.getHeight("Try Again");
		fO = font.getYOffset("Try Again");

		if (c.getInput().getMouseX() > (c.getWidth() - font.getWidth("Try Again")) / 2 && c.getInput().getMouseX() < ((c.getWidth() - font.getWidth("Try Again")) / 2) + fW) {
			if (c.getInput().getMouseY() > 630 + fO && c.getInput().getMouseY() < 630 + fH) {
				// Hovering
				hoverAgain = true;

				// Check for click
				if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))
					sbg.enterState(States.INPUT_IP_ADDR_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			}
		}
	}

	@Override
	public void render(GameContainer c, StateBasedGame sbg, Graphics g) throws SlickException {
		// Draw background
		root.backGroundImg.draw(0, 0, c.getWidth(), c.getHeight());

		// Set color and draw Back button
		if (hoverBack)
			font.drawString(30, 25, "Back to Main Menu", Utils.gold);
		else
			font.drawString(30, 25, "Back to Main Menu", Color.white);

		// Set color and draw Try Again button
		if (hoverAgain)
			font.drawString((c.getWidth() - font.getWidth("Try Again")) / 2, 630, "Try Again", Utils.gold);
		else
			font.drawString((c.getWidth() - font.getWidth("Try Again")) / 2, 630, "Try Again", Color.white);

		// Draw the info
		for (int i = 0; i < info.length; i++)
			infoFont.drawString((c.getWidth() - infoFont.getWidth(info[i])) / 2, 180 + (60 * i), info[i], Color.white);
	}

	@Override
	public int getID() { return States.GAME_OVER_STATE.ordinal(); }
}
