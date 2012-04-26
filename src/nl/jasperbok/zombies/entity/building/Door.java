package nl.jasperbok.zombies.entity.building;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Door extends Entity {
	
	public Door(Level level, boolean facingLeft) throws SlickException {
		super.init(level);
		this.isBlocking = true;
		this.initAnimation(facingLeft);
		this.boundingBox = new Rectangle(position.x, position.y, this.currentAnim.getWidth(), this.currentAnim.getHeight());
	}
	
	public void initAnimation(boolean facingLeft) throws SlickException {
		Animation door = new Animation();
		if (facingLeft) {
			door.addFrame(new Image("data/sprites/entity/building/door.png"), 5000);
		} else {
			door.addFrame(new Image("data/sprites/entity/building/door.png").getFlippedCopy(true, false), 5000);
		}
		this.anims.put("door", door);
		this.currentAnim = this.anims.get("door");
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);
		
		// Ugly quickfix
		//---
		if (level.env.getPlayer().boundingBox.intersects(boundingBox)) {
			level.env.getPlayer().position.x -= 1;
		}
		//---
	}

	public void call(String message) {
		if (message == "on") {
			level.env.removeEntity(this);
		}
	}
}
