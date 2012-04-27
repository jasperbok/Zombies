package nl.jasperbok.zombies.entity.component;

import java.util.ArrayList;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.object.BloodMark;
import nl.jasperbok.zombies.entity.object.WoodenCrate;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class PlayerInputComponent extends Component {
	
	public PlayerInputComponent(Entity owner) {
		this.id = Component.PLAYER_INPUT;
		this.owner = owner;
	}

	public void update(Input input, int delta) {
		if (owner.playerControlled) {
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
					owner.velocity.set(owner.velocity.getX() + owner.acceleration.getX(), owner.velocity.getY());
					if (owner.velocity.getX() > owner.maxVelocity.getX()) {
						owner.velocity.set(owner.maxVelocity.getX(), owner.velocity.getY());
					}
				}
			}
			if (input.isKeyDown(Input.KEY_A)) {
				if (!((Player)owner).isHidden()) {
					owner.velocity.set(owner.velocity.getX() - owner.acceleration.getX(), owner.velocity.getY());
					if (owner.velocity.getX() < -owner.maxVelocity.getX()) {
						owner.velocity.set(-owner.maxVelocity.getX(), owner.velocity.getY());
					}
				}
			}
			if (!input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_A)) {
				if (owner.velocity.x < 0.0f) {
					owner.velocity.x += owner.acceleration.getX() * 2;
					if (owner.velocity.x > 0.0f) owner.velocity.x = 0.0f;
				} else if (owner.velocity.x > 0.0f) {
					owner.velocity.x -= owner.acceleration.getX() * 2;
					if (owner.velocity.x < 0.0f) owner.velocity.x = 0.0f;
				}
			}
			if (input.isKeyPressed(Input.KEY_Q) && ((LifeComponent)owner.getComponent(Component.LIFE)).getHealth() != 0) {
				try {
					((LifeComponent)owner.getComponent(Component.LIFE)).takeDamage(1);
					BloodMark bm = new BloodMark(owner.level);
					bm.position.x = this.owner.position.x;
					bm.position.y = this.owner.position.y;
					owner.level.env.spawnEntity(bm);
					owner.level.env.mobDirector.addAttractor(bm, 60, false);
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
			if (input.isKeyDown(Input.KEY_W)){
				if (owner.level.env.isOnClimableSurface(owner)) {
					owner.isClimbing = true;
					owner.velocity.set(owner.velocity.getX(), -((Player)owner).climbSpeed);
				}
			}
			if (input.isKeyDown(Input.KEY_S)){
				if (owner.level.env.isOnClimableSurface(owner)) {
					owner.isClimbing = true;
					owner.velocity.set(owner.velocity.getX(), ((Player)owner).climbSpeed);
				}
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				if (owner.level.env.isOnHideableSurface(owner)) {
					owner.velocity = new Vector2f(0, 0);
					((Player)owner).switchHide();
				} else {
					ArrayList<Entity> usables = this.owner.level.env.getUsableEntities(this.owner.boundingBox);
					for (Entity usable: usables) {
						System.out.println("I can use " + usable.name);
						usable.use(this.owner);
					}
				}
			}
			if (input.isKeyPressed(Input.KEY_SPACE)){
				ArrayList<Entity> targets = owner.level.env.getUsableEntities(owner.boundingBox);
				for (Entity target: targets) {
					if (target != null && target instanceof WoodenCrate) {
						owner.setPosition(target.position.getX(), target.position.getY() - owner.boundingBox.getHeight());
						break;
					}
				}
			}
			if (input.isMousePressed(0)) {
				owner.level.fl.switchOnOff();
			}
		}
	}

}
