package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.object.WoodenCrate;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class PlayerInputComponent extends Component {
	
	public PlayerInputComponent(Entity owner) {
		this.id = Component.PLAYER_INPUT;
		this.owner = owner;
	}

	public void update(Input input, int delta) {
		if (owner.playerControlled) {
			// Handle player input.
			if (input.isKeyDown(Input.KEY_D)) {
				owner.velocity.set(owner.velocity.getX() + owner.acceleration.getX(), owner.velocity.getY());
				if (owner.velocity.getX() > owner.maxVelocity.getX()) {
					owner.velocity.set(owner.maxVelocity.getX(), owner.velocity.getY());
				}
			}
			if (input.isKeyDown(Input.KEY_A)) {
				owner.velocity.set(owner.velocity.getX() - owner.acceleration.getX(), owner.velocity.getY());
				if (owner.velocity.getX() < -owner.maxVelocity.getX()) {
					owner.velocity.set(-owner.maxVelocity.getX(), owner.velocity.getY());
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
					owner.level.env.addAttractor(owner.boundingBox, "BloodMark");
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
				if (owner.level.env.isOnHidableSurface(owner)) {
					((Player)owner).switchHide();
				} else {
					Usable target = owner.level.env.getUsableEntity(owner.boundingBox);
					if (target != null) {
						target.use(owner);
					}
				}
			}
			if (input.isKeyPressed(Input.KEY_SPACE)){
				Entity target = (Entity) owner.level.env.getUsableEntity(owner.boundingBox);
				if (target != null && target instanceof WoodenCrate) {
					owner.setPosition(target.position.getX(), target.position.getY() - owner.boundingBox.getHeight());
				}
			}
			if (input.isMousePressed(0)) {
				owner.level.fl.switchOnOff();
			}
		}
	}

}
