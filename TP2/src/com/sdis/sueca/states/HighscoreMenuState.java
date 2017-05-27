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

public class HighscoreMenuState extends BasicGameState {

	// Instance variables
	private Sueca root;

	private boolean hoverBack;
	private UnicodeFont backFont;

	private String[] scores;
	private UnicodeFont scoreFont;

	/**
	 * Creates an InputIPAddrState instance
	 * @param root the root of the state
	 */
	public HighscoreMenuState(Sueca root) { this.root = root; }

	// Instance methods
	@Override
	public void init(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Load font
		backFont = Utils.loadFont(40);
		scoreFont = Utils.loadFont(50);
	}

	@Override
	public void enter(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Reset option
		hoverBack = false;
		scores = new String[Utils.MAX_SCORES];

		// Load scores
		String[] temp = Utils.loadScores();
		if (temp != null) {
			for (int i = 0; i < temp.length; i++) {
				String[] data = temp[i].split("_");
				String points = String.format("%03d", Integer.parseInt(data[0]));
				scores[i] = (i + 1) + ". " + points + " on " + data[1] + " at " + data[2];
			}
			
			// Set padding on the scores
			if (temp.length < 10)
				for (int i = temp.length; i < Utils.MAX_SCORES; i++)
					scores[i] = (i + 1) + ". --------------------------";
		} else {
			// There are no scores to show
			for (int i = 0; i < Utils.MAX_SCORES; i++)
				scores[i] = (i + 1) + ". --------------------------";
		}
	}

	@Override
	public void update(GameContainer c, StateBasedGame sbg, int dt) throws SlickException {
		// Reset button
		hoverBack = false;

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

		// Draw the scores
		for (int i = 0; i < scores.length; i++)
			scoreFont.drawString((c.getWidth() - scoreFont.getWidth(scores[i])) / 2, 100 + (50 * i), scores[i]);
	}

	@Override
	public int getID() { return States.HIGHSCORE_MENU_STATE.ordinal(); }
}
