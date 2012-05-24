package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.engine.Entity;

import org.newdawn.slick.Input;

public class GravityComponent extends Component {
	/**
	 * Settings for the gravity component.
	 */
	private float gravity;
	private float maxGravity;
	private boolean applyGravity = true;
	
	public GravityComponent(Entity owner) {
		this(0.005f, owner);
	}

	public GravityComponent(float gravity, Entity owner) {
		this.id = Component.GRAVITY;
		this.gravity = gravity;
		this.maxGravity = gravity * 500;
		this.owner = owner;
	}
	
	/**
	 * Switches the gravity on or off.
	 */
	public void toggleGravity() {
		applyGravity = !applyGravity;
	}

	public void update(Input input, int delta) {
		if (applyGravity) {
			if (!owner.standing && !owner.isClimbing) {
				float yVel = owner.vel.getY() + gravity;
				if (yVel > maxGravity) yVel = maxGravity;
				owner.vel.set(owner.vel.getX(), yVel);
			}
		}
	}
}
