package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.level.Level;
import nl.timcommandeur.zombies.light.LightSource;
import nl.timcommandeur.zombies.screen.Camera;

public class MagneticCrane extends Entity implements Usable {
	public Vector2f sliderPos;
	public Vector2f armPos;
	
	public LightSource craneLight;
	
	private Entity user;
	private Rectangle useBox;
	
	public boolean magnetActive = false;
	
	private Image rail;
	private Image slider;
	public Image arm;
	
	// Arm positional variables.
	private int maxLeftPos = 32;
	private int maxRightPos = 256;
	private float minArmHeight;
	private float maxArmHeight;
	public Vector2f armVelocity;
	private Vector2f armAcceleration;
	private Vector2f maxArmVelocity;
	
	public MagneticCrane(Level level, Vector2f pos) throws SlickException {
		super.init(level);
		this.gravityAffected = false;
		this.position = pos;
		this.armVelocity = new Vector2f(0f, 0f);
		this.armAcceleration = new Vector2f(0.01f, 0.01f);
		this.maxArmVelocity = new Vector2f(0.1f, 0.1f);
		this.useBox = new Rectangle(1840, 720, 80, 160);
		
		// Initialize the images.
		rail = new Image("data/sprites/entity/building/craneRail.png", new Color(255, 255, 255));
		slider = new Image("data/sprites/entity/building/craneSlider.png", new Color(255, 255, 255));
		arm = new Image("data/sprites/entity/building/craneArm.png", new Color(255, 255, 255));
		
		this.sliderPos = new Vector2f(position.x + 100.0f, position.y);
		this.armPos = new Vector2f(sliderPos.x + slider.getWidth() / 2 - arm.getWidth() / 2, sliderPos.y + 200.0f);
		this.minArmHeight = sliderPos.y + 32;
		this.maxArmHeight = sliderPos.y + slider.getHeight();
		this.maxLeftPos = (int) (position.x - 100);
		this.maxRightPos = (int) (position.x + rail.getWidth() - slider.getWidth());
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
		Camera.getInstance().setTarget(this, new Vector2f(0, 400));
		
		// Create the crane light when the crane is used.
		craneLight = new LightSource(new Vector2f(armPos.getX() + 30, 730), 400, 1, new Color(200, 200, 200), camera);
		Level.lights.add(craneLight);
		level.fl.switchOnOff();
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
		if (playerControlled) {
			handleInput(input);
			craneLight.setPosition(new Vector2f(armPos.x + arm.getWidth() / 2, armPos.y + arm.getHeight()));
		} else {
			if (Level.lights.contains(craneLight)) {
				Level.lights.remove(craneLight);
				craneLight = null;
				level.fl.switchOnOff();
			}
		}
		
		// Because the crane itself does not have a velocity, it is not
		// affected by TileEnvironment. Therefore we increment the arm's
		// position here manually.
		armPos.y += armVelocity.getY() * delta;
		if (armPos.y > maxArmHeight) armPos.y = maxArmHeight;
		if (armPos.y < minArmHeight) armPos.y = minArmHeight;
		sliderPos.x += armVelocity.getX() * delta;
		if (sliderPos.x < maxLeftPos) sliderPos.x = maxLeftPos;
		if (sliderPos.x > maxRightPos) sliderPos.x = maxRightPos;
		armPos.x = sliderPos.getX() + slider.getWidth() / 2 - arm.getWidth() / 2;
		super.update(input, delta);
	}
	
	private void handleInput(Input input) {
		// Check vertical movement.
		if (input.isKeyDown(Input.KEY_W)) {
			armVelocity.y -= armAcceleration.y;
			if (armVelocity.y < -maxArmVelocity.y) armVelocity.y = -maxArmVelocity.y;
		}
		if (input.isKeyDown(Input.KEY_S)) {
			armVelocity.y += armAcceleration.y;
			if (armVelocity.y > maxArmVelocity.y) armVelocity.y = maxArmVelocity.y;
		}
		if (!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)) {
			// No vertical movement input, slow this thing down!
			if (armVelocity.y < 0) {
				armVelocity.y += armAcceleration.y;
				if (armVelocity.y > 0) armVelocity.y = 0;
			} else {
				armVelocity.y -= armAcceleration.y;
				if (armVelocity.y < 0) armVelocity.y = 0;
			}
		}
		
		// Check horizontal movement.
		if (input.isKeyDown(Input.KEY_A)) {
			armVelocity.x -= armAcceleration.x;
			if (armVelocity.x < -maxArmVelocity.x) armVelocity.x = -maxArmVelocity.x;
		}
		if (input.isKeyDown(Input.KEY_D)) {
			armVelocity.x += armAcceleration.x;
			if (armVelocity.x > maxArmVelocity.x) armVelocity.x = maxArmVelocity.x;
		}
		if (!input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_D)) {
			// No horizontal movement input, slow this thing down!
			if (armVelocity.x < 0) {
				armVelocity.x += armAcceleration.x;
				if (armVelocity.x > 0) armVelocity.x = 0;
			} else {
				armVelocity.x -= armAcceleration.x;
				if (armVelocity.x < 0) armVelocity.x = 0;
			}
		}
		
		// Check if the player wants to stop using.
		if (input.isKeyPressed(Input.KEY_E)) {
			playerControlled = false;
			user.playerControlled = true;
			Camera.getInstance().setTarget(user);
			user = null;
		}
		
		// Check if the magnet should be turned on or off.
		if (input.isKeyPressed(Input.KEY_SPACE)) {
			magnetActive = !magnetActive;
		}
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		rail.draw(renderPosition.x, renderPosition.y);
		arm.draw(renderPosition.x + armPos.x - position.x, renderPosition.y + armPos.y - position.y);
		slider.draw(renderPosition.x + sliderPos.x - position.x, renderPosition.y + sliderPos.y - position.getY());
	}
}
