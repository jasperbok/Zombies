package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

public class Ladder extends Entity {

	public float ladderSpeed = 0.8f;

	public Ladder(Level level, float x, float y, float sizeX, float sizeY) {
		super.init(level);
		
		this.size = new Vector2f(sizeX, sizeY);
		
		this.position = new Vector2f(x, y);
		this.maxVel = new Vector2f(0, 0);
		this.gravityFactor = 0;
		this.invincible = true;
		
		this.type = Entity.Type.B;
		this.checkAgainst = Entity.Type.A;
		//this.collides = Entity.Collides.LITE;
		this.collides = Entity.Collides.FIXED;
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
			
			if (player.position.y + player.size.y > this.position.y - 15 &&
					player.position.y + player.size.y < this.position.y + 5) {
				// Player is on top of ladder, stop climbing and just walk.
				player.position.y = this.position.y + player.size.y;
				player.standing = true;
				player.momentumDirectionY = 0;
				player.isClimbing = false;
			}
			else if (player.momentumDirectionY == 1 && player.position.y == player.last.y) {
				// Player is touching the floor below, stop climbing.
				player.momentumDirectionY = 0;
				player.isClimbing = false;
			}
			else {
				// Player is climbing on the ladder, calculate his speed.
				if (player.momentumDirectionY == 1) {
					player.vel.y = this.ladderSpeed / 5;
				}
				else if (player.momentumDirectionY == -1) {
					player.vel.y = -this.ladderSpeed;
				}
				else {
					player.vel.y = 0;
				}
			}
		}
	}
}
