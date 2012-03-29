package nl.jasperbok.zombies.entity.mob;

import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import LightTest.LoopingList;

/**
 * @author timcommandeur
 * 
 * The MobDirector handles the behaviour of the mobs.
 * 
 * The Mobdirector is responsable for the behaviour the mobs will (try) to execute.
 * It is not a physics class so it will not handle gravity, collision with surroundings, etc.
 */
public class MobDirector {
	public List<Mob> mobs;
	
	public MobDirector() {
		mobs = new LoopingList<Mob>();
	}
	
	public MobDirector(List<Mob> mobs) {
		refresh(mobs);
	}
	
	/**
	 * Updates the moblist the MobDirector uses.
	 * 
	 * @param mobs
	 */
	public void refresh(List<Mob> mobs) {
		this.mobs = mobs;
	}
	
	/**
	 * Executes all logic to make mobs behave accordingly.
	 */
	public void moveMobs() {
		keepDistanceBetweenAllMobs();
	}
	
	/**
	 * Makes all the mobs push themselves away slightly from each other.
	 * The push effect will be set to the velocity.
	 */
	protected void keepDistanceBetweenAllMobs() {
		for (Mob mob1 : mobs) {
			for (Mob mob2 : mobs) {
				if (mob1 != mob2)
					keepDistanceBetweenMobs(mob1, mob2);
			}
		}
	}
	
	/**
	 * Pushes mobs away from each other slightly.
	 * 
	 * Do note the first given mob moves half the desired distance from the second given mob.
	 * This is because this method will be executed twice (once for each mob).
	 * 
	 * @param mob1
	 * @param mob2
	 */
	protected void keepDistanceBetweenMobs(Mob mob1, Mob mob2) {
		float mob1Radius = mob1.boundingBox.getWidth() / 2;
		float mob2Radius = mob2.boundingBox.getWidth() / 2;
		
		if (Math.round(mob1.position.x - mob2.position.x) < mob2Radius)
			mob1.velocity.x += mob1.position.x - mob2.position.x;
	}
	
	/**
	 * Makes the mob tend towards a certain point.
	 * 
	 * Keep in mind there is no limit to the mobs speed in this method, so the velocity should be
	 * limited by the mobs own class. 
	 * 
	 * @param mob
	 * @param point
	 */
	protected void tendTowardsPoint(Mob mob, Vector2f point) {
		// The division by 100 is a limiter.
		// The higher the division the slower the mob will move towards a point.
		mob.velocity.x = (mob.position.x - point.x) / 100;
	}
}
