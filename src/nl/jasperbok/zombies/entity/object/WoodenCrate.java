package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.DraggableComponent;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.entity.component.LifeComponent;
import nl.jasperbok.zombies.level.Level;

public class WoodenCrate extends Entity {
	private Rectangle useBox;
	public boolean playerControlled = false;
	
	private int delayTimer = 0;
	private boolean canBeUnUsed = false;

	/**
	 * WoodenCrate constructor creates a new crate.
	 * 
	 * @param level The level the crate is part of.
	 * @throws SlickException
	 */
	public WoodenCrate(Level level, float size) throws SlickException {
		super.init(level);
		this.isBlocking = true;
		this.type = Entity.Type.B;
		this.checkAgainst = Entity.Type.BOTH;
		this.collides = Entity.Collides.ACTIVE;
		
		// Loading the animation.
		Animation idle = new Animation();
		idle.addFrame(new Image("data/sprites/entity/object/wooden_crate.png").getScaledCopy(size), 5000);
		this.anims.put("idle", idle);
		this.currentAnim = this.anims.get("idle");
		
		this.level = level;
		this.boundingBox = new Rectangle(this.position.getX(), this.position.getY(), 80, 80);
		this.useBox = new Rectangle(this.position.x - 30, this.position.y, this.currentAnim.getWidth() + 60, this.currentAnim.getHeight());
		this.addComponent(new GravityComponent(this));
		this.addComponent(new DraggableComponent(this));
		this.addComponent(new LifeComponent(this));
		((LifeComponent)this.getComponent(Component.LIFE)).setDamageable(false);
	}

	public void update(Input input, int delta){
		this.standing = level.env.isOnGround(this, false);
		this.useBox.setBounds(this.position.x - 30, this.position.y, this.currentAnim.getWidth() + 60, this.currentAnim.getHeight());
		
		if (playerControlled) {
			this.vel = user.vel.copy();
			if (input.isKeyDown(Input.KEY_E) && this.canBeUnUsed) {
				this.playerControlled = false;
				this.user = null;
				this.canBeUnUsed = false;
				this.delayTimer = 0;
			}
		} else {
			this.vel.x = 0;
		}
		
		if (!this.canBeUnUsed && playerControlled) {
			this.delayTimer += delta;
			if (this.delayTimer > 200) {
				this.canBeUnUsed = true;
				this.delayTimer = 0;
			}
		}
		
		super.update(input, delta);
	}

	public void use(Entity user) {
		this.user = user;
		this.playerControlled = true;
	}

	public boolean canBeUsed(Rectangle rect) {
		return rect.intersects(useBox) && !playerControlled;
	}
}
