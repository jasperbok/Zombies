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
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.entity.mob.Mob;
import nl.jasperbok.zombies.entity.object.WoodenCrate;

public class Player extends Mob {
	private float climbSpeed = 0.1f;
	private float walkAcceleration = 0.06f;
	private float maxWalkSpeed = 0.2f;
	private float maxFallSpeed = 2f;
	
	// Status variables.
	protected boolean wasGoingLeft = false;
	protected boolean wasGoingRight = false;
	
	public Player(int health, Level level) throws SlickException {
		this.health = health;
		super.init(level);
		this.init();
	}
	
	public void init() throws SlickException {
		this.addComponent(new GravityComponent(0.01f, this));
		gravityAffected = false;
		position = new Vector2(280.0f, 300.0f);
		playerControlled = true;
		boundingBox = new Rectangle(position.x, position.y, 10, 10);
		
		// Fix the walking animations.
		SpriteSheet walkSprites = new SpriteSheet("data/sprites/entity/walksheet_no_arms_girl.png", 75, 150);
		walkRightAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkRightAnimation.addFrame(walkSprites.getSprite(i, 0), 150);
		}
		walkLeftAnimation = new Animation();
		for (int i = 0; i < 4; i++) {
			walkLeftAnimation.addFrame(walkSprites.getSprite(i, 0).getFlippedCopy(true, false), 150);
		}
		
		// Fix the idle animation.
		SpriteSheet idleSprites = new SpriteSheet("data/sprites/entity/girl_stand.png", 51, 166);
		idleAnimation = new Animation();
		idleAnimation.addFrame(idleSprites.getSprite(0, 0), 500);
		
		// Fix the climb animation.
		SpriteSheet climbSprites = new SpriteSheet("data/sprites/entity/girl_climb_sprite.png", 56, 147);
		climbAnimation = new Animation();
		climbAnimation.addFrame(climbSprites.getSprite(0, 0), 250);
		climbAnimation.addFrame(climbSprites.getSprite(0, 0).getFlippedCopy(true, false), 250);
		
		// Set the initial animation.
		currentAnimation = idleAnimation;
	}
	
	protected void updateBoundingBox() {
		this.boundingBox.setBounds(position.x, position.y, currentAnimation.getCurrentFrame().getWidth(), currentAnimation.getCurrentFrame().getHeight());
	}
	
	public void update(Input input, int delta) {
		updateBoundingBox();
		
		wasClimbing = isClimbing;
		
		this.isOnGround = level.env.isOnGround(this, false);
		if (wasClimbing && level.env.isOnClimableSurface(this)) {
			isClimbing = true;
			velocity.set(new Vector2f(velocity.getX(), 0));
		} else if (!level.env.isOnClimableSurface(this)) {
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
			// Handle player input.
			if (input.isKeyDown(Input.KEY_D)) {
				// Move the player right.
				velocity.x += walkAcceleration;
				if (velocity.x > maxWalkSpeed) velocity.x = maxWalkSpeed;
			}
			if (input.isKeyDown(Input.KEY_A)) {
				// Move the player left.
				velocity.x -= walkAcceleration;
				if (velocity.x < -maxWalkSpeed) velocity.x = -maxWalkSpeed;
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
				if (level.env.isOnClimableSurface(this)) {
					isClimbing = true;
					velocity.set(velocity.getX(), -climbSpeed);
				}
			}
			if (input.isKeyDown(Input.KEY_S)){
				if (level.env.isOnClimableSurface(this)) {
					isClimbing = true;
					velocity.set(velocity.getX(), climbSpeed);
				}
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				useObject();
			}
			if (input.isKeyPressed(Input.KEY_SPACE)) {
				climbObject();
			}
			if (input.isMousePressed(0)) {
				level.fl.switchOnOff();
			}
		}
		
		// Decide what animation should be played.
		if (isOnGround) {
			if (velocity.getX() < 0f) {
				currentAnimation = walkLeftAnimation;
			} else if (velocity.getX() > 0f) {
				currentAnimation = walkRightAnimation;
			} else if (velocity.getX() == 0f) {
				currentAnimation = idleAnimation;
			}
		} else if (isClimbing) {
			currentAnimation = climbAnimation;
			if (!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)) {
				currentAnimation.stop();
			} else if (currentAnimation.isStopped()) {
				currentAnimation.start();
			}
		} else {
			// Not on ground and not climbing, surely the player is falling!
			//currentAnimation = fallAnimation;
		}
		
		//System.out.println(isOnGround);
		super.update(input, delta);
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
	
	private void climbObject() {
		Entity target = (Entity) level.env.getUsableEntity(boundingBox);
		if (target != null && target instanceof WoodenCrate) {
			this.setPosition(target.position.getX(), target.position.getY() - this.boundingBox.getHeight());
		}
	}
	
	public void hurt(int amount) {
		health -= amount;
		Hud.getInstance().setPlayerHealth(health);
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw((int)renderPosition.getX(), (int)renderPosition.getY());
	}
}
