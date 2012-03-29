package nl.jasperbok.zombies.entity.mob;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.level.Level;

import LightTest.LoopingList;

public abstract class MobDirector {
	public List<Mob> mobs;
	
	public MobDirector() {
		mobs = new LoopingList<Mob>();
	}
	
	public void refresh(List<Mob> mob) {
		this.mobs = mobs;
	}
	
	protected void keepDistance(Mob mob1, Mob mob2) {
		
	}
	
	protected void tendTowardsPoint(Mob mob, Vector2f v) {
		
	}
}
