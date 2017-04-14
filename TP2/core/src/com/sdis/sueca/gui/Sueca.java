package com.sdis.sueca.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Sueca extends ApplicationAdapter {

	// Static variables
	public static final int WIDTH = 720;
	public static final int HEIGHT = 480;
	public static OrthographicCamera cam;

	@Override
	public void create () {
		// Setup the camera
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
	}

	@Override
	public void render () {
		// Clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void dispose () {
		
	}
}
