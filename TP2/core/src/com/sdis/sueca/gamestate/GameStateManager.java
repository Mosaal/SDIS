package com.sdis.sueca.gamestate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * An object of type GameStateManger handles
 * all of the logic (initialization, updates, drawing, ...)
 * for the current game state.
 */
public class GameStateManager {

	// Instance variables
	private GameState gameState;
	
	// Static variables
	public static final int MAIN_MENU_STATE = 0;
	public static final int PLAY_GAME_STATE = 1;
	public static final int SERVER_MENU_STATE = 2;
	public static final int HIGHSCORE_MENU_STATE = 3;
	
	/** Creates a GameStateManager instance */
	public GameStateManager() { setState(MAIN_MENU_STATE); }
	
	// Instance methods
	/**
	 * Sets the current game state
	 * @param state the game state to be set
	 */
	public void setState(int state) {
		// Dispose of previous state
		if (gameState != null)
			gameState.dispose();
		
		// Check what state will be set
		switch (state) {
		case MAIN_MENU_STATE:
			gameState = new MainMenuState(this);
			break;
		case PLAY_GAME_STATE:
			gameState = new PlayGameState(this);
			break;
		case SERVER_MENU_STATE:
			gameState = new ServerMenuState(this);
			break;
		case HIGHSCORE_MENU_STATE:
			// switch to highscore menu
			break;
		}
	}
	
	/** Update the current game state */
	public void update(float deltaTime) { gameState.update(deltaTime); }
	
	/** Draw the current game state */
	public void draw(SpriteBatch sb) { gameState.draw(sb); }
	
	/** Handles the current game state's input */
	public void handleInput() { gameState.handleInput(); }
	
	/** Dispose the current game state's assets */
	public void dispose() { gameState.dispose(); }
}
