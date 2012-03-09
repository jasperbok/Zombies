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
	private Entity user;
	private Rectangle useBox;
	private float moveSpeed = 0.2f;
	
	private Image rail;
	private Image slider;
	private Image arm;
	
	private Vector2 sliderPos;
	private Vector2 armPos;
	
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
			
			if (input.isKeyDown(Input.KEY_W)) {
				armPos.y -= moveSpeed * delta;
				if (armPos.y <= maxArmHeight) armPos.y = maxArmHeight;
			}
			if (input.isKeyDown(Input.KEY_S)) {
				armPos.y += moveSpeed * delta;
				if (armPos.y >= minArmHeight) armPos.y = minArmHeight;
			}
			if (input.isKeyDown(Input.KEY_A)) {
				sliderPos.x -= moveSpeed * delta;
				if (sliderPos.x < maxLeftPos) sliderPos.x = maxLeftPos;
				armPos.x = sliderPos.x + slider.getWidth() / 2 - arm.getWidth() / 2;
			}
			if (input.isKeyDown(Input.KEY_D)) {
				sliderPos.x += moveSpeed * delta;
				if (sliderPos.x > maxRightPos) sliderPos.x = maxRightPos;
				armPos.x = sliderPos.x + slider.getWidth() / 2 - arm.getWidth() / 2;
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				playerControlled = false;
				user.playerControlled = true;
				user = null;
			}
		}
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		rail.draw(position.x, position.y);
		arm.draw(armPos.x, armPos.y);
		slider.draw(sliderPos.x, sliderPos.y);
	}
}
