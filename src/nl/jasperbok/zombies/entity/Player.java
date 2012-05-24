package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.engine.Entity;
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
	protected boolean isHidden = false;
	/**
	 * Arm variables.
	 */
	protected Image armImageLeft;
	protected Image armImageRight;
	protected Vector2f armPos;
	
	private boolean firstUpdate = true;
	
	public Player(Level level, Vector2f pos) throws SlickException {
		super.init(level);
		
		this.position = pos;
		this.friction = new Vector2f(0.1f, 0.1f);
		this.maxVel = new Vector2f(0.3f, 10f);
		this.zIndex = -1;
		
		this.type = Entity.Type.A;
		this.checkAgainst = Entity.Type.B;
		this.collides = Entity.Collides.ACTIVE;
		
		this.addComponent(new PlayerInputComponent(this));
		this.addComponent(new LifeComponent(this, 5));
		
		this.playerControlled = true;
		
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
		
		// Init the arm.
		this.armImageLeft = new Image("data/sprites/entity/arm1.png", new Color(255, 255, 255)).getFlippedCopy(true, true);
		this.armImageRight = new Image("data/sprites/entity/arm1.png", new Color(255, 255, 255)).getFlippedCopy(true, false);
		this.armPos = this.position.copy();
		
		inventory = new Inventory();
		//this.init();
	}
	
	public void update(Input input, int delta) {
		if (this.firstUpdate) {
			this.ladderReleaseTimer = this.level.env.addTimer(0.5f);
			this.firstUpdate = false;
		}
		super.update(input, delta);
		
		wasClimbing = isClimbing;
		isClimbing = false;
		
		// Position and rotate the arm.
		int armAngle = (int) this.level.fl.getAngleInDegrees();
		this.armImageLeft.setCenterOfRotation(8, 24);
		this.armImageRight.setCenterOfRotation(10, 20);
		this.armImageLeft.setRotation(armAngle);
		this.armImageRight.setRotation(armAngle);
		/*
		if (wasClimbing && level.env.isOnClimableSurface(this)) {
			isClimbing = true;
			vel.set(new Vector2f(vel.getX(), 0));
		} else if (!level.env.isOnClimableSurface(this)) {
			isClimbing = false;
		}*/
		
		try {
			Hud.getInstance().setPlayerHealth(this.health);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Decide what animation should be played.
		if (this.isHidden) {
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
	
	/**
	 * Makes the player climb on top of an object.
	 * 
	 * @param target
	 */
	public void climbOnObject(Entity target) {
		((GravityComponent)this.getComponent(Component.GRAVITY)).toggleGravity();
		this.position.y -= target.boundingBox.getHeight();
		this.position.x = target.position.x;
		this.currentAnim = this.anims.get("climbOnObject");
		this.isClimbingObject = true;
	}
	
	/**
	 * Hides the player if the player was not hidden and unhides if the player was hidden.
	 * The tiles behind the player must be hideable.
	 */
	public void switchHide() {
		this.isHidden = !this.isHidden;
	}
	
	/**
	 * Hides the player if the current tiles are hideable and the player is not hidden.
	 */
	public void hide() {
		this.isHidden = true;
	}
	
	/**
	 * Unhides the player if the player is hidden.
	 */
	public void unHide() {
		this.isHidden = false;
	}
	
	/**
	 * Returns if the player is hidden or not.
	 * 
	 * @return
	 */
	public boolean isHidden() {
		return this.isHidden;
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
	
	/**
	 * Renders the Player to the screen.
	 * 
	 * Uses the Entity's renderPosition field.
	 * 
	 * @param container The GameContainer this Entity is part of.
	 * @param g The Graphics object the Entity should draw itself on.
	 * @throws SlickException
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (this.currentAnim != null) {
			g.drawAnimation(this.currentAnim, (int)this.renderPosition.x, (int)this.renderPosition.y);
			if (this.facing == Entity.LEFT) {
				g.drawImage(this.armImageLeft, this.renderPosition.x + this.currentAnim.getWidth() / 2 - 10, this.renderPosition.y + 15);
			} else {
				g.drawImage(this.armImageRight, this.renderPosition.x + this.currentAnim.getWidth() / 2 - 10, this.renderPosition.y + 15);
			}
		}
	}
}
