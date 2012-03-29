package nl.jasperbok.zombies.entity.mob;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.object.Crate;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

public class Zombie extends Mob {
	public Crate crate;
	
	// Animations
	private SpriteSheet sprites;
	private Animation idleAnimation;
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	private Animation currentAnimation;
	private boolean draw = true;
	
	public Zombie(float x, float y, Crate crate) throws SlickException {
		this.position.x = x;
		this.position.y = y;
		this.velocity.x = 0;
		this.velocity.y = 0;
		this.crate = crate;

		sprites = new SpriteSheet("data/sprites/entity/zombiesheet.png", 33, 75);
		idleAnimation = new Animation();
		idleAnimation.addFrame(sprites.getSprite(0, 0), 500);
		walkLeftAnimation = new Animation();
		for (int i = 0; i < 3; i++) {
			walkLeftAnimation.addFrame(sprites.getSprite(i, 0).getFlippedCopy(false, false), 150);
		}
		walkRightAnimation = new Animation();
		for (int i = 0; i < 3; i++) {
			walkRightAnimation.addFrame(sprites.getSprite(i, 0).getFlippedCopy(true, false), 150);
		}
		currentAnimation = idleAnimation;
		boundingBox = new Rectangle(0, 0, 4, 4);
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		//position.add(velocity);
		
		//String moveStatus = level.movingStatus(this);
		
		//if (moveStatus == "falling") {
		//	velocity.add(level.gravity);
		//}
		
		if (velocity.x < 0) {
			currentAnimation = walkLeftAnimation;
		} else if (velocity.x > 0) {
			currentAnimation = walkRightAnimation;
		} else {
			currentAnimation = idleAnimation;
		}
		
		boundingBox.setBounds(position.x, position.y, currentAnimation.getCurrentFrame().getWidth(), currentAnimation.getCurrentFrame().getHeight());
		
		if (draw) draw = !crateOnHead();
		System.out.println(crateOnHead());
	}
	
	public boolean crateOnHead() {
		System.out.println("Checking for falling crates :)");
		return crate.boundingBox.intersects(boundingBox) && crate.velocity.y > 0;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (draw) currentAnimation.draw(position.x, position.y);
	}
	
	public void zombieDie() {
		draw = false;
	}
}
