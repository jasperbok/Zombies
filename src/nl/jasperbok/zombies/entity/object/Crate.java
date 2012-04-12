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
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class Crate extends Entity {
	private Image image;
	private boolean isFalling = false;
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
	}

	public void update(Input input, int delta) {
		boundingBox.setBounds(position.x, position.y, image.getWidth(), image.getHeight());
		this.isOnGround = level.env.isOnGround(this, false);
		
		if (crane.magnetActive && !draggedByMagnet) {
			Rectangle hitBox = new Rectangle(crane.armPos.getX(), crane.armPos.getY() + crane.arm.getHeight(), crane.arm.getWidth(), 20);
			if (hitBox.intersects(this.boundingBox)) {
				setPosition(position.getX(), crane.armPos.getY() + crane.arm.getHeight());
				draggedByMagnet = true;
			}
		}
		if (draggedByMagnet) {
			if (crane.magnetActive) {
				this.velocity = crane.velocity;
			} else {
				draggedByMagnet = false;
			}
		}
		
		// Update components.
		super.update(input, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(drawPosition.getX(), drawPosition.getY());
	}
}
