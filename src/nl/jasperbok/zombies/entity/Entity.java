package nl.jasperbok.zombies.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public abstract class Entity {
	public Level level;
	public Vector2 position = new Vector2(0.0f, 0.0f);
	public Vector2 velocity = new Vector2(0.0f, 0.0f);
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	public boolean isBlocking = true;
	public boolean isMovable = true;
	public boolean playerControlled = false;
	public boolean gravityAffected = true;
	
	public boolean isFalling = false;
	public boolean isOnGround = true;
	public boolean isFacingLeft = false;
	
	public Vector2 drawPosition = new Vector2(0.0f, 0.0f);
	
	public void init(Level level) {
		this.level = level;
		this.boundingBox = new Rectangle(0, 0, 0, 0);
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
		updateBoundingBox();
	}
	
	protected void updateBoundingBox() {
		boundingBox.setX(position.getX());
		boundingBox.setY(position.getY());
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		
	}
	
	public void update(Input input, int delta) {
		
	}
}
