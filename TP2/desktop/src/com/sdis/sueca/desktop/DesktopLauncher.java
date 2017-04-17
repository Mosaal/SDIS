package com.sdis.sueca.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sdis.sueca.main.Sueca;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Initialize the launcher
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// Set configurations
		config.resizable = false;
		config.title = Sueca.TITLE;
		config.width = Sueca.WIDTH;
		config.height = Sueca.HEIGHT;

		// Start the application
		new LwjglApplication(new Sueca(), config);
	}
}
