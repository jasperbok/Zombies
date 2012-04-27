package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Crate extends Entity {
	private Image image;
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
		this.addComponent(new GravityComponent(0.05f, this));
		this.gravityAffected = false;
		this.position = pos;
		this.velocity = new Vector2(0.0f, 0.0f);
		this.image = new Image("data/sprites/entity/object/crate.png", new Color(255, 255, 255));
		this.crane = crane;
		this.boundingBox = new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
		this.isSolid = true;
	}

	public void update(Input input, int delta) {
		this.isOnGround = level.env.isOnGround(this, false);
		
		if (crane.magnetActive && !draggedByMagnet) {
			Rectangle hitBox = new Rectangle(crane.armPos.getX(), crane.armPos.getY() + crane.arm.getHeight(), crane.arm.getWidth(), 20);
			if (hitBox.intersects(this.boundingBox)) {
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
				Vector2f armPosCopy = crane.armPos.copy();
				this.position = new Vector2f(armPosCopy.x + crane.arm.getWidth() / 2 - image.getWidth() / 2, crane.armPos.copy().y + crane.arm.getHeight());
			} else {
				draggedByMagnet = false;
				// Enable the GravityComponent.
				if (this.hasComponent(Component.GRAVITY)) {
					GravityComponent comp = (GravityComponent) this.getComponent(Component.GRAVITY);
					comp.toggleGravity();
				}
			}
		}
		
		// Update components.
		super.update(input, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(renderPosition.getX(), renderPosition.getY());
	}
}
