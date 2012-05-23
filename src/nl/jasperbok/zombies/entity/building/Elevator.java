package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Elevator extends Entity implements Usable {
	private Vector2 velocity = new Vector2(0.0f, 0.0f);
	private float moveSpeed = 1f;
	private boolean moving = false;
	private boolean goingUp = true;
	private Entity user;
	private int useTimeOut = 0;
	
	public float maxHeight = 100.0f;
	public float minHeight = 500.0f;
	public Rectangle useBox;
	
	private Image image;
	
	/**
	 * Class constructor
	 * 
	 * @param level The level this elevator is part of.
	 * @throws SlickException
	 */
	public Elevator(Level level, int xPos, int yPos) throws SlickException {
		setPosition(xPos, yPos);
		image = new Image("data/sprites/entity/building/elevator.png");
		useBox = new Rectangle(position.getX(), position.getY() - 40, image.getWidth(), image.getHeight());
		gravityAffected = false;
		this.isBlocking = true;
		init(level);
	}
	
	/**
	 * 
	 */
	public void use(Entity user) {
		user.playerControlled = false;
		playerControlled = true;
		this.user = user;
	}
	
	/**
	 * Checks whether this elevator can be used from the position of the
	 * given rectangle.
	 * 
	 * @param rect The rectangle from where we want to use this elevator.
	 * @return boolean True if the elevator can be used, false otherwise.
	 */
	public boolean canBeUsed(Rectangle rect) {
		if (useTimeOut <= 0) {
			return useBox.intersects(rect);
		} else {
			return false;
		}
	}
	
	public void getToPosition(boolean top) {
		moving = true;
		goingUp = top;
	}
	
	public void update(Input input, int delta) {
		useBox.setLocation(position.x, position.y - 50);
		
		if (playerControlled) {			
			if (input.isKeyDown(Input.KEY_W)) {
				position.y -= moveSpeed;
				user.position.y -= moveSpeed;
				if (position.y <= maxHeight) position.y = maxHeight;
			}
			if (input.isKeyDown(Input.KEY_S)) {
				position.y += moveSpeed;
				user.position.y += moveSpeed;
				if (position.y >= minHeight) position.y = minHeight;
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				System.out.println("User wants to stop controlling this elevator!");
				playerControlled = false;
				user.playerControlled = true;
				user = null;
			}
		} else {
			if (moving) {
				if (goingUp) {
					position.y -= moveSpeed;
					if (position.y <= maxHeight) {
						position.y = maxHeight;
						moving = false;
						goingUp = false;
					}
				} else {
					position.y += moveSpeed;
					if (position.y >= minHeight) {
						position.y = minHeight;
						moving = false;
						goingUp = true;
					}
				}
			}
		}
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(position.x, position.y);
	}
}
