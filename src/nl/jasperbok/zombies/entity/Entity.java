package nl.jasperbok.zombies.entity;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.item.Inventory;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public abstract class Entity extends RenderObject {
	public Level level;
	public Vector2f velocity = new Vector2f(0.0f, 0.0f);
	public Vector2f acceleration = new Vector2f(0f, 0f);
	public Vector2f maxVelocity = new Vector2f(0f, 0f);
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	public int health = 5;
	public boolean isBlocking = true;
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
	public boolean isOnGround = false;
	public boolean isFacingLeft = false;
	public boolean wasClimbing = false;
	public boolean isClimbing = false;
	
	public Vector2 drawPosition = new Vector2(0.0f, 0.0f);
	
	protected ArrayList<Component> components;
	
	public void init(Level level) {
		this.level = level;
		this.boundingBox = new Rectangle(0, 0, 0, 0);
		components = new ArrayList<Component>();
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
	
	protected void updateBoundingBox() {
		boundingBox.setX(position.getX());
		boundingBox.setY(position.getY());
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		
	}
	
	public void update(Input input, int delta) {
		for (Component comp: components) {
			comp.update(input, delta);
		}
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
}
