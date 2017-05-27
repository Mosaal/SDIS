package com.sdis.sueca.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.sdis.sueca.main.Sueca;
import com.sdis.sueca.utils.Utils;

public class MainMenuState extends BasicGameState {

	// Instance variables
	private Sueca root;
	private Image logoImg;

	private int currOption;
	private String[] options;

	private UnicodeFont font;
	private UnicodeFont title;

	/**
	 * Creates a MainMenuState instance
	 * @param root the root of the state
	 */
	public MainMenuState(Sueca root) { this.root = root; }

	// Instance methods
	@Override
	public void init(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Load logo
		logoImg = new Image("assets/img/logo.png");

		// Load fonts
		font = Utils.loadFont(50);
		title = Utils.loadFont(100);

		// Options settings
		options = new String[] { "Start Game", "Start Server", "HighScores", "Quit Game" };
	}

	@Override
	public void enter(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Reset option
		currOption = -1;
	}

	/**
	 * Checks which option was clicked
	 * @param c the container holding the game
	 * @param sgb the game holding this state
	 * @param index the index of the clicked option
	 */
	private void optionClicked(GameContainer c, StateBasedGame sbg, int index) {
		switch (index) {
		case 0:
			sbg.enterState(States.INPUT_IP_ADDR_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			break;
		case 1:
			sbg.enterState(States.SERVER_MENU_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			break;
		case 2:
			sbg.enterState(States.HIGHSCORE_MENU_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			break;
		case 3:
			c.exit();
			break;
		}
	}

	@Override
	public void update(GameContainer c, StateBasedGame sbg, int dt) throws SlickException {
		// Reset option
		currOption = -1;

		// Check if the options are being hovered over
		for (int i = 0; i < options.length; i++) {
			int fW = font.getWidth(options[i]);
			int fH = font.getHeight(options[i]);
			int fO = font.getYOffset(options[i]);

			if (c.getInput().getMouseX() > 30 && c.getInput().getMouseX() < 30 + fW) {
				if (c.getInput().getMouseY() > 450 + (i * 60) + fO && c.getInput().getMouseY() < 450 + (i * 60) + fH) {
					// Option hovered
					currOption = i;

					// Check for click
					if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))
						optionClicked(c, sbg, i);
				}
			}
		}
	}

	@Override
	public void render(GameContainer c, StateBasedGame sbg, Graphics g) throws SlickException {
		// Draw background
		root.backGroundImg.draw(0, 0, c.getWidth(), c.getHeight());

		// Draw game name
		title.drawString((c.getWidth() - title.getWidth("Sueca")) / 2, 10, "Sueca", Utils.gold);

		// Draw logo
		logoImg.draw((c.getWidth() - logoImg.getWidth()) / 2, (c.getHeight() - logoImg.getHeight()) / 2);

		// Draw options
		for (int i = 0; i < options.length; i++) {
			if (currOption == i)
				font.drawString(30, 450 + (i * 60), options[i], Utils.gold);
			else
				font.drawString(30, 450 + (i * 60), options[i], Color.white);
		}
	}

	@Override
	public int getID() { return States.MAIN_MENU_STATE.ordinal(); }
}
