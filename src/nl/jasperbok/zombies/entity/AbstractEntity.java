package nl.jasperbok.zombies.entity;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

/**
 * The common bits of all entities. A bunch of utility methods and a physical
 * body.
 * 
 * @author kevin
 *
 */
public class AbstractEntity {
	protected Body body;
	
	/**
	 * Set the velocity of the entity.
	 * 
	 * @param x The x component of the velocity.
	 * @param y The y component of the velocity.
	 */
	public void setVelocity(float x, float y) {
		Vector2f vec = new Vector2f(body.getVelocity());
		vec.scale(-1);
		body.adjustVelocity(vec);
		body.adjustVelocity(new Vector2f(x, y));
	}
	
	/**
	 * Set the x coordinate of this Entities position.
	 * 
	 * @param x The new x coordinate.
	 */
	public void setX(float x) {
		body.setPosition(x, getY());
	}
	
	/**
	 * Set the y coordinate of this Entities position.
	 * 
	 * @param y The new y coordinate.
	 */
	public void setY(float y) {
		body.setPosition(getX(), y);
	}
	
	/**
	 * Set the position of this Entity.
	 * 
	 * @param x The new x coordinate.
	 * @param y The new y coordinate.
	 */
	public void setPosition(float x, float y) {
		body.setPosition(x, y);
	}
	
	/**
	 * Get the x coordinate of this Entity's position.
	 * 
	 * @return The x coordinate of this Entity.
	 */
	public float getX() {
		return body.getPosition().getX();
	}
	
	/**
	 * Get the y coordinate of this Entity's position.
	 * 
	 * @return The y coordinate of this Entity.
	 */
	public float getY() {
		return body.getPosition().getY();
	}
	
	/**
	 * Get the x component of the velocity of this Entity.
	 * 
	 * @return The x component of the velocity of this Entity.
	 */
	public float getVelX() {
		return body.getVelocity().getX();
	}
	
	/**
	 * Get the y component of the velocity of this Entity.
	 * 
	 * @return The y component of the velocity of this Entity.
	 */
	public float getVelY() {
		return body.getVelocity().getY();
	}
}
