package com.sdis.sueca.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.sdis.sueca.gamestate.GameStateManager;

public class Sueca extends ApplicationAdapter {

	// Instance variables
	private GameStateManager gsm;
	
	// Static variables
	public static final int WIDTH = 720;
	public static final int HEIGHT = 480;
	public static OrthographicCamera cam;
	public static final float[] RGB = new float[] { 0.01f, 0.3f, 0.12f };

	// Instance methods
	@Override
	public void create () {
		// Setup the camera
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		
		// Initialize the game state manager
		gsm = new GameStateManager();
	}

	@Override
	public void render () {
		// Clear screen
		Gdx.gl.glClearColor(RGB[0], RGB[1], RGB[2], 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Update and draw the current state
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void dispose () {
		gsm.dispose();
	}
}
