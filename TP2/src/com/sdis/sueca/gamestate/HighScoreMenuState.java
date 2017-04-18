package com.sdis.sueca.gamestate;

import java.awt.Graphics;

public class HighScoreMenuState extends GameState {

	private static final long serialVersionUID = -7572817702106532033L;

	public HighScoreMenuState(GameStateManager gsm) {
		super(gsm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		clearScreen(g);
	}
}
