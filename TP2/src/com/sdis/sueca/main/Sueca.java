package com.sdis.sueca.main;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.sdis.sueca.states.GameOverState;
import com.sdis.sueca.states.HighscoreMenuState;
import com.sdis.sueca.states.InputIPAddrState;
import com.sdis.sueca.states.MainMenuState;
import com.sdis.sueca.states.PlayGameState;
import com.sdis.sueca.states.ServerMenuState;

public class Sueca extends StateBasedGame {

	// Game data
	public int points;
	public int teamPoints;
	public long elapsedTime;
	public String ipAddress;
	
	// Game assets
	public Image backGroundImg;
	public Image backSideHorImg;
	public Image backSideVerImg;
	public PackedSpriteSheet cardsImg;
	public PackedSpriteSheet suitsImg;
	
	// Assets' paths
	private final String cardsPath = "assets/cards/cards.def";
	private final String suitsPath = "assets/cards/suits.def";
	private final String backGroundPath = "assets/img/background.png";
	private final String backSideHorPath = "assets/cards/backsideHor.png";
	private final String backSideVerPath = "assets/cards/backsideVer.png";

	/**
	 * Creates a Sueca instance
	 * @param name the name of the game
	 */
	public Sueca(String name) { super(name); }

	// Instance methods
	@Override
	public void initStatesList(GameContainer cont) throws SlickException {
		// Load only once
		backGroundImg = new Image(backGroundPath);
		cardsImg = new PackedSpriteSheet(cardsPath);
		suitsImg = new PackedSpriteSheet(suitsPath);
		
		Image temp = cardsImg.getSprite("2_of_clubs.png").getScaledCopy(0.3f);
		backSideHorImg = new Image(backSideHorPath).getScaledCopy(temp.getHeight(), temp.getWidth());
		backSideVerImg = new Image(backSideVerPath).getScaledCopy(temp.getWidth(), temp.getHeight());

		// Set all the game's states
		addState(new MainMenuState(this));
		addState(new InputIPAddrState(this));
		addState(new PlayGameState(this));
		addState(new ServerMenuState(this));
		addState(new HighscoreMenuState(this));
		addState(new GameOverState(this));
	}

	/**
	 * Where it all starts
	 * @param args the command line arguments
	 * @throws SlickException
	 */
	public static void main(String[] args) throws SlickException {
		// Initialize the app
		AppGameContainer app = new AppGameContainer(new Sueca("Sueca"));

		// Set application settings
		app.setDisplayMode(1280, 720, false);
		app.setClearEachFrame(false);
		app.setAlwaysRender(true);
		app.setShowFPS(false);
		app.setVSync(true);
		
		// Start application
		app.start();
	}
}
