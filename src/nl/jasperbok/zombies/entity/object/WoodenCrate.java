package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.DraggableComponent;
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
		this.type = Entity.Type.NONE;
		this.checkAgainst = Entity.Type.BOTH;
		this.collides = Entity.Collides.ACTIVE;
		
		this.size.x = size;
		this.size.y = size;
		
		// Loading the animation.
		this.animSheet = new SpriteSheet("data/sprites/entity/object/wooden_crate.png", 80, 80);
		this.addAnim("idle", 50, new int[]{0});
		this.currentAnim = this.anims.get("idle");
		
		this.useBox = new Rectangle(this.position.x - 30, this.position.y, this.currentAnim.getWidth() + 60, this.currentAnim.getHeight());
		this.addComponent(new DraggableComponent(this));
		this.addComponent(new LifeComponent(this));
		((LifeComponent)this.getComponent(Component.LIFE)).setDamageable(false);
	}

	public void update(Input input, int delta){
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
