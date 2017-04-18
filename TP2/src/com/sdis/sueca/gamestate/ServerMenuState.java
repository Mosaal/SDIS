package com.sdis.sueca.gamestate;

import java.awt.Graphics;

public class ServerMenuState extends GameState {

	private static final long serialVersionUID = 1172332344661064811L;

	public ServerMenuState(GameStateManager gsm) {
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
