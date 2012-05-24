package nl.jasperbok.zombies.entity.object.projectile;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

public class Bullet extends Entity {
	
	private int range = -1;
	private int startX;
	
	/**
	 * 
	 * @param level
	 * @param velocity
	 * @param damage
	 * @param range
	 * @throws SlickException
	 */
	public Bullet(Level level, float x, float y, Vector2f vel, int damage, int range) throws SlickException {
		super.init(level);
		
		this.position.x = x;
		this.position.y = y;
		this.vel = vel;
		
		this.gravityFactor = 0;
		
		this.type = Entity.Type.NONE;
		this.checkAgainst = Entity.Type.BOTH;
		this.collides = Entity.Collides.ACTIVE;
		
		this.startX = (int)position.x;
		this.range = range;
		
		Animation idle = new Animation();
		if (this.vel.x > 0) {
			idle.addFrame(new Image("data/sprites/entity/object/projectile/bullet.png").getFlippedCopy(true, false), 5000);
		} else {
			idle.addFrame(new Image("data/sprites/entity/object/projectile/bullet.png"), 5000);
		}
		this.anims.put("idle", idle);
		this.currentAnim = this.anims.get("idle");
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);
		
		if (range != -1) {
			if (startX - this.position.x > this.range || this.position.x - startX > this.range) {
				this.kill();
			}
		}
	}
	
	public void check(Entity other) {
		System.out.println("Bullet hits something!");
		other.receiveDamage(1);
		this.kill();
	}
}
