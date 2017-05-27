package com.sdis.sueca.states;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.sdis.sueca.main.Sueca;
import com.sdis.sueca.rmi.Client;
import com.sdis.sueca.utils.Utils;

public class PlayGameState extends BasicGameState {

	// Instance variables
	private Sueca root;
	private Client client;

	private UnicodeFont font;
	private boolean hoverQuit;

	private int currCard;
	private long elapsedTime;
	private boolean cardsLoaded;
	private int top, left, right;

	/**
	 * Creates an InputIPAddrState instance
	 * @param root the root of the state
	 */
	public PlayGameState(Sueca root) { this.root = root; }

	// Instance methods
	@Override
	public void init(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Load font
		font = Utils.loadFont(40);
	}

	@Override
	public void enter(GameContainer c, StateBasedGame sbg) throws SlickException {
		// Reset settings
		currCard = -1;
		elapsedTime = 0;
		hoverQuit = false;
		cardsLoaded = false;

		// Start client
		try { client = new Client(true, root.ipAddress); }
		catch (RemoteException | NotBoundException e) { e.printStackTrace(); }
	}

	/** Sets the player's spots on the table */
	private void setPlayersSpots() {
		switch (client.getID()) {
		case 0:
			top = 2;
			left = 3;
			right = 1;
			break;
		case 1:
			top = 3;
			left = 0;
			right = 2;
			break;
		case 2:
			top = 0;
			left = 1;
			right = 3;
			break;
		case 3:
			top = 1;
			left = 2;
			right = 0;
			break;
		}
	}

	@Override
	public void update(GameContainer c, StateBasedGame sbg, int dt) throws SlickException {
		// Load cards
		if (!cardsLoaded && client != null) {
			if (client.countCardsOnHand() == 10) {
				cardsLoaded = true;
				setPlayersSpots();
			}
		}

		// Reset settings
		currCard = -1;
		hoverQuit = false;

		// Check if Back is being hovered over
		int fW = font.getWidth("Quit");
		int fH = font.getHeight("Quit");
		int fO = font.getYOffset("Quit");

		if (c.getInput().getMouseX() > 30 && c.getInput().getMouseX() < 30 + fW) {
			if (c.getInput().getMouseY() > 25 + fO && c.getInput().getMouseY() < 25 + fH) {
				// Hovering
				hoverQuit = true;

				// Check for click
				if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
					// Shut client down
					client.quitGame();
					
					// Change state
					sbg.enterState(States.MAIN_MENU_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
				}
			}
		}

		// Check if cards have been loaded
		if (cardsLoaded) {
			// Set elapsed time
			elapsedTime += dt;

			// Check if the game is over
			if (client.isGameOver()) {
				// Shut client down
				client.gameOver();
				
				// Change state
				sbg.enterState(States.GAME_OVER_STATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
			}
			
			try {
				// Check if cards are hovered over
				int w = root.backSideVerImg.getWidth() + (client.countCardsOnHand() - 1) * 40;
				int x = (c.getWidth() - w) / 2;

				for (int i = 0; i < client.countCardsOnHand(); i++) {
					// Set cards on the bottom
					client.getCardByIndex(i).setPosition(x + (i * 40), 600);

					// Check which is hovered over
					Image temp = root.cardsImg.getSprite(client.getCardByIndex(i).toString() + ".png");
					if (c.getInput().getMouseX() > client.getCardByIndex(i).getX() && c.getInput().getMouseX() < client.getCardByIndex(i).getX() + temp.getWidth())
						if (c.getInput().getMouseY() > client.getCardByIndex(i).getY() && c.getInput().getMouseY() < client.getCardByIndex(i).getY() + temp.getHeight())
							currCard = i;
				}

				// Highlight hovered card and check for click
				if (currCard != -1) {
					// Highlight it
					client.getCardByIndex(currCard).setPosition(x + (40 * currCard), 550);

					// Check for click
					if (c.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))
						client.canPlaceCard(currCard);
				}
			} catch (RemoteException e) { return; }
		}
	}

