package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Attractor;
import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.level.Level;

public class BloodMark extends Entity implements Attractor {
	private int lifetime = 5000;
	private int currentLifetime = 0;
	private Image image;
	private float attractionPower = 0f;
	
	/**
	 * Class constructor.
	 * 
	 * @param level The level this bloodmark is part of.
	 * @param xPos The x position for this bloodmark.
	 * @param yPos The y position for this bloodmark.
	 * @throws SlickException
	 */
	public BloodMark(Level level, float xPos, float yPos) throws SlickException {
		image = new Image("data/sprites/entity/object/blood.png", new Color(255, 255, 255));
		this.level = level;
		boundingBox = new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
		setPosition(xPos, yPos);
	}
	
	/**
	 * Renders the bloodmark on the screen if it's lifetime has not expired.
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (currentLifetime < lifetime) image.draw(position.x, position.y);
	}
	
	/**
	 * Updates the lifetime of the bloodmark.
	 */
	public void update(Input input, int delta) {
		currentLifetime += delta;
		//if (currentLifetime > lifetime) level.env.removeAttractor(this);
	}
	
	/**
	 * Calculates the attraction of this bloodmark.
	 */
	public void calculateAttraction() {
		
	}
}
