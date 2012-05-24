package nl.jasperbok.zombies.entity.mob;

import java.util.ArrayList;

import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

public class Zombie extends Mob {
	
	private ArrayList<Vector2f> blockingPointsLeft;
	private ArrayList<Vector2f> blockingPointsRight;
	
	public Zombie(Level level) throws SlickException {
		super.init(level);
		
		this.maxVel = new Vector2f(0.15f, 0.3f);
		
		//this.type = Entity.Type.B;
		//this.checkAgainst = Entity.Type.A;
		//this.collides = Entity.Collides.ACTIVE;
		
		this.gravityFactor = 1;
		
		this.blockingPointsLeft = new ArrayList<Vector2f>();
		this.blockingPointsRight = new ArrayList<Vector2f>();

		// Loading all the animations.
		SpriteSheet sprites = new SpriteSheet("data/sprites/entity/zombie_walk.png", 102, 150);
		Animation idleAnimation = new Animation();
		Animation walkLeftAnimation = new Animation();
		Animation walkRightAnimation = new Animation();
		idleAnimation.addFrame(sprites.getSprite(0, 0), 500);
		for (int i = 0; i < 3; i++) {
			walkLeftAnimation.addFrame(sprites.getSprite(i, 0).getFlippedCopy(false, false), 150);
		}
		for (int i = 0; i < 3; i++) {
			walkRightAnimation.addFrame(sprites.getSprite(i, 0).getFlippedCopy(true, false), 150);
		}
		
		this.anims.put("idle", idleAnimation);
		this.anims.put("walkLeft", walkLeftAnimation);
		this.anims.put("walkRight", walkRightAnimation);
		this.currentAnim = this.anims.get("idle");
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);

		// Stop when touching a left blocking point.
		for (Vector2f pl : blockingPointsLeft) {
			if (position.x + vel.x < pl.x) {
				vel.x = 0;
			}
		}
		
		// Stop when touching a right blocking point.
		for (Vector2f pr : blockingPointsRight) {
			if (position.x + vel.x > pr.x) {
				vel.x = 0;
			}
		}
		
		// Determine the animation.
		if (vel.x < 0) {
			this.currentAnim = this.anims.get("walkLeft");
		} else if (vel.x > 0) {
			this.currentAnim = this.anims.get("walkRight");
		} else {
			this.currentAnim = this.anims.get("idle");
		}
		
		// Move towards the attractors.
		Vector2f v = new Vector2f(0, 0);
		for (MobAttractor attractor : this.level.env.attractors) {
			if (this.touches(attractor.object)) {
				vel.x = 0;
				break;
			}
			if (this.position.y - attractor.object.position.y < 240 && this.position.y - attractor.object.position.y > -240) {
				v.x = ((500 * (attractor.power)) / -((this.position.x - attractor.object.position.x) * 50)) * 2;
			}
		}
		this.vel = this.vel.add(v);
		
		// Check if the player touches the zombie.
		if (this.touches(level.env.getEntityByName("player")) && level.env.getEntityByName("player").health > 0) {
			level.env.getEntityByName("player").receiveDamage(200);
		}
		
		// Check if a zombie collides with another mob.
		for (Mob mob : this.level.env.mobs) {
			if (mob != this && this.touchesFromCenter(mob, 7, 0)) {
				if (this.position.x + this.size.x / 2 < mob.position.x + mob.size.x / 2) {
					this.position.x--;
				} else if (this.position.x + this.size.x / 2 >= mob.position.x + mob.size.x / 2) {
					this.position.x++;
				}
			}
		}
		
		if (this.vel.x > this.maxVel.x) {
			this.vel.x = this.maxVel.x;
		} else if (this.vel.x < -this.maxVel.x) {
			this.vel.x = -this.maxVel.x;
		}
	}
	
	public void addBlockingPointLeft(float x) {
		blockingPointsLeft.add(new Vector2f(x, 0));
	}
	
	public void addBlockingPointRight(float x) {
		blockingPointsRight.add(new Vector2f(x, 0));
	}
}
