package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

public class BloodMark extends Entity {
	private int lifetime = 9000;
	private int currentLifetime = 0;
	
	/**
	 * Class constructor.
	 * 
	 * @param level The level this bloodmark is part of.
	 * @throws SlickException
	 */
	public BloodMark(Level level) throws SlickException {
		super.init(level);
		this.zIndex = -2;
		
		// Load the animation.
		Animation idle = new Animation();
		idle.addFrame(new Image("data/sprites/entity/object/bloodmark.png"), 5000);
		this.anims.put("idle", idle);
		this.currentAnim = this.anims.get("idle");
		
		boundingBox = new Rectangle(position.x, position.y, this.currentAnim.getWidth(), this.currentAnim.getHeight());
	}
	
	/**
	 * Updates the lifetime of the bloodmark.
	 */
	public void update(Input input, int delta) {
		super.update(input, delta);
		currentLifetime += delta;
		if (this.currentLifetime > this.lifetime) {
			this.level.env.removeAttractor(this);
			this.kill();
		}
	}
}
