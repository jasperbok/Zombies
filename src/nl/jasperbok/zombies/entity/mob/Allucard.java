package nl.jasperbok.zombies.entity.mob;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.math.Vector2;

public class Allucard {
	private Vector2 position = new Vector2(200.0f, 200.0f);
	private Vector2 velocity = new Vector2(0.0f, 0.0f);
	
	private SpriteSheet sprites;
	private Animation idleAnimation;
	private Animation currentAnimation;
	
	public boolean isBeingControlled = false;
	
	public Allucard() throws SlickException {
		sprites = new SpriteSheet("data/sprites/entity/allucard.png", 25, 48);
		idleAnimation = new Animation();
		for (int i = 0; i < 6; i++) {
			idleAnimation.addFrame(sprites.getSprite(i, 0), 250);
		}
		currentAnimation = idleAnimation;
	}
	
	public void init() throws SlickException {
		
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw(position.x, position.y);
	}
}
