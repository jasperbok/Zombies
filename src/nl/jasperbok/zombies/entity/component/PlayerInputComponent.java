package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.zombies.entity.Entity;

import org.newdawn.slick.Input;

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
			if (input.isKeyDown(Input.KEY_Q)) {
				addBloodMark();
			}
			if (input.isKeyDown(Input.KEY_W)){
				if (owner.level.env.isOnClimableSurface(owner)) {
					owner.isClimbing = true;
					owner.velocity.set(owner.velocity.getX(), -owner.climbSpeed);
				}
			}
			if (input.isKeyDown(Input.KEY_S)){
				if (owner.level.env.isOnClimableSurface(owner)) {
					owner.isClimbing = true;
					owner.velocity.set(owner.velocity.getX(), owner.climbSpeed);
				}
			}
			if (input.isKeyPressed(Input.KEY_E)) {
				owner.useObject();
			}
			if (input.isMousePressed(0)) {
				owner.level.fl.switchOnOff();
			}
		}
	}

}
