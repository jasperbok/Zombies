package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import nl.jasperbok.zombies.gui.Notifications;

public class Player {
	private int health;
	private int bandages;
	
	private float walkSpeed;
	private float vx;
	private float vy;
	private float x;
	private float y;
	
	// Animations
	private Animation idleAnimation;
	private Animation walkAnimation;
	private Animation fallingAnimation;
	private Animation currentAnimation;
	
	public Player(Animation walk, int health, int bandages) throws SlickException {
		this.health = health;
		this.bandages = bandages;
		this.walkAnimation = walk;
		this.init();
	}
	
	public void init() throws SlickException {
		walkSpeed = 0.2f;
		vx = 0.0f;
		vy = 0.0f;
		x = 200.0f;
		y = 200.0f;
		currentAnimation = walkAnimation;
	}
	
	public void update(int delta) throws SlickException {
		if (true) {
			currentAnimation = walkAnimation;
			vx = walkSpeed;
		}
		
		x += vx;
		y += vy;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw((int)x, (int)y);
	}
}
