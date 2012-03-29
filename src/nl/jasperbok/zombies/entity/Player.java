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
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;
import nl.jasperbok.zombies.entity.mob.Mob;

public class Player extends Mob {
	private int bandages;
	
	private TiledMap map;
	private int tileWidth;

	private Vector2 gravity = new Vector2(0.0f, -0.002f);
	private float climbSpeed = 0.1f;
	private float walkAcceleration = 0.0006f;
	private float maxWalkSpeed = 0.035f;
	private float maxFallSpeed = 0.5f;
	private Rectangle box;
	
	// Animations
	private SpriteSheet sprites;
	private Animation idleAnimation;
	private Animation walkRightAnimation;
	private Animation walkLeftAnimation;
	private Animation fallAnimation;
	private Animation climbAnimation;
	private Animation currentAnimation;
	
	// Status variables.
	protected boolean wasOnGround = false;
	protected boolean wasFalling = false;
	protected boolean wasGoingLeft = false;
	protected boolean wasGoingRight = false;
	protected boolean wasClimbing = false;
	
	public Player(int health, int bandages, Level level) throws SlickException {
		this.health = health;
		this.bandages = bandages;
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
	
	public void update(Input input, int delta) {		
		this.boundingBox.setBounds(position.x, position.y, currentAnimation.getCurrentFrame().getWidth(), currentAnimation.getCurrentFrame().getHeight());
		
		boolean isFalling = false;
		boolean isJumping = false;
		boolean isClimbing = false;
		boolean isOnGround = false;

		/*
		// If the player isn't standing on something AND not climbing, he must be falling:
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

		// Apply vertical forces according to state.
		if (isFalling) velocity.y += gravity.y * delta;
		if (isOnGround || isClimbing) velocity.y = 0;
		*/
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
			/*
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
			}*/
			if (input.isKeyPressed(Input.KEY_E)) {
				Usable target = level.env.getUsableEntity(boundingBox);
				if (target != null) {
					target.use(this);
				}
			}
		}
		/*
		if (isOnGround && (!input.isKeyDown(Input.KEY_A)) && (!input.isKeyDown(Input.KEY_D))) {
			currentAnimation = idleAnimation;
		}
		
		if (isClimbing) {
			currentAnimation = climbAnimation;
			if (!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)) {
				currentAnimation.stop();
			} else if (currentAnimation.isStopped()) {
				currentAnimation.start();
			}
		}*/
	}
	
	public void hurt(int amount) {
		health -= amount;
		Hud.getInstance().setPlayerHealth(health);
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw((int)position.x, (int)position.y);
	}
}
