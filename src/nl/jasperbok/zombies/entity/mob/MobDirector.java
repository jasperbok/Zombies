package nl.jasperbok.zombies.entity.mob;

import java.io.IOException;
import java.util.List;

import nl.jasperbok.zombies.entity.Entity;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

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
	public List<MobAttractor> attractors;
	
	private Audio wavEffect;
	
	public MobDirector() {
		mobs = new LoopingList<Mob>();
	}
	
	public MobDirector(List<Mob> mobs) {
		refresh(mobs);
	}
	
	public void init() throws IOException {
		wavEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sound/zombiegroan1.wav"));
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
		for (Mob mob : mobs) {
			keepDistanceBetweenAllMobs(mob);
			for (MobAttractor attractor : attractors) {
				tendTowardsPoint(mob, new Vector2f(attractor.position.x, attractor.position.y), attractor.power);
			}
		}
	}
	
	/**
	 * Makes all the mobs push themselves away slightly from each other.
	 * The push effect will be set to the velocity.
	 */
	protected void keepDistanceBetweenAllMobs(Mob mob1) {
		for (Mob mob2 : mobs) {
			if (mob1 != mob2)
				keepDistanceBetweenMobs(mob1, mob2);
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
	 * @param power
	 */
	protected void tendTowardsPoint(Mob mob, Vector2f point, int power) {
		// The division by (1 * (1000 / power)) is a limiter.
		// The higher the division the slower the mob will move towards a point.
		mob.velocity.x = (10 * (power)) / (mob.position.x - point.x);
	}
	
	/**
	 * Adds an attractor to which the mobs will be attracted.
	 * 
	 * @param object
	 * @param power
	 */
	public void addAttractor(Entity object, int power) {
		MobAttractor attractor = new MobAttractor(object, power);
		attractors.add(attractor);
	}
	
	/**
	 * Removes an attractor from the list where the attractor belongs to the given object.
	 * 
	 * @param object
	 */
	public void removeAttractor(Entity object) {
		for (MobAttractor attractor : attractors) {
			if (attractor.object == object) {
				attractors.remove(attractor);
				attractor = null;
			}
		}
	}
	
	public void makeNoise() {
		wavEffect.playAsSoundEffect(1.0f, 1.0f, false);
	}
}
