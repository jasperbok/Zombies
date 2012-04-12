package nl.jasperbok.zombies.entity;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public abstract class Entity extends RenderObject {
	public Level level;
	public Vector2 velocity = new Vector2(0.0f, 0.0f);
	public Vector2 acceleration = new Vector2(0f, 0f);
	public Vector2 maxVelocity = new Vector2(0f, 0f);
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	public boolean isBlocking = true;
	public boolean isMovable = true;
	public boolean playerControlled = false;
	public boolean gravityAffected = true;
	
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
}
