package nl.jasperbok.zombies.entity.building;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Door extends Entity {
	
	public Door(Level level, boolean facingLeft) throws SlickException {
		super.init(level);
		this.initAnimation(facingLeft);
		
		this.type = Entity.Type.B;
		this.checkAgainst = Entity.Type.BOTH;
		this.collides = Entity.Collides.FIXED;
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

	public void call(String message) {
		if (message == "on") {
			level.env.removeEntity(this);
		}
	}
	
	public void receiveDamage() {
		
	}
}
