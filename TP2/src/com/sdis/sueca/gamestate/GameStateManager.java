package com.sdis.sueca.gamestate;

import com.sdis.sueca.app.Application;

public class GameStateManager {

	// Instance variables
	private Application root;
	private GameState gameState;
	
	// Static variables
	public static final int MAIN_MENU = 0;
	public static final int PLAY_GAME = 1;
	public static final int SERVER_MENU = 2;
	
	public GameStateManager(Application root) {
		this.root = root;
		setState(MAIN_MENU);
	}
	
	public void setState(int state) {
		if (gameState != null)
			gameState = null;
		
		switch (state) {
		case MAIN_MENU:
			gameState = new MainState(this);
			root.setContentPane(gameState);
			break;
		case PLAY_GAME:
			// gameState = new PlayGame(this);
			// root.setContentPane(gameState);
			break;
		case SERVER_MENU:
			// gameState = new ServerMenu(this);
			// root.setContentPane(gameState);
			break;
		}
	}
	
	public void handleInput() { gameState.handleInput(); }
	
	public void update() { gameState.update(); }
	
	public void render() { gameState.repaint(); }
}
