package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;
import nl.jasperbok.zombies.entity.mob.Mob;

public class Player extends Mob {
	private int bandages;

	private float climbSpeed = 0.001f;
	private float walkAcceleration = 0.06f;
	private float maxWalkSpeed = 0.2f;
	private float maxFallSpeed = 2f;
	
	// Animations
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
	public boolean isClimbing = false;
	
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
		SpriteSheet sprites = new SpriteSheet("data/sprites/entity/player.png", 33, 75);
		SpriteSheet walkSprites = new SpriteSheet("data/sprites/entity/walksheet_girl2.png", 75, 150);
		walkRightAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkRightAnimation.addFrame(sprites.getSprite(i, 1).getFlippedCopy(true, false), 150);
		}
		walkLeftAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkLeftAnimation.addFrame(sprites.getSprite(i, 1), 150);
		}
		walkRightAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkRightAnimation.addFrame(walkSprites.getSprite(i, 1).getFlippedCopy(true, false), 150);
		}
		walkLeftAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkLeftAnimation.addFrame(walkSprites.getSprite(i, 1), 150);
		}
		//walkLeftAnimation = new Animation();
		//walkLeftAnimation.addFrame(new SpriteSheet("data/sprites/entity/girl_stand.png", 51, 166).getSprite(0, 0), 5000);
		idleAnimation = new Animation();
		idleAnimation.addFrame(sprites.getSprite(0, 0), 500);
		climbAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			climbAnimation.addFrame(sprites.getSprite(i, 2), 250);
		}
		currentAnimation = idleAnimation;
		//walkRightAnimation = walkLeftAnimation;
		//currentAnimation = walkLeftAnimation;
	}
	
	protected void updateBoundingBox() {
		this.boundingBox.setBounds(position.x, position.y, currentAnimation.getCurrentFrame().getWidth(), currentAnimation.getCurrentFrame().getHeight());
	}
	
	public void update(Input input, int delta) {		
		updateBoundingBox();
		
		if (isClimbing && level.env.canClimbHere(boundingBox)) {
			velocity.set(new Vector2f(velocity.getX(), 0));
		} else if (!level.env.canClimbHere(boundingBox)) {
			isClimbing = false;
		}

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
				moveRight();
			}
			if (input.isKeyDown(Input.KEY_A)) {
				moveLeft();
			}
			if (!input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_A)) {
				if (velocity.x < 0.0f) {
					velocity.x += walkAcceleration * 2;
					if (velocity.x > 0.0f) velocity.x = 0.0f;
				} else if (velocity.x > 0.0f) {
					velocity.x -= walkAcceleration * 2;
					if (velocity.x < 0.0f) velocity.x = 0.0f;
				}
			}
			if (input.isKeyDown(Input.KEY_Q)) {
				addBloodMark();
			}
			if (input.isKeyDown(Input.KEY_W)){
				if (level.env.canClimbHere(boundingBox)) {
					isClimbing = true;
					velocity.set(velocity.getX(), -climbSpeed);
				}
			}
			if (input.isKeyDown(Input.KEY_S)){
				if (level.env.canClimbHere(boundingBox)) {
					isClimbing = true;
					velocity.set(velocity.getX(), climbSpeed);
				}
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				useObject();
			}
			if (input.isMousePressed(0)) {
				level.fl.switchOnOff();
			}
			
			if (isClimbing) currentAnimation = climbAnimation;
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
	
	private void moveRight() {
		if (currentAnimation != walkRightAnimation) currentAnimation = walkRightAnimation;
		velocity.x += walkAcceleration;
		if (velocity.x > maxWalkSpeed) velocity.x = maxWalkSpeed;
	}
	
	private void moveLeft() {
		if (currentAnimation != walkLeftAnimation) currentAnimation = walkLeftAnimation;
		velocity.x -= walkAcceleration;
		if (velocity.x < -maxWalkSpeed) velocity.x = -maxWalkSpeed;
	}
	
	private void addBloodMark() {
		try {
			level.env.addAttractor(boundingBox, "BloodMark");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	private void useObject() {
		Usable target = level.env.getUsableEntity(boundingBox);
		if (target != null) {
			target.use(this);
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
