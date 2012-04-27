package nl.jasperbok.zombies.entity.object.projectile;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.component.DamagingAuraComponent;
import nl.jasperbok.zombies.entity.component.LifeComponent;
import nl.jasperbok.zombies.level.Level;

public class Bullet extends Entity {
	
	private int range = -1;
	
	/**
	 * 
	 * @param level
	 * @param velocity
	 * @param damage
	 * @param range
	 * @throws SlickException
	 */
	public Bullet(Level level, Vector2f position, Vector2f velocity, int damage, int range) throws SlickException {
		super.init(level);
		this.position = position;
		this.velocity = velocity;
		this.range = range;
		
		this.addComponent(new LifeComponent(this));
		this.addComponent(new DamagingAuraComponent(this, damage));
		
		Animation idle = new Animation();
		if (this.velocity.x > 0) {
			idle.addFrame(new Image("data/sprites/entity/object/projectile/bullet.png").getFlippedCopy(true, false), 5000);
		} else {
			idle.addFrame(new Image("data/sprites/entity/object/projectile/bullet.png"), 5000);
		}
		this.anims.put("idle", idle);
		this.currentAnim = this.anims.get("idle");
	}
}
