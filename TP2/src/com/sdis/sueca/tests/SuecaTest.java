package com.sdis.sueca.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map.Entry;

import org.junit.Test;

import com.sdis.sueca.game.Card;
import com.sdis.sueca.game.Room;
import com.sdis.sueca.rmi.Client;
import com.sdis.sueca.rmi.ClientInterface;

public class SuecaTest {

	// Test methods
	/** Test the card's creation */
	@Test
	public void testCardCreation() {
		// Create card
		Card card = new Card("ace", "clubs");
		card.setPosition(10, 20);

		// Assert values
		assertEquals(10, card.getX());
		assertEquals(20, card.getY());
		assertEquals(11, card.getPoints());
		assertEquals("ace", card.getValue());
		assertEquals("clubs", card.getSuit());
	}

	/** Test the player's creation */
	@Test
	public void testPlayerCreation() {
		try {
			// Create player
			Client player = new Client(false, "");
			player.setID(0);
			
			// Assert values
			assertEquals(0, player.getID());
			assertEquals(0, player.getPoints());
			assertEquals(0, player.getCardsOnHand().size());
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/** Test the room's creation */
	@Test
	public void testRoomCreation() {
		// Create room
		Room room = new Room(0);

		// Assert values
		assertEquals(0, room.getID());
		assertEquals(0, room.getTurn());
		assertEquals(0, room.getPlayers().size());
		assertEquals(40, room.getFullDeck().size());
		assertEquals(0, room.getTableCards().size());
		assertArrayEquals(new boolean[] { false, false, false, false }, room.getTakenIDs());
	}

	/** Test adding a player to a room */
	@Test
	public void testAddPlayerToRoom() {
		// Create room
		Room room = new Room(0);

		// Add four players
		for (int i = 0; i < 4; i++) {
			try { room.addPlayer(new Client(false, "")); }
			catch (RemoteException | NotBoundException e) { e.printStackTrace(); }
		}

		// Assert values
		assertEquals(4, room.getPlayers().size());
		assertArrayEquals(new boolean[] { true, true, true, true }, room.getTakenIDs());

		for (Entry<Integer, ClientInterface> p: room.getPlayers().entrySet())
			assertEquals(room.getID(), ((Client) p.getValue()).getRoomID());
	}

	/** Test serving the cards among the players */
	@Test
	public void testCardDistribution() {
		try {
			// Create room
			Room room = new Room(0);

			// Add four players
			for (int i = 0; i < 4; i++) {
				try { room.addPlayer(new Client(false, "")); }
				catch (RemoteException | NotBoundException e) { e.printStackTrace(); }
			}

			// Assert values
			for (Entry<Integer, ClientInterface> p: room.getPlayers().entrySet())
				assertEquals(10, p.getValue().getCardsOnHand().size());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/** Test placing a card on the table */
	@Test
	public void testCardPlacement() {
		try {
			// Create room
			Room room = new Room(0);

			// Add four players
			for (int i = 0; i < 4; i++) {
				try { room.addPlayer(new Client(false, "")); }
				catch (RemoteException | NotBoundException e) { e.printStackTrace(); }
			}

			// Assert values
			assertEquals(true, room.canPlaceCard(0, 0));
			assertEquals(1, room.getTableCards().size());
			assertEquals(9, room.getPlayerByID(0).getCardsOnHand().size());

			// Assert values
			assertEquals(1, room.getTurn());
			assertEquals(false, room.canPlaceCard(0, 0));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
