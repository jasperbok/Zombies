package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

public class Checkpoint extends Entity {

	public Checkpoint(Level level) throws SlickException {
		super.init(level);
		this.animSheet = new SpriteSheet("data/sprites/entity/building/checkpoint.png", 400, 320);
		this.addAnim("idle", 5000, new int[]{0});
		this.currentAnim = this.anims.get("idle");
	}
	
	public boolean canBeUsed (Rectangle rect) {
		return this.touches(this.level.env.getEntityByName("player"));
	}
	
	public void use (Entity user) {
		this.level.env.setCheckpoint((int)user.position.x, (int)user.position.y, user.health);
	}
}
