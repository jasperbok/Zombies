package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.object.Crate;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class MagneticCrane extends Entity implements Usable {
	public Vector2 sliderPos;
	public Vector2 armPos;
	
	private Entity user;
	private Rectangle useBox;
	private Vector2 acceleration = new Vector2(0.01f, 0.01f);
	
	public boolean magnetActive = false;
	
	private Image rail;
	private Image slider;
	public Image arm;
	
	private int maxLeftPos = 32;
	private int maxRightPos = 256;
	private float minArmHeight;
	private float maxArmHeight;
	
	private Entity magnetTarget = null;
	private Crate crate;
	
	public MagneticCrane(Level level, Vector2f pos) throws SlickException {
		super.init(level);
		this.gravityAffected = false;
		this.position = pos;
		this.maxVelocity = new Vector2(1f, 1f);
		this.useBox = new Rectangle(1840, 720, 80, 160);
		
		rail = new Image("data/sprites/entity/building/craneRail.png", new Color(255, 255, 255));
		slider = new Image("data/sprites/entity/building/craneSlider.png", new Color(255, 255, 255));
		arm = new Image("data/sprites/entity/building/craneArm.png", new Color(255, 255, 255));
		
		sliderPos = new Vector2(position.x + 100.0f, position.y);
		armPos = new Vector2(sliderPos.x + slider.getWidth() / 2 - arm.getWidth() / 2, sliderPos.y + 200.0f);
		maxArmHeight = sliderPos.y;
		minArmHeight = sliderPos.y + slider.getHeight() + arm.getHeight() - 100;
		maxLeftPos = (int) (position.x - 100);
		maxRightPos = (int) (position.x + rail.getWidth() - slider.getWidth());
	}

	/**
	 * Sets the crane into use mode and removes the caller from
	 * the player's control.
	 * 
	 * @param user An entity that wishes to use the crane.
	 */
	public void use(Entity user) {
		user.playerControlled = false;
		playerControlled = true;
		this.user = user;
	}
	
	/**
	 * Checks whether the provided rectangle intersects with the
	 * crane's use box.
	 * 
	 * @param rect The rectangle to check against the crane's use box.
	 * @return True if there is an intersection.
	 */
	public boolean canBeUsed(Rectangle rect) {
		return rect.intersects(useBox);
	}
	
	public void update(Input input, int delta) {
		if (playerControlled) handleInput(input);
			
		armPos.y += velocity.y;
		sliderPos.x += velocity.x;
		armPos.x = sliderPos.x + slider.getWidth() / 2 - arm.getWidth() / 2;
		
		/*if (magnetActive) {
			Rectangle attractArea = new Rectangle(armPos.x, armPos.y + arm.getHeight(), arm.getWidth(), 32);
			if (attractArea.intersects(crate.boundingBox)) {
				crate.position.y = armPos.y + arm.getHeight();
				crate.position.x = armPos.x;
			}
		}*/
		/*
		if (magnetActive) {
			if (crate.position.x > armPos.x - 32) {
				if (crate.position.x < armPos.x - 32 + arm.getWidth()) {
					if (crate.position.y > armPos.y + arm.getHeight()) {
						if (crate.position.y < armPos.y + arm.getHeight() + 32) {
							magnetTarget = crate;
							crate.draggedByMagnet = true;
						}
					}
				}
			}
		} else {
			crate.draggedByMagnet = false;
			magnetTarget = null;
		}
		*/
		if (magnetActive && magnetTarget != null) {
			magnetTarget.setPosition(armPos.x, armPos.y + arm.getHeight());
		}
	}
	
	private void handleInput(Input input) {
		// Check vertical movement.
		if (input.isKeyDown(Input.KEY_W)) {
			velocity.y -= acceleration.y;
			if (velocity.y < -maxVelocity.y) velocity.y = -maxVelocity.y;
		}
		if (input.isKeyDown(Input.KEY_S)) {
			velocity.y += acceleration.y;
			if (velocity.y > maxVelocity.y) velocity.y = maxVelocity.y;
		}
		if (!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)) {
			// No vertical movement input, slow this thing down!
			if (velocity.y < 0) {
				velocity.y += acceleration.y;
				if (velocity.y > 0) velocity.y = 0;
			} else {
				velocity.y -= acceleration.y;
				if (velocity.y < 0) velocity.y = 0;
			}
		}
		
		// Check horizontal movement.
		if (input.isKeyDown(Input.KEY_A)) {
			velocity.x -= acceleration.x;
			if (velocity.x < -maxVelocity.x) velocity.x = -maxVelocity.x;
		}
		if (input.isKeyDown(Input.KEY_D)) {
			velocity.x += acceleration.x;
			if (velocity.x > maxVelocity.x) velocity.x = maxVelocity.x;
		}
		if (!input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_D)) {
			// No horizontal movement input, slow this thing down!
			if (velocity.x < 0) {
				velocity.x += acceleration.x;
				if (velocity.x > 0) velocity.x = 0;
			} else {
				velocity.x -= acceleration.x;
				if (velocity.x < 0) velocity.x = 0;
			}
		}
		
		// Check if the player wants to stop using.
		if (input.isKeyPressed(Input.KEY_E)) {
			playerControlled = false;
			user.playerControlled = true;
			user = null;
		}
		
		// Check if the magnet should be turned on or off.
		if (input.isKeyPressed(Input.KEY_SPACE)) {
			magnetActive = !magnetActive;
		}
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		rail.draw(renderPosition.getX(), renderPosition.getY());
		arm.draw(renderPosition.getX() + armPos.x - position.getX(), renderPosition.getY() + armPos.y - position.getY());
		slider.draw(renderPosition.getX() + sliderPos.x - position.getX(), renderPosition.getY() + sliderPos.y - position.getY());
	}
}
