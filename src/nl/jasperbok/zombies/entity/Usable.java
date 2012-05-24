package nl.jasperbok.zombies.entity;

import nl.jasperbok.engine.Entity;

import org.newdawn.slick.geom.Rectangle;

public interface Usable {

	public void use(Entity user);
	
	public boolean canBeUsed(Rectangle rect);
}
