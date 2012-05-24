package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.engine.Entity;

import org.newdawn.slick.Input;

public abstract class Component {
	
	public static final int GRAVITY = 1;
	public static final int DRAGGABLE = 2;
	public static final int LIFE = 3;
	public static final int DAMAGING_AURA = 4;
	public static final int PLAYER_INPUT = 20;

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
