package com.sdis.sueca.gamestate;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

public class MainState extends GameState {

	private static final long serialVersionUID = 7826216720370051064L;
	
	public MainState(GameStateManager gsm) {
		super(gsm);
		
		setPreferredSize(new Dimension(400, 300));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(new JLabel("Sueca"));
		
		add(new JButton("Play"));
		add(new JButton("Start Server"));
		add(new JButton("Quit"));
	}

	@Override
	public void handleInput() {

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
