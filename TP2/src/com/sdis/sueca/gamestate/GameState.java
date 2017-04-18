package com.sdis.sueca.gamestate;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public abstract class GameState extends JPanel {

	private static final long serialVersionUID = 9023674122256244710L;
	
	protected GameStateManager gsm;
	protected final Color clearColor = new Color(25, 77, 30);
	
	protected GameState(GameStateManager gsm) { this.gsm = gsm; }
	
	// Abstract methods
	public abstract void handleInput();
	public abstract void update();
	
	public void clearScreen(Graphics g) {
		g.setColor(clearColor);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
