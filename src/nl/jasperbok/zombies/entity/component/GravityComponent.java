package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.zombies.entity.Entity;

import org.newdawn.slick.Input;

public class GravityComponent extends Component {
	
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
	
	public void toggleGravity() {
		applyGravity = !applyGravity;
	}

	public void update(Input input, int delta) {
		if (applyGravity) {
			if (!owner.isOnGround && !owner.isClimbing) {
				float yVel = owner.velocity.getY() + gravity;
				if (yVel > maxGravity) yVel = maxGravity;
				owner.velocity.set(owner.velocity.getX(), yVel);
			}
		}
	}
}
