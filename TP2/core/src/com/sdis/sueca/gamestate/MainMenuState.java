package com.sdis.sueca.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector3;
import com.sdis.sueca.main.Sueca;

public class MainMenuState extends GameState {
		
	private Pixmap pix;
	private GlyphLayout gl;
	private BitmapFont font;
	private BitmapFont title;
	
	private int currOption;
	private final String[] options;
	
	/**
	 * Creates a MainMenuState instance
	 * @param gsm the state's game state manager
	 */
	public MainMenuState(GameStateManager gsm) {
		super(gsm);
		
		currOption = -1;
		gl = new GlyphLayout();
		pix = new Pixmap(Gdx.files.internal("img/handCursor.png"));
		options = new String[] { "Start New Game", "Start Server", "Something", "Quit Game" };
		
		FreeTypeFontParameter par = new FreeTypeFontParameter();
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CoffeeTin Initials.ttf"));
		
		par.size = 80;
		title = gen.generateFont(par);
		
		par.size = 40;
		font = gen.generateFont(par);
	}

	// Instance methods
	@Override
	public void update(float deltaTime) {
		
	}

	@Override
	public void draw(SpriteBatch sb) {
		// Set the matrix
		sb.setProjectionMatrix(cam.combined);
		
		// Draw everything between begin and end
		sb.begin();
		
		// Draw title
		gl.setText(title, "Sueca");
		title.draw(sb, "Sueca", (Sueca.WIDTH / 2) - (gl.width / 2), 650);
		
		// Draw options
		for (int i = 0; i < options.length; i++) {
			// Set color
			if (i == currOption)
				font.setColor(Color.GOLD);
			
			// Draw option
			gl.setText(font, options[i]);
			font.draw(sb, options[i], (Sueca.WIDTH / 2) - (gl.width / 2), 250 - (i * 50));
		}
		
		// Close batch
		sb.end();
	}

	@Override
	public void handleInput() {
		for (int i = 0; i < options.length; i++) {
			// Get text width
			gl.setText(font, options[i]);
			
			// Get real mouse coordinates
			mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			cam.unproject(mouse);
			
			// Change cursor
			// Gdx.graphics.setCursor(Gdx.graphics.newCursor(pix, 0, 0));
		}
	}

	@Override
	public void dispose() {
		pix.dispose();
		font.dispose();
		title.dispose();
	}
}
