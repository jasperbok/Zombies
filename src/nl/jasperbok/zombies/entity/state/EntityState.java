package nl.jasperbok.zombies.entity.state;

import nl.jasperbok.zombies.entity.mob.Mob;

import org.newdawn.slick.Input;

public interface EntityState {
	
	public void enterState(Mob mob);
	
	public void update(Input input, Mob mob);
}
