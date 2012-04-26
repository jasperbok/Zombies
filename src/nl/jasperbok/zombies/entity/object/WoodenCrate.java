package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.component.DraggableComponent;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;

public class WoodenCrate extends Entity {
	private Rectangle useBox;
	public boolean playerControlled = false;

	/**
	 * WoodenCrate constructor creates a new crate.
	 * 
	 * @param level The level the crate is part of.
	 * @throws SlickException
	 */
	public WoodenCrate(Level level) throws SlickException {
		super.init(level);
		this.isBlocking = true;
		
		// Loading the animation.
		Animation idle = new Animation();
		idle.addFrame(new Image("data/sprites/entity/object/wooden_crate.png"), 5000);
		this.anims.put("idle", idle);
		this.currentAnim = this.anims.get("idle");
		
		this.level = level;
		this.boundingBox = new Rectangle(this.position.getX(), this.position.getY(), 80, 80);
		this.useBox = new Rectangle(this.position.x - 30, this.position.y, this.currentAnim.getWidth() + 60, this.currentAnim.getHeight());
		this.addComponent(new GravityComponent(this));
		this.addComponent(new DraggableComponent(this));
	}

	public void update(Input input, int delta){
		this.isOnGround = level.env.isOnGround(this, false);
		this.useBox.setBounds(this.position.x - 30, this.position.y, this.currentAnim.getWidth() + 60, this.currentAnim.getHeight());
		updateBoundingBox();
		
		if (playerControlled) {
			this.velocity = user.velocity.copy();
			if (input.isKeyPressed(Input.KEY_E)) {
				playerControlled = false;
				user = null;
			}
		}
		
		super.update(input, delta);
	}

	public void use(Entity user) {
		this.user = user;
		this.playerControlled = true;
		System.out.println("use");
	}

	public boolean canBeUsed(Rectangle rect) {
		return rect.intersects(useBox);
	}
}
