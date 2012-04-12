package nl.jasperbok.zombies.entity.state;

import nl.jasperbok.zombies.entity.mob.Mob;

import org.newdawn.slick.Input;

public class WalkState implements EntityState {
	
	private static WalkState instance = null;

	public void enterState(Mob mob) {
		if (mob.velocity.getX() > 0) mob.currentAnimation = mob.walkRightAnimation;
		if (mob.velocity.getX() < 0) mob.currentAnimation = mob.walkLeftAnimation;
	}

	public void update(Input input, Mob mob) {
		if (mob.velocity.getX() > 0) {
			float xVel = mob.velocity.getX() + mob.acceleration.getX();
			if (xVel > mob.maxVelocity.getX()) xVel = mob.maxVelocity.getX();
			mob.velocity.set(xVel, mob.velocity.getY());
		} else if (mob.velocity.getX() < 0) {
			float xVel = mob.velocity.getX() - mob.acceleration.getX();
			if (xVel < -mob.maxVelocity.getX()) xVel = -mob.maxVelocity.getX();
			mob.velocity.set(xVel, mob.velocity.getY());
		}
	}
	
	public static WalkState getInstance() {
		if (instance == null) {
			instance = new WalkState();
		}
		return instance;
	}
}
