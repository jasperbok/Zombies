package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.entity.component.LifeComponent;
import nl.jasperbok.zombies.entity.component.PlayerInputComponent;
import nl.jasperbok.zombies.entity.item.Inventory;
import nl.jasperbok.zombies.gui.Hud;

public class Player extends Entity {
	public float climbSpeed = 0.1f;
	/**
	 * Variables for states the player can be in.
	 */
	public boolean isCrawling = false;
	private boolean isClimbingObject = false;
	/**
	 * Status variables.
	 */
	protected boolean wasGoingLeft = false;
	protected boolean wasGoingRight = false;
	
	public Player(Level level, Vector2f pos) throws SlickException {
		super.init(level);
		this.zIndex = -1;
		this.type = Entity.Type.A;
		this.checkAgainst = Entity.Type.B;
		this.collides = Entity.Collides.ACTIVE;
		this.addComponent(new GravityComponent(0.01f, this));
		this.addComponent(new PlayerInputComponent(this));
		this.addComponent(new LifeComponent(this, 5));
		this.accel = new Vector2f(0.06f, 0);
		this.maxVel = new Vector2f(0.3f, 10f);
		this.position = pos;
		this.playerControlled = true;
		this.boundingBox = new Rectangle(position.x, position.y, 10, 10);
		
		this.facing = Entity.RIGHT;
		this.animSheet = new SpriteSheet("data/sprites/entity/player_walk.png", 75, 150);
		this.addAnim("walkRight", 100, new int[]{0, 1, 2, 3, 4, 5, 6, 7});
		this.addAnim("walkLeft", 100, new int[]{15, 14, 13, 12, 11, 10, 9, 8});
		this.addAnim("idleRight", 5000, new int[]{0});
		this.addAnim("idleLeft", 5000, new int[]{15});
		this.animSheet = new SpriteSheet("data/sprites/entity/player_climb_on_object.png", 110, 240);
		this.addAnim("climbOnObject", 100, new int[]{0, 1, 2, 3, 4});
		this.animSheet = new SpriteSheet("data/sprites/entity/player_climb.png", 75, 147);
		this.addAnim("climb", 250, new int[]{0, 1});
		this.animSheet = new SpriteSheet("data/sprites/entity/player_hide.png", 75, 150);
		this.addAnim("hide", 5000, new int[]{0});
		this.animSheet = new SpriteSheet("data/sprites/entity/player_crawl.png", 140, 75);
		this.addAnim("crawlLeft", 250, new int[]{0, 1});
		this.addAnim("crawlRight", 250, new int[]{2, 3});
		this.currentAnim = this.anims.get("idleRight");
		
		inventory = new Inventory();
	}
	
