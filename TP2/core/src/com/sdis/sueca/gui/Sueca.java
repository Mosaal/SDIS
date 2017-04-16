package com.sdis.sueca.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sdis.sueca.gamestate.GameStateManager;
import com.sdis.sueca.utils.Utils;

public class Sueca extends ApplicationAdapter {

	// Instance variables
	private SpriteBatch sb;
	private GameStateManager gsm;
	
	// Static variables
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final String TITLE = "Sueca";

	// Instance methods
	@Override
	public void create() {
		// Setup the game
		sb = new SpriteBatch();
		gsm = new GameStateManager();
		
		// Setup the clear color
		float[] RGB = Utils.toUnaryRGB(25, 77, 30);
		Gdx.gl.glClearColor(RGB[0], RGB[1], RGB[2], 1);
	}

	@Override
	public void render() {
		// Clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Update and draw the current state
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw(sb);
	}

	@Override
	public void dispose() {
		sb.dispose();
		gsm.dispose();
	}
}
