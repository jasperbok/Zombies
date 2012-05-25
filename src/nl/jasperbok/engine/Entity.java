package nl.jasperbok.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.RenderObject;
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
	
	/**
	 * Entity collision types.
	 */
	public static enum Collides {
		NEVER (0),		// The entity never collides.
		LITE (1),		// This entity will always be weak and thus move out of the way in collisions.
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
	
	public Vector2f last = new Vector2f(0, 0);
	public Vector2f vel = new Vector2f(0, 0);
	public Vector2f accel = new Vector2f(0, 0);
	public Vector2f maxVel = new Vector2f(3, 3);
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
	public boolean invincible = false;
	
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
		this.last = this.position.copy();
		this.vel.y += this.level.gravity * delta * this.gravityFactor;
		
		this.vel.x = this.getNewVelocity(this.vel.x, this.accel.x, this.friction.x, this.maxVel.x);
		this.vel.y = this.getNewVelocity(this.vel.y, this.accel.y, this.friction.y, this.maxVel.y);
		
		//this.position.x = this.position.x + this.vel.x * delta;
		//this.position.y = this.position.y + this.vel.y * delta;
		
		// Set the size of the Entity.
		if (this.currentAnim != null) {
			this.size.x = this.currentAnim.getCurrentFrame().getWidth();
			this.size.y = this.currentAnim.getCurrentFrame().getHeight();
		} else {
			this.size.x = 0;
			this.size.y = 0;
		}
		
		//this.updateBoundingBox();
		
		float mx = this.vel.x * delta;
		float my = this.vel.y * delta;
		this.handleMovementTrace(this.level.env.collisionMap.trace((int)this.position.x, (int)this.position.y, mx, my, (int)this.size.x, (int)this.size.y));
		
		this.updateRenderPosition();
	}
	
	private float getNewVelocity(float vel, float accel, float friction, float max) {
		if (accel != 0) {
			float newVel = vel + accel;
			if (newVel > max) {return max;}
			else if (newVel < -max) {return -max;}
			else {return newVel;}
		}
		else if (friction != 0) {
			if (vel - friction > 0) {
				return (vel - friction);
			}
			else if (vel + friction < 0) {
				return (vel + friction);
			}
			else {
				return 0;
			}
		}
		if (vel > max) {return max;}
		else if (vel < -max) {return -max;}
		else {return vel;}
	}
	
	public void handleMovementTrace(Resolve res) {
		this.standing = false;
		
		if (res.collision.get("y")) {
			if (this.vel.y > 0) {
				this.standing = true;
			}
			this.vel.y = 0;
		}
		
		if (res.collision.get("x")) {
			this.vel.x = 0;
		}
		
		this.position = res.pos.copy();
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
		if (!this.invincible) {
			this.health -= amount;
			if (this.health <= 0) {
				this.kill();
			}
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
				this.position.x >= other.position.x + other.size.x ||
				this.position.x + this.size.x <= other.position.x ||
				this.position.y >= other.position.y + other.size.y ||
				this.position.y + this.size.y <= other.position.y
				);
	}
	
	/**
	 * Checks if the entity collides from the given x and y distance to the center.
	 * 
	 * @param other
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean touchesFromCenter(Entity other, float x, float y) {
		Vector2f thisCenter = this.getCenter();
		Vector2f otherCenter = other.getCenter();
		if (x != 0 && y != 0) {
			return !(
				thisCenter.x - x >= otherCenter.x + x ||
				thisCenter.x + x <= otherCenter.x - x ||
				thisCenter.y - y >= otherCenter.y + y ||
				thisCenter.y + y <= otherCenter.y - y
				);
		} else if (y != 0) {
			return !(
				thisCenter.y - y >= otherCenter.y + y ||
				thisCenter.y + y <= otherCenter.y - y
				);
		} else {
			return !(
				thisCenter.x - x >= otherCenter.x + x ||
				thisCenter.x + x <= otherCenter.x - x
				);
		}
	}
	
	public Vector2f getCenter() {
		return new Vector2f(this.position.x + this.size.x / 2, this.position.y + this.size.y / 2);
	}
	
	/**
	 * Calculates the distance between the center points of this and
	 * another Entity.
	 * 
	 * @param other The other Entity to check the distance to.
	 * @return The distance between the Entities.
	 */
	public float distanceTo(Entity other) {
		float xDistance = this.position.x + this.size.x / 2 - other.position.x + other.size.x / 2;
		float yDistance = this.position.y + this.size.y / 2 - other.position.y + other.size.y / 2;
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
				(other.position.y + other.size.y / 2) - (this.position.y + this.size.y / 2),
				(other.position.x + other.size.x / 2) - (this.position.x + this.size.x / 2)
				);
	}
	
	public void check (Entity other) {}
	public void collideWith (Entity other, String axis) {}
	public void ready () {}
	
	//
	// STATIC COLLISION FUNCTIONS
	//
	
	public static void checkPair(Entity a, Entity b) {
		// Do these entities want to check?
		if (
				a.checkAgainst == b.type ||
				(a.checkAgainst == Entity.Type.BOTH && (b.type == Entity.Type.A || b.type == Entity.Type.B))
		) {
			a.check(b);
		}
		
		if (
				b.checkAgainst == a.type ||
				(b.checkAgainst == Entity.Type.BOTH && (a.type == Entity.Type.A || a.type == Entity.Type.B))
		) {
			b.check(a);
		}
		
		// If this pair allows collisions, solve it! At least one entity must
		// collide ACTIVE or FIXED, while the other one must not collide NEVER.
		if (
				((a.collides == Entity.Collides.ACTIVE || a.collides == Entity.Collides.FIXED) && b.collides != Entity.Collides.NEVER) ||
				((b.collides == Entity.Collides.ACTIVE || b.collides == Entity.Collides.FIXED) && a.collides != Entity.Collides.NEVER)
		) {
			Entity.solveCollision(a, b);
		}
	}
	
	public static void solveCollision(Entity a, Entity b) {
		Entity weak = null;
		
		if (
				a.collides == Entity.Collides.LITE ||
				b.collides == Entity.Collides.FIXED
		) {
			weak = a;
		}
		else if (
				b.collides == Entity.Collides.LITE || 
				a.collides == Entity.Collides.FIXED
		) {
			weak = b;
		}
		
		// The rest of this function requires that every Entity has it's previous position stored.
		// The current implementation is based on the current crappy framework and should be
		// reworked if we implement the 'last' property of entities.
		
		// Vertical collision.
		if (
				(a.last.x + a.size.x) > (b.last.x) &&
				(a.last.x) < (b.last.x + b.size.x)
		) {
			// Which one is on top?
			if (a.last.y < b.last.y) {
				Entity.seperateOnYAxis(a, b, weak);
			}
			else {
				Entity.seperateOnYAxis(b, a, weak);
			}
			a.collideWith(b, "y");
			b.collideWith(a, "y");
		}
		
		// Horizontal collision.
		if (
				(a.last.y + a.size.y) > (b.last.y) &&
				(a.last.y) < (b.last.y + b.size.y)
		) {
			// Which one is on the left?
			if (a.last.x < b.last.x) {
				Entity.seperateOnXAxis(a, b, weak);
			}
			else {
				Entity.seperateOnXAxis(b, a, weak);
			}
			a.collideWith(b, "x");
			b.collideWith(a, "x");
		}
	}
	
	public static void seperateOnXAxis(Entity left, Entity right, Entity weak) {
		float nudge = (left.position.x + left.size.x - right.position.x);
		
		// There is a weak Entity, so just move that one.
		if (weak != null) {
			Entity strong = (weak == left ? right : left);
			weak.vel.x = -weak.vel.x * weak.bounciness + strong.vel.x;
			
			Resolve resWeak = weak.level.env.collisionMap.trace(
					(int)weak.position.x, (int)weak.position.y, weak == left ? (float)-nudge : (float)nudge, 0, (int)weak.size.x, (int)weak.size.y
				);
			weak.position.x = resWeak.pos.x;
		}
		
		// Normal collision, move both.
		else {
			float v2 = (left.vel.x - right.vel.x) / 2;
			left.vel.x = -v2;
			right.vel.x = v2;
			
			Resolve resLeft = left.level.env.collisionMap.trace(
					(int)left.position.x, (int)left.position.y, -nudge / 2, 0, (int)left.size.x, (int)left.size.y
				);
			left.position.x = (float)Math.floor(resLeft.pos.x);
			
			Resolve resRight = right.level.env.collisionMap.trace(
					(int)right.position.x, (int)right.position.y, nudge / 2, 0, (int)right.size.x, (int)right.size.y
				);
			right.position.x = (float)Math.ceil(resRight.pos.x);
		}
	}
	
	public static void seperateOnYAxis(Entity top, Entity bottom, Entity weak) {
		float nudge = (top.position.y + top.size.y - bottom.position.y);
		
		// There is a weak Entity, so just move that one.
		if (weak != null) {
			Entity strong = top == weak ? bottom : top;
			
			weak.vel.y = -weak.vel.y * weak.bounciness + strong.vel.y;
			
			// On a platform?
			float nudgeX = 0;
			if (weak == top && Math.abs(weak.vel.y - strong.vel.y) < weak.minBounceVelocity) {
				weak.standing = true;
				nudgeX = strong.vel.x; // *delta?
			}
			
			Resolve resWeak = weak.level.env.collisionMap.trace(
					(int)weak.position.x, (int)weak.position.y, nudgeX, weak == top ? (float)-nudge : (float)nudge, (int)weak.size.x, (int)weak.size.y
				);
			weak.position = resWeak.pos.copy();
		}
		
		// Bottom Entity is standing, just move the top one.
		else if (top.level.gravity != 0 && (bottom.standing || top.vel.y > 0)) {
			Resolve resTop = top.level.env.collisionMap.trace(
					(int)top.position.x, (int)top.position.y, 0, -(top.position.y + top.size.y - bottom.position.y), (int)top.size.x, (int)top.size.y
			);
			top.position.y = resTop.pos.y;
			
			if (top.bounciness > 0 && top.vel.y > top.minBounceVelocity) {
				top.vel.y *= -top.bounciness;
			} else {
				top.standing = true;
				top.vel.y = 0;
			}
		}
		
		// Normal collision, both move.
		else {
			float v2 = (top.vel.y - bottom.vel.y) / 2;
			top.vel.y = -v2;
			bottom.vel.y = v2;
			
			float nudgeX = bottom.vel.x; // *delta?
			
			Resolve resTop = top.level.env.collisionMap.trace(
					(int)top.position.x, (int)top.position.y, nudgeX, -nudge / 2, (int)top.size.x, (int)top.size.y
			);
			top.position.y = resTop.pos.y;
			
			Resolve resBottom = top.level.env.collisionMap.trace(
					(int)bottom.position.x, (int)bottom.position.y, 0, nudge / 2, (int)bottom.size.x, (int)bottom.size.y
			);
			bottom.position.y = resBottom.pos.y;
		}
	}
}
