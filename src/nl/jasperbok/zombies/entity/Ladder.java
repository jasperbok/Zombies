package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

public class Ladder extends Entity {

	public float ladderSpeed = 0.3f;

	public Ladder(Level level, float x, float y, float sizeX, float sizeY) {
		super.init(level);
		
		this.size = new Vector2f(sizeX, sizeY);
		
		this.position = new Vector2f(x, y);
		this.maxVel = new Vector2f(0, 0);
		this.gravityFactor = 0;
		this.health = 999999;
		
		this.type = Entity.Type.B;
		this.checkAgainst = Entity.Type.BOTH;
		this.collides = Entity.Collides.LITE;
	}
	
	public void update(Input input, int delta) {
		Player player = (Player)this.level.env.getEntityByName("player");
		if (player != null) {
			player.canClimb = false;
		}
	}
	
	public void check(Entity other) {
		if (other == this.level.env.getEntityByName("player")) {
			Player player = (Player)other;
			player.canClimb = true;
			
			if (player.ladderReleaseTimer.delta() > -0.1) {
				if (player.vel.y < 0 && player.momentumDirectionY != -1) {
					player.isClimbing = true;
				} else {
					if (player.isClimbing && player.momentumDirectionY != 0) {
						player.vel.y = this.ladderSpeed * player.momentumDirectionY;
					} else {
						player.momentumDirectionY = 0;
						player.vel.y = 0;
						player.position.y = player.last.y;
					}
					
					// Player is at the bottom of a ladder, so get him off.
					if (player.momentumDirectionY == 1 && player.position.y == player.last.y) {
						player.momentumDirectionY = 0;
						player.isClimbing = false;
					}
				}
			}
		}
	}
}
