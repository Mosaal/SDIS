package com.sdis.sueca.gamestate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sdis.sueca.utils.Utils;

public class MainMenuState extends GameState {

	private static final long serialVersionUID = 7826216720370051064L;
	
	private JButton playBtn;
	private JButton serverBtn;
	private JButton highScoreBtn;
	private JButton quitBtn;
	
	private BufferedImage logoImage;
	
	public MainMenuState(GameStateManager gsm) {
		super(gsm);
		
		setLayout(new GridLayout(2, 1));
		setPreferredSize(new Dimension(1000, 700));
		
		JLabel title = new JLabel("Sueca");
		title.setForeground(new Color(212, 175, 55));
		title.setBorder(new EmptyBorder(30, 0, 0, 0));
		title.setVerticalAlignment(SwingConstants.TOP);
		title.setFont(new Font("CoffeeTin", Font.PLAIN, 90));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		
		add(title);
		add(buttonsPanel);
		
		playBtn = new JButton("Play Game");
		playBtn.setFocusPainted(false);
		playBtn.setFont(new Font("CoffeeTin", Font.PLAIN, 45));
		playBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		serverBtn = new JButton("Start Server");
		serverBtn.setFocusPainted(false);
		serverBtn.setFont(new Font("CoffeeTin", Font.PLAIN, 45));
		serverBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		highScoreBtn = new JButton("High Scores");
		highScoreBtn.setFocusPainted(false);
		highScoreBtn.setFont(new Font("CoffeeTin", Font.PLAIN, 45));
		highScoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		quitBtn = new JButton("Quit Game");
		quitBtn.setFocusPainted(false);
		quitBtn.setFont(new Font("CoffeeTin", Font.PLAIN, 45));
		quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Component rigidArea1 = Box.createRigidArea(new Dimension(getWidth(), 20));
		Component rigidArea2 = Box.createRigidArea(new Dimension(getWidth(), 20));
		Component rigidArea3 = Box.createRigidArea(new Dimension(getWidth(), 20));
		
		buttonsPanel.add(playBtn);
		buttonsPanel.add(rigidArea1);
		buttonsPanel.add(serverBtn);
		buttonsPanel.add(rigidArea2);
		buttonsPanel.add(highScoreBtn);
		buttonsPanel.add(rigidArea3);
		buttonsPanel.add(quitBtn);
		
		logoImage = Utils.loadImage("assets/cards/logo.png");
		
		initInput();
	}
	
	private void initInput() {
		playBtn.addActionListener(e -> {
			if (e.getActionCommand().equals("Play Game"))
				gsm.setState(GameStateManager.PLAY_GAME_STATE);
		});
		
		serverBtn.addActionListener(e -> {
			if (e.getActionCommand().equals("Start Server"))
				gsm.setState(GameStateManager.SERVER_MENU_STATE);
		});
		
		highScoreBtn.addActionListener(e -> {
			if (e.getActionCommand().equals("High Scores"))
				gsm.setState(GameStateManager.HIGHSCORE_MENU_STATE);
		});
		
		quitBtn.addActionListener(e -> System.exit(0));
	}

	@Override
	public void handleInput() {}

	@Override
	public void update() {}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		clearScreen(g);
		
		// Draw the main image
		int w = (getWidth() / 2) - (logoImage.getWidth() / 2);
		int h = (getHeight() / 2) - (logoImage.getHeight() / 2);
		g.drawImage(logoImage, w, h, null);
	}
}
