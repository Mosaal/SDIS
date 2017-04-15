package com.sdis.sueca.gamestate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuState extends GameState {

	/**
	 * Creates a MainMenuState instance
	 * @param gsm the state's game state manager
	 */
	public MainMenuState(GameStateManager gsm) {
		super(gsm);
	}

	// Instance methods
	@Override
	public void update(float deltaTime) {
		handleInput();
	}

	@Override
	public void draw(SpriteBatch sb) {
		
	}

	@Override
	public void handleInput() {
		
	}

	@Override
	public void dispose() {
		
	}
}
