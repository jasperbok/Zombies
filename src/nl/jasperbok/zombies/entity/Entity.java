package nl.jasperbok.zombies.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public abstract class Entity {
	public Level level;
	public Vector2 position = new Vector2(0.0f, 0.0f);
	public Rectangle boundingBox;
	public boolean isBlocking = true;
	public boolean playerControlled = false;
	
	public void init(Level level) {
		this.level = level;
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		
	}
}
