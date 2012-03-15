package nl.jasperbok.zombies.entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

/**
 * An Actor is a special type of Entity that handles more
 * platformer-like dynamics.
 * 
 * @author Jasper Bok
 */
public class Actor extends Entity {
	// The maxium velocity an Actor can jump at.
	private static final int MAX_JUMP_VELOCITY = 50;
	
	// The world in which an Actor exists.
	private World world;
	
	private boolean onGround = false;
	private int offGroundTimer = 0;
	private boolean jumped = false;
	private boolean facingRight = true;
	private boolean moving = false;
	private boolean falling = false;
	
	// The size of the actors collision bounds.
	private float size;
	protected float velocityX;
	
	/**
	 * Create a new Actor.
	 */
	public Actor(float x, float y, float mass, float size) {
		this.size = size;
		
		body = new Body(new Box(size, size), mass);
		body.setUserData(this);
		body.setRestitution(0);
		body.setFriction(0f);
		body.setMaxVelocity(20, 50);
		body.setRotatable(false);
		setPosition(x, y);
	}
	
	/**
	 * Apply force to the Actor. This should be used to move the
	 * Actor around the level.
	 * 
	 * @param x The x component of the force to apply.
	 * @param y The y component of the force to apply.
	 */
	public void applyForce(float x, float y) {
		body.addForce(new Vector2f(x, y));
		
		// If the force applied is up into the air, the actor is
		// considered to be jumping.
		if (y < 0) {
			jumped = true;
		}
		
		// If the actor has just changed direction, kill the x
		// velocity cause that's what happens in platformers.
		if (x > 0) {
			if (!facingRight) setVelocity(0, getVelY());
			facingRight = true;
		} else if (x < 0) {
			if (facingRight) setVelocity(0, getVelY());
			facingRight = false;
		}
	}
	
	/**
	 * @param delta
	 */
	public void preUpdate(int delta) {
		// At the start of each frame kill the x velocity
		// if the actors isn't being moved.
		if (!moving) setVelocity(0, getVelY());
		falling = (getVelY() > 0) && (!onGround());
		velocityX = getVelX();
	}
	
	public void update(int delta) {
		// Update the flag for the Actor being moved on the ground.
		// The physics engine will cause constant tiny bounces as the
		// body tries to settle, so don't consider the body to have
		// left the ground until it's done so for some time.
		boolean on = onGroundImpl(body);
		
		if (!on) {
			offGroundTimer += delta;
			if (offGroundTimer > 100) onGround = false;
		} else {
			offGroundTimer = 0;
			onGround = true;
		}
		
		// If the Actor has been pushed back from a collision
		// horizontally then kill the velocity - don't want to
		// keep pushing during this frame.
		if ((getVelX() > 0) && (!facingRight)) velocityX = 0;
		if ((getVelX() < 0) && (facingRight)) velocityX = 0;
		
		// Keep velocity constant throughout the updates.
		setVelocity(velocityX, getVelY());
		
		// If the Actor is standing on ground, negate gravity.
		// This stops some instability in physics.
		body.setGravityEffected(!on);
		
		// Clamp y.
		if (getVelY() < -MAX_JUMP_VELOCITY) setVelocity(getVelX(), -MAX_JUMP_VELOCITY);
		
		// Handle jumping as opposed to being moved up. This prevents
		// bounces on edges.
		if ((!jumped) && (getVelY() < 0)) setVelocity(getVelX(), getVelY() * 0.95f);
		
		if ((jumped) && (getVelY() >= 0)) jumped = false;
	}
	
	/**
	 * Implementation on ground check. This can be expensive so best to
	 * try and limit its use by caching.
	 * 
	 * @param body The body to check.
	 * @return True if the body is resetting on the ground.
	 */
	protected boolean onGroundImpl(Body body) {
		if (world == null) return false;
		
		// Loop through the collision events that have occured in the world.
		CollisionEvent[] events = world.getContacts(body);
		
		for (int i = 0; i < events.length; i++) {
			// If the point of collision was below the centre of the Actor,
			// i.e. near the feet.
			if (events[i].getPoint().getY() > getY() + (size / 4)) {
				// Check the normal to work out which body we care about.
				// If the right body is involved and a collision has happened
				// below it then the Actor is on ground.
				if (events[i].getNormal().getY() < -0.5) {
					if (events[i].getBodyB() == body) {
						return true;
					}
				}
				if (events[i].getNormal().getY() > 0.5) {
					if (events[i].getBodyA() == body) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/* GETTERS AND SETTERS */
	
	public boolean facingRight() {
		return facingRight;
	}
	
	public boolean falling() {
		return falling;
	}
	
	public Body getBody() {
		return body;
	}
	
	public boolean jumping() {
		return jumped;
	}
	
	public boolean moving() {
		return moving;
	}
	
	public boolean onGround() {
		return onGround;
	}
	
	/**
	 * Indicate whether the Actor is being moved.
	 * 
	 * @param moving True if this Actor is being moved.
	 */
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
}
