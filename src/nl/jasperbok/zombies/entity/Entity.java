package nl.jasperbok.zombies.entity;

import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public abstract class Entity {
	public Level level;
	public Vector2 position = new Vector2(0.0f, 0.0f);
	public boolean isBlocking = true;
	public boolean playerControlled = false;
	
	public void init(Level level) {
		this.level = level;
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}
}
