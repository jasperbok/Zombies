package nl.jasperbok.zombies.entity.mob;

import nl.jasperbok.engine.Entity;

public class MobAttractor extends Entity {
	public Entity object;
	public int power = 0;
	
	protected boolean triggerAgression = false;
	
	public MobAttractor(Entity object, int power, boolean triggerAgression) {
		this.object = object;
		this.power = power;
		this.triggerAgression = triggerAgression;
	}
	
	public void update() {
		position.x = object.position.x;
		position.y = object.position.y;
	}
}
