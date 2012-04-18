package nl.jasperbok.zombies.entity.object.projectile;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.component.DamagingAuraComponent;
import nl.jasperbok.zombies.entity.component.LifeComponent;
import nl.jasperbok.zombies.level.Level;

public class Bullet extends Entity {
	private Image image;
	
	/**
	 * 
	 * @param level
	 * @param position
	 * @param velocity
	 * @throws SlickException
	 */
	public Bullet(Level level, Vector2f position, Vector2f velocity) throws SlickException {
		this(level, position, velocity, 1);
	}
	
	/**
	 * 
	 * @param level
	 * @param position
	 * @param velocity
	 * @param damage
	 * @throws SlickException
	 */
	public Bullet(Level level, Vector2f position, Vector2f velocity, int damage) throws SlickException {
		this.position = position;
		this.velocity = velocity;
		
		this.image = new Image("data/sprites/entity/object/projectile/bullet.png");
		if (velocity.x > 0) {
			this.image.getFlippedCopy(true, false);
		}
		
		addComponent(new LifeComponent(this));
		addComponent(new DamagingAuraComponent(this, damage));
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(renderPosition.x, renderPosition.y);
	}
}
