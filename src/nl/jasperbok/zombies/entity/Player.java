package nl.jasperbok.zombies.entity;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Player extends Actor {
	private int health;
	private int bandages;

	private float climbSpeed = 0.1f;
	private float walkAcceleration = 0.0006f;
	private float maxWalkSpeed = 0.2f;
	private float maxFallSpeed = 0.5f;
	
	// Animations
	private SpriteSheet sprites;
	private Animation idleAnimation;
	private Animation walkRightAnimation;
	private Animation walkLeftAnimation;
	private Animation fallAnimation;
	private Animation climbAnimation;
	private Animation currentAnimation;
	
	public Player(int health, int bandages, float x, float y, float mass, float size) throws SlickException {
		super(x, y, mass, size);
		this.health = health;
		this.bandages = bandages;
		this.init();
	}
	
	public void init() throws SlickException {
		position = new Vector2(280.0f, 300.0f);
		playerControlled = true;
		sprites = new SpriteSheet("data/sprites/entity/player.png", 33, 75);
		walkRightAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkRightAnimation.addFrame(sprites.getSprite(i, 1).getFlippedCopy(true, false), 150);
		}
		walkLeftAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkLeftAnimation.addFrame(sprites.getSprite(i, 1), 150);
		}
		idleAnimation = new Animation();
		idleAnimation.addFrame(sprites.getSprite(0, 0), 500);
		climbAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			climbAnimation.addFrame(sprites.getSprite(i, 2), 250);
		}
		currentAnimation = idleAnimation;
		velocityX = 500;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		Input input = container.getInput();
		
		if (playerControlled) {
			// Check player input.
			if (input.isKeyDown(Input.KEY_D)) {
				if (currentAnimation != walkRightAnimation) currentAnimation = walkRightAnimation;
				//velocity.x += walkAcceleration * delta;
				applyForce(500f, 0f);
				if (getVelX() > maxWalkSpeed) setVelocity(maxWalkSpeed, getVelY());
			}
			if (input.isKeyDown(Input.KEY_A)) {
				if (currentAnimation != walkLeftAnimation) currentAnimation = walkLeftAnimation;
				applyForce(walkAcceleration * delta, 0f);
				if (getVelX() < -maxWalkSpeed) setVelocity(maxWalkSpeed, getVelY());
			}
			/*
			if (!input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_A)) {
				if (velocity.x < 0.0f) {
					velocity.x += walkAcceleration * 2 * delta;
					if (velocity.x > 0.0f) velocity.x = 0.0f;
				} else if (velocity.x > 0.0f) {
					velocity.x -= walkAcceleration * 2 * delta;
					if (velocity.x < 0.0f) velocity.x = 0.0f;
				}
			}
			if (input.isKeyDown(Input.KEY_W)) {
				if ("true".equals(map.getTileProperty(topTileId, "climable", "false")) ||
						"true".equals(map.getTileProperty(bottomTileId, "climable", "false"))) {
					if (isClimbing) velocity.y += climbSpeed;
					wasClimbing = true;
				}
			}
			if (input.isKeyDown(Input.KEY_S)) {
				if ("true".equals(map.getTileProperty(topTileId, "climable", "false")) ||
						"true".equals(map.getTileProperty(bottomTileId, "climable", "false"))) {
					if (isClimbing) velocity.y -= climbSpeed;
					wasClimbing = true;
				}
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				Usable target = level.findUsableObject(boundingBox);
				if (target != null) {
					target.use(this);
				}
			}
			*/
		}
		
		//if (isClimbing) currentAnimation = climbAnimation;
		
		//position.x += velocity.x * delta;
		//position.y -= velocity.y * delta;
		
		/*
		ArrayList<Entity> touchingEnts = level.touchingSolidObject(this);
		for (Entity ent: touchingEnts) {
			System.out.println("BLOCKING! :D");
			boolean[] intersections = level.findIntersects(this, ent);
			if (ent.isBlocking) {
				if (intersections[0]) position.y += 1.0f;
				if (intersections[1]) position.x -= 1.0f;
				if (intersections[2]) position.y -= 1.0f;
				if (intersections[3]) position.x += 1.0f;
			}
		}
		*/
	}
	
	public void hurt(int amount) {
		health -= amount;
		Hud.getInstance().setPlayerHealth(health);
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw((int)body.getPosition().getX(), (int)body.getPosition().getY());
	}
}
