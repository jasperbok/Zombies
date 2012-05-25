package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;

public class Crate extends Entity {
	public boolean draggedByMagnet = false;
	private MagneticCrane crane;

	/**
	 * Crate constructor creates a new crate.
	 * 
	 * @param level The level the crate is part of.
	 * @param pos A Vector2f containing the initial position for the Crate.
	 * @param crane The MagneticCrane this crate will be affected by.
	 */
	public Crate(Level level, Vector2f pos, MagneticCrane crane) throws SlickException {
		super.init(level);
		this.position = pos;
		this.vel = new Vector2f(0.0f, 0.0f);
		this.maxVel = new Vector2f(0.1f, 1f);
		
		Animation idle = new Animation();
		idle.addFrame(new Image("data/sprites/entity/object/crate.png", new Color(255, 255, 255)), 5000);
		this.anims.put("idle", idle);
		this.currentAnim = this.anims.get("idle");
		
		this.crane = crane;
	}

	public void update(Input input, int delta) {		
		if (crane.magnetActive && !draggedByMagnet) {
			if (
					this.position.y > crane.armPos.y + crane.arm.getHeight() &&
					this.position.y < crane.armPos.y + crane.arm.getHeight() + 20 &&
					(this.position.x > crane.armPos.x || this.position.x < crane.armPos.x + crane.arm.getWidth())
			) {
				setPosition(position.getX(), crane.armPos.getY() + crane.arm.getHeight());
				draggedByMagnet = true;
				// Disable the GravityComponent.
				if (this.hasComponent(Component.GRAVITY)) {
					GravityComponent comp = (GravityComponent) this.getComponent(Component.GRAVITY);
					comp.toggleGravity();
				}
			}
		}
		if (draggedByMagnet) {
			if (crane.magnetActive) {
				this.gravityFactor = 0;
				Vector2f armPosCopy = crane.armPos.copy();
				this.position = new Vector2f(armPosCopy.x + crane.arm.getWidth() / 2 - this.currentAnim.getWidth() / 2, crane.armPos.copy().y + crane.arm.getHeight());
			} else {
				this.gravityFactor = 1;
				draggedByMagnet = false;
			}
		}
		
		super.update(input, delta);
	}
}
