package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Elevator extends Entity implements Usable {
	private Vector2 velocity = new Vector2(0.0f, 0.0f);
	private float moveSpeed = 0.1f;
	private boolean moving = false;
	private boolean goingUp = true;
	private Entity user;
	private int useTimeOut = 0;
	
	public float maxHeight = 100.0f;
	public float minHeight = 500.0f;
	public Rectangle useBox;
	
	private Image image;
	
	public Elevator(Level level) throws SlickException {
		position = new Vector2(500.0f, 336.0f);
		image = new Image("data/sprites/entity/building/elevator.png");
		useBox = new Rectangle(500.0f, 300.0f, image.getWidth(), image.getHeight());
		init(level);
	}
	
	public void use(Entity user) {
		user.playerControlled = false;
		playerControlled = true;
		this.user = user;
	}
	
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
	
	public void update(GameContainer container, int delta) throws SlickException {
		useBox.setLocation(position.x, position.y - 50);
		
		if (playerControlled) {
			Input input = container.getInput();
			
			if (input.isKeyDown(Input.KEY_W)) {
				position.y -= moveSpeed * delta;
				user.position.y -= moveSpeed * delta;
				if (position.y <= maxHeight) position.y = maxHeight;
			}
			if (input.isKeyDown(Input.KEY_S)) {
				position.y += moveSpeed * delta;
				user.position.y += moveSpeed * delta;
				if (position.y >= minHeight) position.y = minHeight;
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				playerControlled = false;
				user.playerControlled = true;
				user = null;
			}
		} else {
			if (moving) {
				if (goingUp) {
					position.y -= moveSpeed * delta;
					if (position.y <= maxHeight) {
						position.y = maxHeight;
						moving = false;
						goingUp = false;
					}
				} else {
					position.y += moveSpeed * delta;
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
