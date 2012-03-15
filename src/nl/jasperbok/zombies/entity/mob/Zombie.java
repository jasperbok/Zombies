package nl.jasperbok.zombies.entity.mob;

import nl.jasperbok.zombies.entity.Entity;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Zombie extends Entity {
	private Animation idleAnimation;
	private Animation walkAnimation;
	private Animation currentAnimation;
	
	public Zombie(float x, float y) throws SlickException {
		this.position.x = x;
		this.position.y = y;
		this.velocity.x = 0;
		this.velocity.y = 0;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		position.add(velocity);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw(position.x, position.y);
	}
}
