package nl.jasperbok.zombies.gui;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Hud {
	private static Hud instance = null;
	
	private int playerHealth = 5;
	private Animation playerHealthBar;
	
	public void setPlayerHealth(int amount) {
		if (amount > 5) {
			playerHealth = 5;
		} else if (amount < 0) {
			playerHealth = 0;
		} else {
			playerHealth = amount;
		}
	}
	
	public void update(int delta) throws SlickException {
		// Adjust the health bar.
		playerHealthBar.setCurrentFrame(playerHealth);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.drawAnimation(playerHealthBar, 460, -30);
	}
	
	private Hud() throws SlickException {
		playerHealthBar = new Animation();
		playerHealthBar.setAutoUpdate(false);
		playerHealthBar.stop();
		playerHealthBar.addFrame(new Image("data/sprites/gui/health_bloodmarks/BM_0.png"), 500);
		playerHealthBar.addFrame(new Image("data/sprites/gui/health_bloodmarks/BM_1.png"), 500);
		playerHealthBar.addFrame(new Image("data/sprites/gui/health_bloodmarks/BM_2.png"), 500);
		playerHealthBar.addFrame(new Image("data/sprites/gui/health_bloodmarks/BM_3.png"), 500);
		playerHealthBar.addFrame(new Image("data/sprites/gui/health_bloodmarks/BM_4.png"), 500);
		playerHealthBar.addFrame(new Image("data/sprites/gui/health_bloodmarks/BM_5.png"), 500);
	}
	
	public static synchronized Hud getInstance() throws SlickException {
		if (instance == null) {
			instance = new Hud();
		}
		
		return instance;
	}
}
