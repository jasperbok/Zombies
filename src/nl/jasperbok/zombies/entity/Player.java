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
import nl.jasperbok.zombies.entity.component.PlayerInputComponent;
import nl.jasperbok.zombies.entity.mob.Mob;
import nl.jasperbok.zombies.entity.object.WoodenCrate;

public class Player extends Mob {
	public float climbSpeed = 0.1f;
	private float maxWalkSpeed = 0.2f;
	
	// Status variables.
	protected boolean wasGoingLeft = false;
	protected boolean wasGoingRight = false;
	
	public Player(int health, Level level) throws SlickException {
		this.health = health;
		super.init(level);
		this.addComponent(new GravityComponent(0.01f, this));
		this.addComponent(new PlayerInputComponent(this));
		this.acceleration = new Vector2f(0.06f, 0);
		this.maxVelocity = new Vector2f(0.1f, 10f);
		this.position = new Vector2(280.0f, 300.0f);
		this.playerControlled = true;
		this.boundingBox = new Rectangle(position.x, position.y, 10, 10);
		this.init();
	}
	
	public void init() throws SlickException {		
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
		isClimbing = false;
		
		this.isOnGround = level.env.isOnGround(this, false);
		if (wasClimbing && level.env.isOnClimableSurface(this)) {
			isClimbing = true;
			velocity.set(new Vector2f(velocity.getX(), 0));
		} else if (!level.env.isOnClimableSurface(this)) {
			isClimbing = false;
		}
		
		super.update(input, delta);
		
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
	}
	
	public void addBloodMark() {
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
