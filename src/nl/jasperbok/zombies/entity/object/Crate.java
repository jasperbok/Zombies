package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Crate extends Entity {
	private Image image;
	private boolean isFalling = false;

	public Crate(Level level) throws SlickException {
		velocity = new Vector2(0.0f, 0.0f);
		image = new Image("data/sprites/entity/object/crate.png", new Color(255, 255, 255));
		this.level = level;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		if (isFalling) {
			this.velocity.y += level.gravity.y * delta;
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
