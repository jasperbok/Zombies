package nl.jasperbok.zombies.entity;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.item.Inventory;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public abstract class Entity extends RenderObject {
	/**
	 * constants
	 */
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	
	public static enum Collides {
		NEVER (0),
		LITE (1),
		PASSIVE (2),
		ACTIVE (4),
		FIXED (8);
		
		public int value;
		Collides(int value) {this.value = value;};
	}
	
	public static enum Type {
		NONE (0),
		A (1),
		B (2),
		BOTH (4);
		
		public int value;
		Type(int value) {this.value = value;};
	}
	
	public int id = 0;
	public String name = "";
	public HashMap<String, String> settings = new HashMap<String, String>();
	
	public Vector2f size = new Vector2f(0, 0);
	public Vector2f offset = new Vector2f(0, 0);
	
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	
	public Vector2f last = new Vector2f(0, 0);
	public Vector2f vel = new Vector2f(0, 0);
	public Vector2f accel = new Vector2f(0, 0);
	public Vector2f maxVel = new Vector2f(0, 0);
	public Vector2f friction = new Vector2f(0, 0);
	public int zIndex = 0;
	public int gravityFactor = 1;
	public boolean standing = false;
	public int bounciness = 0;
	public float minBounceVelocity = 5f;
	
	public HashMap<String, Animation> anims = new HashMap<String, Animation>();
	public SpriteSheet animSheet = null;
	public Animation currentAnim = null;
	public int health = 5;
	
	public Entity.Type type = Entity.Type.NONE;
	public Entity.Type checkAgainst = Entity.Type.NONE;
	public Entity.Collides collides = Entity.Collides.NEVER;
	
	public boolean killed = false;
	
	public boolean isBlocking = true;
	public boolean isSolid = false;
	public boolean isTopSolid = false;
	public boolean isMovable = true;
	public boolean playerControlled = false;
	public boolean gravityAffected = true;
	public Entity user = null;
	public Inventory inventory;
	
	// Status variables.
	public boolean wasFalling = false;
	public boolean isFalling = false;
	public boolean wasOnGround = false;
	public boolean isFacingLeft = false;
	public boolean wasClimbing = false;
	public boolean isClimbing = false;
	public int facing = 1;
	
	public Vector2 drawPosition = new Vector2(0.0f, 0.0f);
	
	protected ArrayList<Component> components;
	public Level level;
	
	public void init(Level level) {
		this.level = level;
		this.boundingBox = new Rectangle(0, 0, 0, 0);
		components = new ArrayList<Component>();
	}
	
	/**
	 * Adds an animation to the Entity's list of animations.
	 * 
	 * @param name The name of the animation.
	 * @param duration The duration of each frame.
	 * @param sequence A list of
	 */
	public void addAnim(String name, int duration, int[] sequence) {
		addAnim(name, duration, sequence, true);
	}
	
	public void addAnim(String name, int duration, int[] sequence, boolean loop) {
		if (this.animSheet != null) {
			Animation anim = new Animation();
			for (int i = 0; i < sequence.length; i++) {
				anim.addFrame(this.animSheet.getSprite(sequence[i], 0), duration);
			}
			anim.setLooping(loop);
			this.anims.put(name, anim);
		} else {
			System.out.println("No animation sheet set.");
		}
	}
	
	public void addComponent(Component component) {
		component.setOwner(this);
		components.add(component);
	}
	
	public Component getComponent(int id) {
		for (Component comp: components) {
			if (comp.getId() == id) {
				return comp;
			}
		}
		return null;
	}
	
	public boolean hasComponent(int id) {
		for (Component comp: components) {
			if (comp.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
		updateBoundingBox();
	}
	
	public void updateBoundingBox() {
		if (this.currentAnim != null) {
			this.boundingBox.setBounds(
					this.position.x,
					this.position.y,
					this.currentAnim.getWidth(),
					this.currentAnim.getHeight()
					);
		}
	}
	
	/**
	 * Renders the Entity to the screen.
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
		}
	}
	
	public void update(Input input, int delta) {
		for (Component comp: components) {
			comp.update(input, delta);
		}
		
		this.updateBoundingBox();
		this.updateRenderPosition();
	}
	
	/**
	 * Notifies the Entity.
	 * 
	 * This would actually be called notify, but that's already a function
	 * inside Object which cannot be overridden.
	 * 
	 * @param message The message to send to the Entity.
	 */
	public void call(String message) {
		System.out.println("Entity activated, but no subclass implements the call() method!");
		System.out.println("Message from trigger: " + message);
	}
	
	public boolean canBeUsed (Rectangle rect) {
		return false;
	}
	
	public void use (Entity user) {
		
	}
	
	/**
	 * Kills the Entity.
	 */
	public void kill() {
		this.level.env.removeEntity(this);
	}
	
	/**
	 * Deals damage to the Entity.
	 * 
	 * @param amount The amount of damage to deal.
	 */
	public void receiveDamage(int amount) {
		this.health -= amount;
		if (this.health <= 0) {
			this.kill();
		}
	}
	
	/**
	 * Heals the Entity.
	 * 
	 * @param amount The amount of damage to heal.
	 */
	public void heal(int amount) {
		this.receiveDamage(-amount);
	}
	
	/**
	 * Checks whether this entity touches another.
	 * 
	 * @param other The Entity to check.
	 * @return True if the Entities overlap, otherwise false.
	 */
	public boolean touches(Entity other) {
		return !(
				this.position.x >= other.position.x + other.boundingBox.getWidth() ||
				this.position.x + this.boundingBox.getWidth() <= other.position.x ||
				this.position.y >= other.position.y + other.boundingBox.getHeight() ||
				this.position.y + this.boundingBox.getHeight() <= other.position.y
				);
	}
	
	/**
	 * Calculates the distance between the center points of this and
	 * another Entity.
	 * 
	 * @param other The other Entity to check the distance to.
	 * @return The distance between the Entities.
	 */
	public float distanceTo(Entity other) {
		float xDistance = this.boundingBox.getCenterX() - other.boundingBox.getCenterX();
		float yDistance = this.boundingBox.getCenterY() - other.boundingBox.getCenterY();
		return (float)Math.sqrt(xDistance * xDistance + yDistance * yDistance);
	}
	
	/**
	 * Returns the angle from this Entity to another.
	 * 
	 * @param other The other Entity to check the angle to.
	 * @return
	 */
	public float angleTo(Entity other) {
		return (float)Math.atan2(
				(other.position.y + other.boundingBox.getHeight() / 2) - (this.position.y + this.boundingBox.getHeight() / 2),
				(other.position.x + other.boundingBox.getWidth() / 2) - (this.position.x + this.boundingBox.getWidth() / 2)
				);
	}
	
	//
	// STATIC COLLISION FUNCTIONS
	//
	
	public static void checkPair(Entity a, Entity b) {
		
	}
	
	public static void solveCollision(Entity a, Entity b) {
		Entity weak = null;
		
		if (a.collides == Entity.Collides.LITE || b.collides == Entity.Collides.FIXED) {
			weak = a;
		} else if (b.collides == Entity.Collides.LITE || a.collides == Entity.Collides.FIXED) {
			weak = b;
		}
		
		// The rest of this function requires that every Entity has it's previous position stored.
	}
	
	public static void seperateOnXAxis(Entity left, Entity right, Entity weak) {
		float nudge = (left.position.x + left.boundingBox.getWidth() - right.position.x);
		
		// There is a weak Entity, so just move that one.
		if (weak != null) {
			if (weak == left) {
				weak.setPosition(weak.position.x - nudge, weak.position.y);
			} else {
				weak.setPosition(weak.position.x + nudge, weak.position.y);
			}
		}
		
		else {
			left.setPosition(left.position.x - nudge / 2, left.position.y);
			right.setPosition(right.position.x + nudge / 2, right.position.y);
		}
	}
	
	public static void seperateOnYAxis(Entity top, Entity bottom, Entity weak) {
		float nudge = (top.position.y + top.boundingBox.getHeight() - bottom.position.y);
		
		// There is a weak Entity, so just move that one.
		if (weak != null) {
			Entity strong = top == weak ? bottom : top;
			
			weak.vel.y = strong.vel.y;
			
			// On a platform?
			float nudgeX = 0;
			if (weak == top) {
				weak.standing = true;
				nudgeX = strong.vel.x;
			}
			
			weak.setPosition(weak.position.x + nudgeX, weak.position.y + nudge);
		}
		
		// Bottom Entity is standing, just move the top one.
		else if (bottom.standing) {
			top.setPosition(top.position.x, top.position.y - nudge);
			top.standing = true;
			top.vel.y = 0;
		}
		
		// Normal collision, both move.
		else {
			bottom.setPosition(bottom.position.x, bottom.position.y + nudge / 2);
			top.setPosition(top.position.x, top.position.y - nudge / 2);
		}
	}
}
