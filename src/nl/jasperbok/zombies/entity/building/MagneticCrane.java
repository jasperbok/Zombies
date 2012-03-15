package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.Color;
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

public class MagneticCrane extends Entity implements Usable {
	public Vector2 sliderPos;
	public Vector2 armPos;
	
	private Entity user;
	private Rectangle useBox;
	private Vector2 maxVelocity = new Vector2(0.5f, 0.5f);
	private Vector2 acceleration = new Vector2(0.01f, 0.01f);
	private Vector2 velocity = new Vector2(0.0f, 0.0f);
	
	public boolean isMagnetActive = false;
	
	private Image rail;
	private Image slider;
	private Image arm;
	
	private int maxLeftPos = 32;
	private int maxRightPos = 256;
	private float minArmHeight;
	private float maxArmHeight;
	
	public MagneticCrane(Level level, Vector2 pos) throws SlickException {
		super.init(level);
		position = pos;
		//useBox = new Rectangle(448.0f, 96.0f, 96.0f, 32.0f);
		useBox = new Rectangle(400, 200, 700, 700);
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

	public void use(Entity user) {
		user.playerControlled = false;
		playerControlled = true;
		this.user = user;
	}
	
	public boolean canBeUsed(Rectangle rect) {
		return rect.intersects(useBox);
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		if (playerControlled) {
			Input input = container.getInput();
			
			// Check vertical movement.
			if (input.isKeyDown(Input.KEY_W)) {
				velocity.y -= acceleration.y * delta;
				if (velocity.y < -maxVelocity.y) velocity.y = -maxVelocity.y;
			}
			if (input.isKeyDown(Input.KEY_S)) {
				velocity.y += acceleration.y * delta;
				if (velocity.y > maxVelocity.y) velocity.y = maxVelocity.y;
			}
			if (!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_S)) {
				// No vertical movement input, slow this thing down!
				if (velocity.y < 0) {
					velocity.y += acceleration.y * delta;
					if (velocity.y > 0) velocity.y = 0;
				} else {
					velocity.y -= acceleration.y * delta;
					if (velocity.y < 0) velocity.y = 0;
				}
			}
			
			// Check horizontal movement.
			if (input.isKeyDown(Input.KEY_A)) {
				velocity.x -= acceleration.x * delta;
				if (velocity.x < -maxVelocity.x) velocity.x = -maxVelocity.x;
			}
			if (input.isKeyDown(Input.KEY_D)) {
				velocity.x += acceleration.x * delta;
				if (velocity.x > maxVelocity.x) velocity.x = maxVelocity.x;
			}
			if (!input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_D)) {
				// No horizontal movement input, slow this thing down!
				if (velocity.x < 0) {
					velocity.x += acceleration.x * delta;
					if (velocity.x > 0) velocity.x = 0;
				} else {
					velocity.x -= acceleration.x * delta;
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
				isMagnetActive = !isMagnetActive;
			}
			
			armPos.y += velocity.y;
			sliderPos.x += velocity.x;
			armPos.x = sliderPos.x + slider.getWidth() / 2 - arm.getWidth() / 2;
		}
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		rail.draw(position.x, position.y);
		arm.draw(armPos.x, armPos.y);
		slider.draw(sliderPos.x, sliderPos.y);
	}
}
