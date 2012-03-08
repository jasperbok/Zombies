package nl.jasperbok.zombies.entity.mob;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Zombie {
	private float x;
	private float y;
	private float vx;
	private float vy;
	
	private Animation idleAnimation;
	private Animation walkAnimation;
	private Animation currentAnimation;
	
	public Zombie(float x, float y) throws SlickException {
		this.x = x;
		this.y = y;
		this.vx = 0;
		this.vx = 0;
	}
	
	public void init() throws SlickException {
		
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		x += vx;
		y += vy;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw(x, y);
	}
}
