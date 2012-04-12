package nl.jasperbok.zombies.entity.state;

import nl.jasperbok.zombies.entity.mob.Mob;

import org.newdawn.slick.Input;

public class IdleState implements EntityState {
	
	private static IdleState instance = null;

	public void enterState(Mob mob) {
		mob.currentAnimation = mob.idleAnimation;
	}

	public void update(Input input, Mob mob) {
				
	}
	
	public static IdleState getInstance() {
		if (instance == null) {
			instance = new IdleState();
		}
		return instance;
	}
}
