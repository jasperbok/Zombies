package nl.jasperbok.zombies.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Hud {
	private static Hud instance = null;
	
	private int playerHealth = 100;
	private int bandages = 0;
	private Rectangle healthBar;
	
	public void setPlayerHealth(int amount) {
		playerHealth = amount;
	}
	
	public void setNumBandages(int num) {
		bandages = num;
	}
	
	public void update(int delta) throws SlickException {
		// Adjust the health bar.
		healthBar.setWidth((float)playerHealth);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.draw(healthBar);
	}
	
	private Hud() {
		healthBar = new Rectangle(1000, 10, 100, 20);
	}
	
	public static synchronized Hud getInstance() {
		if (instance == null) {
			instance = new Hud();
		}
		
		return instance;
	}
}
