package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.math.Vector2;

public class Player {
	private int health;
	private int bandages;
	
	private TiledMap map;
	private int tileWidth;

	private float x = 390.0f;
	private float y = 200.0f;
	private Vector2 velocity = new Vector2(0.0f, 0.0f);
	private Vector2 gravity = new Vector2(0.0f, -0.002f);
	private float climbSpeed = 0.1f;
	private float walkAcceleration = 0.0006f;
	private float maxWalkSpeed = 0.2f;
	private float maxFallSpeed = 0.5f;
	private Rectangle box;
	
	private boolean wasOnGround = false;
	private boolean wasFalling = false;
	private boolean wasGoingLeft = false;
	private boolean wasGoingRight = false;
	private boolean wasClimbing = false;
	
	// Animations
	private SpriteSheet sprites;
	private Animation idleAnimation;
	private Animation walkRightAnimation;
	private Animation walkLeftAnimation;
	private Animation fallingAnimation;
	private Animation climbAnimation;
	private Animation currentAnimation;
	
	public Player(int health, int bandages, TiledMap map) throws SlickException {
		this.health = health;
		this.bandages = bandages;
		this.map = map;
		this.box = new Rectangle(x, y, 10, 10);
		this.tileWidth = map.getTileHeight();
		this.init();
	}
	
	public void init() throws SlickException {
		sprites = new SpriteSheet("data/sprites/entity/peach.png", 16, 32);
		walkRightAnimation = new Animation();
		for (int i = 0; i < 3; i++) {
			walkRightAnimation.addFrame(sprites.getSprite(i, 0), 200);
		}
		walkLeftAnimation = new Animation();
		for (int i = 0; i < 3; i++) {
			walkLeftAnimation.addFrame(sprites.getSprite(i, 0).getFlippedCopy(true, false), 200);
		}
		idleAnimation = new Animation();
		idleAnimation.addFrame(sprites.getSprite(3, 0), 500);
		climbAnimation = new Animation();
		climbAnimation.addFrame(sprites.getSprite(4, 0), 500);
		climbAnimation.addFrame(sprites.getSprite(4, 0).getFlippedCopy(true, false), 500);
		currentAnimation = idleAnimation;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		Input input = container.getInput();
		// Variables used for collision detection.
		int height = currentAnimation.getCurrentFrame().getHeight();
		int width = currentAnimation.getCurrentFrame().getWidth();
		int centerX = (int)(x + width / 2);
		int centerY = (int)(y + height / 2);
		int rightX = (int)(x + width);
		int bottomY = (int)(y + height);
		
		box.setSize(width - 4, height - 4);
		box.setCenterX(centerX);
		box.setCenterY(centerY);
		
		// Positions in the tile system.
		int yTiled = (int)(Math.floor(y / tileWidth));
		int xTiled = (int)(Math.floor(x / tileWidth));
		int centerXTiled = (int)(Math.floor(centerX / tileWidth));
		int centerYTiled = (int)(Math.floor(centerY / tileWidth));
		int rightXTiled = (int)(Math.floor((x + width) / tileWidth));
		int bottomYTiled = (int)(Math.floor((y + height) / tileWidth));
		
		int bottomTileId = map.getTileId(centerXTiled, bottomYTiled, 0);
		int rightTileId = map.getTileId(rightXTiled, centerYTiled, 0);
		int leftTileId = map.getTileId(xTiled, centerYTiled, 0);
		int centerTileId = map.getTileId(centerXTiled, centerYTiled, 0);
		int topTileId = map.getTileId(centerXTiled, yTiled, 0);
		int tileUnderneathId = map.getTileId(centerXTiled, yTiled + 1, 0);
		
		boolean isFalling = false;
		boolean isJumping = false;
		boolean isClimbing = false;
		boolean isOnGround = false;
		
		// If the player isn't standing on something AND not climbing, apply gravity:
		if ("false".equals(map.getTileProperty(tileUnderneathId, "blocked", "false"))) {
			isFalling = true;
			//if (velocity.y <= 0.05f) velocity.y += gravity.y;
		}
		
		if ("true".equals(map.getTileProperty(tileUnderneathId, "blocked", "false"))) {
			isOnGround = true;
			isFalling = false;
		}
		
		// Climbing?
		if (wasClimbing && ("true".equals(map.getTileProperty(topTileId, "climable", "false")) ||
					"true".equals(map.getTileProperty(bottomTileId, "climable", "false")))) {
			isClimbing = true;
			isFalling = false;
		}
		
		// Apply vertical forces according to state.
		if (isFalling) velocity.y += gravity.y * delta;
		if (isOnGround || isClimbing) velocity.y = 0;
		
		// Check player input.
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			if (currentAnimation != walkRightAnimation) currentAnimation = walkRightAnimation;
			velocity.x += walkAcceleration * delta;
			if (velocity.x > maxWalkSpeed) velocity.x = maxWalkSpeed;
		}
		if (input.isKeyDown(Input.KEY_LEFT)) {
			if (currentAnimation != walkLeftAnimation) currentAnimation = walkLeftAnimation;
			velocity.x -= walkAcceleration * delta;
			if (velocity.x < -maxWalkSpeed) velocity.x = -maxWalkSpeed;
		}
		if (!input.isKeyDown(Input.KEY_RIGHT) && !input.isKeyDown(Input.KEY_LEFT)) {
			if (velocity.x < 0.0f) {
				velocity.x += walkAcceleration * 2 * delta;
				if (velocity.x > 0.0f) velocity.x = 0.0f;
			} else if (velocity.x > 0.0f) {
				velocity.x -= walkAcceleration * 2 * delta;
				if (velocity.x < 0.0f) velocity.x = 0.0f;
			}
		}
		if (input.isKeyDown(Input.KEY_UP)) {
			if ("true".equals(map.getTileProperty(topTileId, "climable", "false")) ||
					"true".equals(map.getTileProperty(bottomTileId, "climable", "false"))) {
				if (isClimbing) velocity.y += climbSpeed;
				wasClimbing = true;
			}
		}
		if (input.isKeyDown(Input.KEY_DOWN)) {
			if ("true".equals(map.getTileProperty(topTileId, "climable", "false")) ||
					"true".equals(map.getTileProperty(bottomTileId, "climable", "false"))) {
				if (isClimbing) velocity.y -= climbSpeed;
				wasClimbing = true;
			}
		}
		
		if (isClimbing) currentAnimation = climbAnimation;
		
		x += velocity.x * delta;
		y -= velocity.y * delta;
		
		// If the player is now colliding with something, get him out of it.
		// Check for bottom collisions.
		if ("true".equals(map.getTileProperty(bottomTileId, "blocked", "false"))) {
			if (velocity.y < 0) velocity.y = 0;
			y -= bottomY % 32;
		}
		// Check for right collisions.
		if ("true".equals(map.getTileProperty(rightTileId, "blocked", "false"))) {
			if (velocity.x > 0) velocity.x = 0;
			x -= rightX % 32;
			wasGoingRight = false;
		}
		// Check for left collisions.
		if ("true".equals(map.getTileProperty(leftTileId, "blocked", "false"))) {
			if (velocity.x < 0) velocity.x = 0;
			x += tileWidth - (x % tileWidth);
			wasGoingLeft = false;
		}
	}
	
	public void hurt(int amount) {
		health -= amount;
		Hud.getInstance().setPlayerHealth(health);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw((int)x, (int)y);
	}
}
