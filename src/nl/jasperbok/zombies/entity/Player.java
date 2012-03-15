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

import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Player extends Entity {
	private int health;
	private int bandages;
	
	private TiledMap map;
	private int tileWidth;

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
	private Animation fallAnimation;
	private Animation climbAnimation;
	private Animation currentAnimation;
	
	public Player(int health, int bandages, TiledMap map, Level level) throws SlickException {
		this.health = health;
		this.bandages = bandages;
		this.map = map;
		this.tileWidth = map.getTileHeight();
		super.init(level);
		this.init();
	}
	
	public void init() throws SlickException {
		position = new Vector2(280.0f, 300.0f);
		playerControlled = true;
		boundingBox = new Rectangle(position.x, position.y, 10, 10);
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
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		Input input = container.getInput();
		// Variables used for collision detection.
		/*
		int height = currentAnimation.getCurrentFrame().getHeight();
		int width = currentAnimation.getCurrentFrame().getWidth();
		int centerX = (int)(position.x + width / 2);
		int centerY = (int)(position.y + height / 2);
		int rightX = (int)(position.x + width);
		int bottomY = (int)(position.y + height);
		*/
		
		//boundingBox.setBounds(position.x, position.y, width, height);
		boundingBox.setBounds(position.x, position.y, currentAnimation.getCurrentFrame().getWidth(), currentAnimation.getCurrentFrame().getHeight());
		
		// Positions in the tile system.
		/*
		int yTiled = (int)(Math.floor(position.y / tileWidth));
		int xTiled = (int)(Math.floor(position.x / tileWidth));
		int centerXTiled = (int)(Math.floor(centerX / tileWidth));
		int centerYTiled = (int)(Math.floor(centerY / tileWidth));
		int rightXTiled = (int)(Math.floor((position.x + width) / tileWidth));
		int bottomYTiled = (int)(Math.floor((position.y + height) / tileWidth));
		
		int bottomTileId = map.getTileId(centerXTiled, bottomYTiled, 0);
		int rightTileId = map.getTileId(rightXTiled, centerYTiled, 0);
		int leftTileId = map.getTileId(xTiled, centerYTiled, 0);
		int centerTileId = map.getTileId(centerXTiled, centerYTiled, 0);
		int topTileId = map.getTileId(centerXTiled, yTiled, 0);
		int tileUnderneathId = map.getTileId(centerXTiled, yTiled + 1, 0);
		*/
		boolean isFalling = false;
		boolean isJumping = false;
		boolean isClimbing = false;
		boolean isOnGround = false;
		
		/*
		// If the player isn't standing on something AND not climbing, apply gravity:
		if ("false".equals(map.getTileProperty(tileUnderneathId, "blocked", "false"))) {
			isFalling = true;
			//if (velocity.y <= 0.05f) velocity.y += gravity.y;
		} else {
			isOnGround = true;
			isFalling = false;
		}
		
		// Climbing?
		if (wasClimbing && ("true".equals(map.getTileProperty(topTileId, "climable", "false")) ||
					"true".equals(map.getTileProperty(bottomTileId, "climable", "false")))) {
			isClimbing = true;
			isFalling = false;
		}
		*/
		String moveStatus = level.movingStatus(this);
		if (moveStatus == "falling") {
			if (isClimbing) {
				
			}
			isFalling = true;
		} else {
			isOnGround = true;
		}
		
		// Apply vertical forces according to state.
		if (isFalling) velocity.y += gravity.y * delta;
		if (isOnGround || isClimbing) velocity.y = 0;
		
		if (playerControlled) {
			// Check player input.
			if (input.isKeyDown(Input.KEY_D)) {
				if (currentAnimation != walkRightAnimation) currentAnimation = walkRightAnimation;
				velocity.x += walkAcceleration * delta;
				if (velocity.x > maxWalkSpeed) velocity.x = maxWalkSpeed;
			}
			if (input.isKeyDown(Input.KEY_A)) {
				if (currentAnimation != walkLeftAnimation) currentAnimation = walkLeftAnimation;
				velocity.x -= walkAcceleration * delta;
				if (velocity.x < -maxWalkSpeed) velocity.x = -maxWalkSpeed;
			}
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
		}
		
		if (isClimbing) currentAnimation = climbAnimation;
		
		position.x += velocity.x * delta;
		position.y -= velocity.y * delta;
		
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
		
		// If the player is now colliding with something, get him out of it.
		// Check for bottom collisions.
		if ("true".equals(map.getTileProperty(bottomTileId, "blocked", "false"))) {
			if (velocity.y < 0) velocity.y = 0;
			position.y -= bottomY % 32;
		}
		// Check for right collisions.
		if ("true".equals(map.getTileProperty(rightTileId, "blocked", "false"))) {
			if (velocity.x > 0) velocity.x = 0;
			position.x -= rightX % 32;
			wasGoingRight = false;
		}
		// Check for left collisions.
		if ("true".equals(map.getTileProperty(leftTileId, "blocked", "false"))) {
			if (velocity.x < 0) velocity.x = 0;
			position.x += tileWidth - (position.x % tileWidth);
			wasGoingLeft = false;
		}
	}
	
	public void hurt(int amount) {
		health -= amount;
		Hud.getInstance().setPlayerHealth(health);
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw((int)position.x, (int)position.y);
	}
}
