package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.zombies.entity.Entity;

import org.newdawn.slick.Input;

public abstract class Component {
	
	public static int GRAVITY = 1;

	public int id;
	protected Entity owner;
	
	public int getId() {
		return id;
	}
	
	public void setOwner(Entity ent) {
		owner = ent;
	}
	
	public abstract void update(Input input, int delta);
}
