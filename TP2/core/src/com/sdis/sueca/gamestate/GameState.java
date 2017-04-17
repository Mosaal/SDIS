package com.sdis.sueca.gamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.sdis.sueca.main.Sueca;

/**
 * An abstract class from which all of
 * the game's states will derive from.
 */
public abstract class GameState {

	// Instance variables
	protected Vector3 mouse;
	protected GameStateManager gsm;
	protected OrthographicCamera cam;
	
	/**
	 * Initializes the variables common to all game states
	 * @param gsm the state's game state manager
	 */
	public GameState(GameStateManager gsm) {
		this.gsm = gsm;
		mouse = new Vector3();
		
		cam = new OrthographicCamera(Sueca.WIDTH, Sueca.HEIGHT);
		cam.translate(Sueca.WIDTH / 2, Sueca.HEIGHT / 2);
		cam.update();
	}
	
	// Instance methods
	/** Updates what in the state needs to be updated */
	public abstract void update(float deltaTime);
	
	/** Draws the state's interface on the screen */
	public abstract void draw(SpriteBatch sb);
	
	/** Handles the state's input */
	public abstract void handleInput();
	
	/** Disposes of all of the state's assets */
	public abstract void dispose();
}