	@Override
	public void render(GameContainer c, StateBasedGame sbg, Graphics g) throws SlickException {
		// Draw background
		root.backGroundImg.draw(0, 0, c.getWidth(), c.getHeight());

		// Set color and draw Quit button
		if (hoverQuit)
			font.drawString(30, 25, "Quit", Utils.gold);
		else
			font.drawString(30, 25, "Quit", Color.white);

		// Check if cards have been loaded
		if (cardsLoaded) {
			// Draw trump
			font.drawString(980, 20, "Trump: ", Color.white);
			root.suitsImg.getSprite(client.getTrump() + ".png").draw(970 + font.getWidth("Trump: "), 10, 0.7f);

			try {
				// Draw points
				font.drawString(920, 620, "Your Points: " + client.getPoints(), Color.white);
				font.drawString(920, 660, "Team Points: " + client.getTeamPoints(), Color.white);
			} catch (RemoteException e) { return; }

			// Draw turn
			if (client.isTurn())
				font.drawString(40, 620, "Your turn!", Color.white);
			else
				font.drawString(40, 620, "Not your turn!", Color.white);
			
			// Draw game time
			int sec = (int) elapsedTime / 1000;
			int min = sec / 60; sec %= 60;
			String clock = "Time: " + String.format("%02d", min) + ":" + String.format("%02d", sec);
			font.drawString(40, 660, clock, Color.white);

			// Draw cards of the other players
			int t = root.backSideHorImg.getHeight() + ((client.getCardsCount()[right] - 1) * 40);
			int p = (c.getHeight() - t) / 2;
			for (int i = 0; i < client.getCardsCount()[right]; i++)
				root.backSideHorImg.draw(1180, (t - root.backSideHorImg.getHeight()) + p - (i * 40));

			t = root.backSideHorImg.getHeight() + ((client.getCardsCount()[left] - 1) * 40);
			p = (c.getHeight() - t) / 2;
			for (int i = 0; i < client.getCardsCount()[left]; i++)
				root.backSideHorImg.draw(-120, p + (i * 40));

			t = root.backSideVerImg.getWidth() + ((client.getCardsCount()[top] - 1) * 40);
			p = (c.getWidth() - t) / 2;
			for (int i = 0; i < client.getCardsCount()[top]; i++)
				root.backSideVerImg.draw((t - root.backSideVerImg.getWidth()) + p - (i * 40), -130);

			// Draw your card on the table
			if (client.getCardsOnTable().containsKey(client.getID())) {
				Image temp = root.cardsImg.getSprite(client.getCardsOnTable().get(client.getID()).toString() + ".png").getScaledCopy(0.3f);
				temp.draw(c.getWidth() / 2, (c.getHeight() / 2) - 30);
			}

			// Draw the top player card on the table
			if (client.getCardsOnTable().containsKey(top)) {
				Image temp = root.cardsImg.getSprite(client.getCardsOnTable().get(top).toString() + ".png").getScaledCopy(0.3f);
				temp.draw((c.getWidth() / 2) - temp.getWidth(), (c.getHeight() / 2) - temp.getHeight() - 30);
			}

			// Draw the left player card on the table
			if (client.getCardsOnTable().containsKey(left)) {
				Image temp = root.cardsImg.getSprite(client.getCardsOnTable().get(left).toString() + ".png").getScaledCopy(0.3f);
				temp.rotate(90);
				temp.draw((c.getWidth() / 2) - temp.getWidth() - 33, (c.getHeight() / 2) - 63);
			}

			// Draw the right player card on the table
			if (client.getCardsOnTable().containsKey(right)) {
				Image temp = root.cardsImg.getSprite(client.getCardsOnTable().get(right).toString() + ".png").getScaledCopy(0.3f);
				temp.rotate(-90);
				temp.draw((c.getWidth() / 2) + 33, (c.getHeight() / 2) - temp.getHeight() + 3);
			}

			try {
				// Draw cards on hand
				for (int i = 0; i < client.countCardsOnHand(); i++) {
					// Get data
					int x = client.getCardByIndex(i).getX();
					int y = client.getCardByIndex(i).getY();
					String name = client.getCardByIndex(i).toString() + ".png";

					// Draw card
					root.cardsImg.getSprite(name).draw(x, y, 0.3f);
				}
			} catch (RemoteException e) { return; }
		} else {
			int x = (c.getWidth() - font.getWidth("Waiting for more players...")) / 2;
			int y = (c.getHeight() - font.getHeight("Waiting for more players...")) / 2;
			font.drawString(x, y, "Waiting for more players...", Color.white);
		}
	}

	@Override
	public void leave(GameContainer c, StateBasedGame sbg) throws SlickException {
		try {
			// Leave the room
			if (client != null) {
				// Save game data
				root.elapsedTime = elapsedTime;
				root.points = client.getPoints();
				root.teamPoints = client.getTeamPoints();
			}
		} catch (RemoteException e) { return; }
	}

	@Override
	public int getID() { return States.PLAY_GAME_STATE.ordinal(); }
}
