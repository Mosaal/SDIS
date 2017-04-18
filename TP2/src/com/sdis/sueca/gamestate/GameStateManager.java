package com.sdis.sueca.gamestate;

import com.sdis.sueca.app.Application;

public class GameStateManager {

	// Instance variables
	private Application root;
	private GameState gameState;
	
	// Static variables
	public static final int MAIN_MENU_STATE = 0;
	public static final int PLAY_GAME_STATE = 1;
	public static final int SERVER_MENU_STATE = 2;
	public static final int HIGHSCORE_MENU_STATE = 3;
	
	public GameStateManager(Application root) {
		this.root = root;
		setState(MAIN_MENU_STATE);
	}
	
	public void setState(int state) {
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
			gameState = new HighScoreMenuState(this);
			break;
		}
		
		root.setContentPane(gameState);
		root.revalidate();
	}
	
	public void handleInput() { gameState.handleInput(); }
	
	public void update() { gameState.update(); }
	
	public void render() { gameState.repaint(); }
}
