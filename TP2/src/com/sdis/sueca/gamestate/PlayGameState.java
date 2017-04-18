package com.sdis.sueca.gamestate;

import java.awt.Graphics;

public class PlayGameState extends GameState {

	private static final long serialVersionUID = 5215430056667609290L;
	
	public PlayGameState(GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void handleInput() {
		//Point p = MouseInfo.getPointerInfo().getLocation();
		//SwingUtilities.convertPointFromScreen(p, this);
	}

	@Override
	public void update() {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		clearScreen(g);
	}
}
