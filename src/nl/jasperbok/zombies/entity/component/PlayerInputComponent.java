package nl.jasperbok.zombies.entity.component;

import java.util.ArrayList;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.engine.Resolve;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.object.BloodMark;
import nl.jasperbok.zombies.entity.object.WoodenCrate;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class PlayerInputComponent extends Component {
	
	public PlayerInputComponent(Entity owner) {
		this.id = Component.PLAYER_INPUT;
		this.owner = owner;
	}

	public void update(Input input, int delta) {
		if (owner.playerControlled) {
			Player player = (Player)this.owner;

			// Handle player input.
			if (input.isKeyPressed(Input.KEY_C)) {
				try {
					owner.level.reInit();
					//owner.level.env.sounds.playSFX("zombie_groan1");
					/*if (owner.level.doLighting == false) {
						owner.level.doLighting = true;
					} else {
						owner.level.doLighting = false;
					}*/
					//StateManager.getInstance().setState(2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (input.isKeyDown(Input.KEY_D)) {
				if (!((Player)owner).isHidden()) {
					owner.vel.x = 0.5f;
				}
			}
			if (input.isKeyDown(Input.KEY_A)) {
				if (!((Player)owner).isHidden()) {
					owner.vel.x = -0.5f;
				}
			}
			if (!input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_A)) {
				if (owner.vel.x < 0.0f) {
					owner.vel.x += owner.accel.getX() * 2;
					if (owner.vel.x > 0.0f) owner.vel.x = 0.0f;
				} else if (owner.vel.x > 0.0f) {
					owner.vel.x -= owner.accel.getX() * 2;
					if (owner.vel.x < 0.0f) owner.vel.x = 0.0f;
				}
			}
			if (input.isKeyPressed(Input.KEY_Q) && owner.health != 0) {
				try {
					owner.receiveDamage(1);
					BloodMark bm = new BloodMark(owner.level);
					bm.position.x = this.owner.position.x;
					bm.position.y = this.owner.position.y;
					owner.level.env.spawnEntity(bm);
					owner.level.env.addAttractor(bm, 60, false);
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
			
			if ((input.isKeyDown(Input.KEY_W) || input.isKeyDown(Input.KEY_S)) && player.canClimb) {
				player.isClimbing = true;
				
				if (input.isKeyDown(Input.KEY_W)) {
					player.momentumDirectionY = -1;
				}
				else if (input.isKeyDown(Input.KEY_S)) {
					player.momentumDirectionY = 1;
				}
			}
			
			if (input.isKeyPressed(Input.KEY_E)) {
				if (player.canHide) {
					player.vel = new Vector2f(0, 0);
					player.hide();
					player.level.fl.turnOff();
				} else {
					ArrayList<Entity> usables = this.owner.level.env.getUsableEntities(new Rectangle(this.owner.position.x, this.owner.position.y, this.owner.size.x, this.owner.size.y));
					for (Entity usable: usables) {
						System.out.println("I can use " + usable.name);
						usable.use(this.owner);
					}
				}
			} else if (!input.isKeyDown(Input.KEY_E) && ((Player)owner).isHidden()) {
				((Player)owner).unHide();
				this.owner.level.fl.turnOn();
			}
			if (input.isKeyPressed(Input.KEY_SPACE)){
				ArrayList<Entity> targets = owner.level.env.getUsableEntities(new Rectangle(owner.position.x, owner.position.y, owner.size.x, owner.size.y));
				for (Entity target: targets) {
					if (target != null && target instanceof WoodenCrate) {
						Resolve res = owner.level.env.collisionMap.trace(
								(int)owner.position.x, 
								(int)owner.position.y,
								owner.size.x + 10, 
								target.size.y + 5, 
								(int)owner.size.x, 
								(int)owner.size.y
						);
						//if (res.collision.get("y") == false) {
							((Player)owner).climbOnObject(target);
							((Player)owner).state = "ClimbingObject";
							((Player)owner).objectBeingClimbed = target;
							//owner.setPosition(target.position.getX(), target.position.getY() - owner.boundingBox.getHeight());
							break;
						//}
					}
				}
			}
			if (input.isKeyPressed(Input.KEY_P)) {
				this.owner.level.togglePause();
			}
			if (input.isMousePressed(0)) {
				if (!((Player)owner).isHidden()) {
					owner.level.fl.switchOnOff();
				}
			}
		}
	}

}
