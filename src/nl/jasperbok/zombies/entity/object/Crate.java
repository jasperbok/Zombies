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
		int tileWidth = level.map.getTileWidth();
		
		// Variables used for collision detection.
		int height = image.getHeight();
		int width = image.getWidth();
		int centerX = (int)(position.x + width / 2);
		int centerY = (int)(position.y + height / 2);
		int rightX = (int)(position.x + width);
		int bottomY = (int)(position.y + height);
		
		// Positions in the tile system.
		int yTiled = (int)(Math.floor(position.y / tileWidth));
		int xTiled = (int)(Math.floor(position.x / tileWidth));
		int centerXTiled = (int)(Math.floor(centerX / tileWidth));
		int centerYTiled = (int)(Math.floor(centerY / tileWidth));
		int rightXTiled = (int)(Math.floor((position.x + width) / tileWidth));
		int bottomYTiled = (int)(Math.floor((position.y + height) / tileWidth));
		
		int bottomTileId = level.map.getTileId(centerXTiled, bottomYTiled, 0);
		int rightTileId = level.map.getTileId(rightXTiled, centerYTiled, 0);
		int leftTileId = level.map.getTileId(xTiled, centerYTiled, 0);
		int centerTileId = level.map.getTileId(centerXTiled, centerYTiled, 0);
		int topTileId = level.map.getTileId(centerXTiled, yTiled, 0);
		int tileUnderneathId = level.map.getTileId(centerXTiled, yTiled + 1, 0);
		
		boolean isFalling = false;
		boolean isJumping = false;
		boolean isClimbing = false;
		boolean isOnGround = false;
		
		if ("false".equals(level.map.getTileProperty(tileUnderneathId, "blocked", "false"))) {
			isFalling = true;
		} else {
			isOnGround = true;
			isFalling = false;
		}
		
		if (isFalling) {
			this.velocity.y += -level.gravity.y * delta;
		} else {
			this.velocity.y = 0;
		}
		
		position.x += velocity.x;
		position.y += velocity.y;
		
		boundingBox.setBounds(position.x, position.y, image.getWidth(), image.getHeight());
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(position.x, position.y);
	}
}
