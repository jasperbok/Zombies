package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

public class HidingSpace extends Entity {

	public float ladderSpeed = 0.3f;

	public HidingSpace(Level level, float x, float y, float sizeX, float sizeY) {
		super.init(level);
		
		this.size = new Vector2f(sizeX, sizeY);
		
		this.position = new Vector2f(x, y);
		this.maxVel = new Vector2f(0, 0);
		this.gravityFactor = 0;
		this.invincible = true;
		
		this.type = Entity.Type.B;
		this.checkAgainst = Entity.Type.A;
		this.collides = Entity.Collides.LITE;
	}
	
	public void update(Input input, int delta) {
		Player player = (Player)this.level.env.getEntityByName("player");
		if (player != null) {
			player.canHide = false;
		}
	}
	
	public void check(Entity other) {
		if (other == this.level.env.getEntityByName("player")) {
			Player player = (Player)other;
			player.canHide = true;
		}
	}
}
