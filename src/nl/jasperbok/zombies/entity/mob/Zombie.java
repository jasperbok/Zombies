package nl.jasperbok.zombies.entity.mob;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.object.Crate;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Zombie extends Entity {
	public Crate crate;
	
	// Animations
	private SpriteSheet sprites;
	private Animation idleAnimation;
	private Animation currentAnimation;
	
	public Zombie(float x, float y, Crate crate) throws SlickException {
		this.position.x = x;
		this.position.y = y;
		this.velocity.x = 0;
		this.velocity.y = 0;
		this.crate = crate;
	}
	
	public void init() throws SlickException {
		sprites = new SpriteSheet("data/sprites/entity/zombie.png", 33, 75);
		
		idleAnimation = new Animation();
		idleAnimation.addFrame(sprites.getSprite(0, 0), 500);
		
		currentAnimation = idleAnimation;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		position.add(velocity);
		
		String moveStatus = level.movingStatus(this);
		
		if (moveStatus == "falling") {
			velocity.add(level.gravity);
		}
		
		boundingBox.setBounds(position.x, position.y, currentAnimation.getCurrentFrame().getWidth(), currentAnimation.getCurrentFrame().getHeight());
	}
	
	public boolean crateOnHead() {
		//if (crate.boundingBox.intersects(boundingBox) && crate.velocity.y > 0)
		//	return true;
		return false;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw(position.x, position.y);
	}
}
