package com.sdis.sueca.gamestate;

/**
 * An abstract class from which all of
 * the game's states will derive from.
 */
public abstract class GameState {

	// Instance variables
	protected GameStateManager gsm;
	
	/**
	 * Creates a GameState instance
	 * @param gsm the state's game state manager
	 */
	public GameState(GameStateManager gsm) { this.gsm = gsm; }
	
	// Instance methods
	/** Updates what in the state needs to be updated */
	public abstract void update(float deltaTime);
	
	/** Draws the state's interface on the screen */
	public abstract void draw();
	
	/** Handles the state's input */
	public abstract void handleInput();
	
	/** Disposes of all of the state's assets */
	public abstract void dispose();
}
