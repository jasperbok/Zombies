package nl.jasperbok.zombies.entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public abstract class Entity {
	public Level level;
	public Vector2 position = new Vector2(0.0f, 0.0f);
	public Vector2 velocity = new Vector2(0.0f, 0.0f);
	public Rectangle boundingBox;
	public boolean isBlocking = true;
	public boolean isMovable = true;
	public boolean playerControlled = false;
	
	public Vector2 drawPosition = new Vector2(0.0f, 0.0f);
	
	protected World world;
	protected Body body = null;
	
	public void init(Level level) {
		this.level = level;
	}
	
	/**
	 * Set the velocity of the entity.
	 * 
	 * @param x The x component of the velocity to apply.
	 * @param y The y component of the velocity to apply.
	 */
	public void setVelocity(float x, float y) {
		Vector2f vec = new Vector2f(body.getVelocity());
		vec.scale(-1);
		body.adjustVelocity(vec);
		body.adjustVelocity(new Vector2f(x, y));
	}
	
	public void setX(float x) {
		body.setPosition(x, getY());
	}
	
	public void setY(float y) {
		body.setPosition(getX(), y);
	}
	
	public void setPosition(float x, float y) {
		body.setPosition(x, y);
	}
	
	public float getX() {
		return body.getPosition().getX();
	}
	
	public float getY() {
		return body.getPosition().getY();
	}
	
	public float getVelX() {
		return body.getVelocity().getX();
	}
	
	public float getVelY() {
		return body.getVelocity().getY();
	}
	
	/**
	 * Set the world this entity is part of.
	 * 
	 * @param world The world this entity is part of.
	 */
	public void setWorld(World world) {
		this.world = world;
	}
	
	/**
	 * Get the body representing this entity in the world.
	 */
	public Body getBody() {
		return body;
	}
	
	/**
	 * Update this entity. This method is called once before physical world is updated
	 * each cycle. It is only ever called once per update.
	 * 
	 * @param delta The amount of time passed since last update.
	 */
	public void preUpdate(int delta) {
		
	}
	
	public void update(int delta) {
		
	}
	
	public void render(Graphics g) throws SlickException {
		
	}
}