	public void update(Input input, int delta) {
		updateBoundingBox();
		
		wasClimbing = isClimbing;
		isClimbing = false;
		
		this.standing = level.env.isOnGround(this, false);
		if (wasClimbing && level.env.isOnClimableSurface(this)) {
			isClimbing = true;
			vel.set(new Vector2f(vel.getX(), 0));
		} else if (!level.env.isOnClimableSurface(this)) {
			isClimbing = false;
		}
		
		try {
			Hud.getInstance().setPlayerHealth(((LifeComponent)getComponent(Component.LIFE)).getHealth());
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.update(input, delta);
		
		// Decide what animation should be played.
		if (isHidden()) {
			this.currentAnim = this.anims.get("hide");
		} else if (this.isClimbingObject) {
			if (this.currentAnim.getFrame() == 4) {
				this.isClimbingObject = false;
				this.position.y -= 2;
				((GravityComponent)this.getComponent(Component.GRAVITY)).toggleGravity();
			}
		} else if (isClimbing) {
			this.currentAnim = this.anims.get("climb");
			if (!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)) {
				this.currentAnim.stop();
			} else if (this.currentAnim.isStopped()) {
				this.currentAnim.start();
			}
		} else if (standing && isCrawling) {
			if (vel.getX() < 0f) {
				this.facing = Entity.LEFT;
				this.currentAnim = this.anims.get("crawlLeft");
			} else if (vel.getX() > 0f) {
				this.facing = Entity.RIGHT;
				this.currentAnim =this.anims.get("crawlRight");
			} else if (vel.getX() == 0f) {
				if (this.facing == Entity.LEFT) {
					this.currentAnim = this.anims.get("crawlLeft");
				} else {
					this.currentAnim = this.anims.get("crawlRight");
				}
			}
		} else if (standing) {
			if (vel.getX() < 0f) {
				this.facing = Entity.LEFT;
				this.currentAnim = this.anims.get("walkLeft");
				if (!this.level.env.sounds.isSFXPlaying("footstep") && (this.currentAnim.getFrame() == 3 || this.currentAnim.getFrame() == 7)) {
					this.level.env.sounds.playSFX("footstep");
				}
			} else if (vel.getX() > 0f) {
				this.facing = Entity.RIGHT;
				this.currentAnim =this.anims.get("walkRight");
				if (!this.level.env.sounds.isSFXPlaying("footstep") && (this.currentAnim.getFrame() == 3 || this.currentAnim.getFrame() == 7)) {
					this.level.env.sounds.playSFX("footstep");
				}
			} else if (vel.getX() == 0f) {
				if (this.facing == Entity.LEFT) {
					this.currentAnim = this.anims.get("idleLeft");
				} else {
					this.currentAnim = this.anims.get("idleRight");
				}
			}
		} else {
			// Not on ground and not climbing, surely the player is falling!
			//currentAnimation = fallAnimation;
		}
	}
	
	/*
	private void climbObject() {
		Entity target = (Entity) level.env.getUsableEntity(boundingBox);
		if (target != null && target instanceof WoodenCrate) {
			this.setPosition(target.position.getX(), target.position.getY() - this.boundingBox.getHeight());
		}
	}
	*/
	
	/**
	 * Makes the player climb on top of an object.
	 * 
	 * @param target
	 */
	public void climbOnObject(Entity target) {
		((GravityComponent)this.getComponent(Component.GRAVITY)).toggleGravity();
		this.position.y -= target.boundingBox.getHeight();
		this.currentAnim = this.anims.get("climbOnObject");
		this.isClimbingObject = true;
	}
	
	/**
	 * Hides the player if the player was not hidden and unhides if the player was hidden.
	 * The tiles behind the player must be hideable.
	 */
	public void switchHide() {
		LifeComponent lifeComponent = (LifeComponent)getComponent(Component.LIFE);
		if (lifeComponent.getDamageable() == true) {
			hide();
		} else {
			unHide();
		}
	}
	
	/**
	 * Hides the player if the current tiles are hideable and the player is not hidden.
	 */
	public void hide() {
		LifeComponent lifeComponent = (LifeComponent)getComponent(Component.LIFE);
		if (lifeComponent.getDamageable() == true) {
			lifeComponent.setDamageable(false);
			System.out.println("Player.hide: hiding");
		}
	}
	
	/**
	 * Unhides the player if the player is hidden.
	 */
	public void unHide() {
		LifeComponent lifeComponent = (LifeComponent)getComponent(Component.LIFE);
		if (lifeComponent.getDamageable() == false) {
			lifeComponent.setDamageable(true);
			System.out.println("Player.hide: unhiding");
		}
	}
	
	/**
	 * Returns if the player is hidden or not.
	 * 
	 * @return
	 */
	public boolean isHidden() {
		LifeComponent lifeComponent = (LifeComponent)getComponent(Component.LIFE);
		if (lifeComponent.getDamageable() == true) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Kills the player and removes its instance.
	 */
	public void kill() {
		try {
			this.level.env.addEntity(new PlayerCorpse(this.level, (int)this.position.x, (int)this.position.y));
			this.level.env.removeEntity(this);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
