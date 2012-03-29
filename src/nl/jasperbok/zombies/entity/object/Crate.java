package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Crate extends Entity {
	private Image image;
	private boolean isFalling = false;
	public boolean draggedByMagnet = false;

	/**
	 * Crate constructor creates a new crate.
	 * 
	 * @param level The level the crate is part of.
	 * @throws SlickException
	 */
	public Crate(Level level, int xPos, int yPos) throws SlickException {
		position = new Vector2(xPos, yPos);
		velocity = new Vector2(0.0f, 0.0f);
		image = new Image("data/sprites/entity/object/crate.png", new Color(255, 255, 255));
		this.level = level;
		boundingBox = new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
	}

	public void update(GameContainer container, int delta) throws SlickException {
		if (draggedByMagnet) velocity.y = 0f;
		
		position.x += velocity.x;
		position.y += velocity.y;
		
		boundingBox.setBounds(position.x, position.y, image.getWidth(), image.getHeight());
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(position.x, position.y);
	}
}
