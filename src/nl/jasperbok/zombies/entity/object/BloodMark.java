package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.level.Level;

public class BloodMark extends Entity {
	private Image image;
	
	public BloodMark(Level level) throws SlickException {
		image = new Image("data/sprites/entity/object/crate.png", new Color(255, 255, 255));
		this.level = level;
		boundingBox = new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
	}
	
	public void render(Graphics g) throws SlickException {
		image.draw(position.x, position.y);
	}
}
