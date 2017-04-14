package com.sdis.sueca.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sdis.sueca.gui.Sueca;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Initialize the launcher
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// Set configurations
		config.title = "Sueca";
		config.resizable = true;
		config.width = Sueca.WIDTH;
		config.height = Sueca.HEIGHT;

		// Start the application
		new LwjglApplication(new Sueca(), config);
	}
}
