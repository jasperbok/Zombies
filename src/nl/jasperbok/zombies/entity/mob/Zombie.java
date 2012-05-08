package nl.jasperbok.zombies.entity.mob;

import java.util.ArrayList;

import nl.jasperbok.zombies.entity.component.DamagingAuraComponent;
import nl.jasperbok.zombies.entity.component.LifeComponent;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Zombie extends Mob {
	
	private ArrayList<Vector2f> blockingPointsLeft;
	private ArrayList<Vector2f> blockingPointsRight;
	
	public Zombie(Level level) throws SlickException {
		super.init(level);
		
		this.maxVel = new Vector2f(10, 0);
		
		this.addComponent(new LifeComponent(this, 1));
		this.addComponent(new GravityComponent(0.01f, this));
		this.addComponent(new DamagingAuraComponent(this, 0, 5));
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
		boundingBox = new Rectangle(0, 0, 4, 4);
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
		
		updateBoundingBox();
		super.update(input, delta);
	}
	
	public void addBlockingPointLeft(float x) {
		blockingPointsLeft.add(new Vector2f(x, 0));
	}
	
	public void addBlockingPointRight(float x) {
		blockingPointsRight.add(new Vector2f(x, 0));
	}
}
