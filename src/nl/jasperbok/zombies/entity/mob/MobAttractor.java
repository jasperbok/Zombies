package nl.jasperbok.zombies.entity.mob;

import nl.jasperbok.zombies.entity.Entity;

public class MobAttractor extends Entity {
	public Entity object;
	public int power = 0;
	
	public MobAttractor(Entity object, int power) {
		this.object = object;
		this.power = power;
	}
	
	public void update() {
		position.x = object.position.x;
		position.y = object.position.y;
	}
}
