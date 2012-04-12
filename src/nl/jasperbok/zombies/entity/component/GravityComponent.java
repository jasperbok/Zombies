package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.zombies.entity.Entity;

import org.newdawn.slick.Input;

public class GravityComponent extends Component {
	
	private float gravity;
	
	public GravityComponent(Entity owner) {
		this(0.5f, owner);
	}

	public GravityComponent(float gravity, Entity owner) {
		this.id = 1;
		this.gravity = gravity;
		this.owner = owner;
	}

	public void update(Input input, int delta) {
		System.out.println(owner.isOnGround);
		if (!owner.isOnGround && !owner.isClimbing) {
			owner.velocity.set(owner.velocity.getX(), owner.velocity.getY() + gravity * delta);
		}
		//owner.velocity.set(owner.velocity.getX(), owner.velocity.getY() + gravity * delta);
	}
}
