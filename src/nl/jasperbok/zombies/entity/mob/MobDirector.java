package nl.jasperbok.zombies.entity.mob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.jasperbok.zombies.entity.Entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import LightTest.LoopingList;

/**
 * @author Tim Commandeur
 * 
 * The MobDirector handles the behavior of the mobs.
 * 
 * The Mobdirector is responsible for the behavior the mobs will (try) to execute.
 * It is not a physics class so it will not handle gravity, collision with surroundings, etc.
 */
public class MobDirector {
	public List<Mob> mobs;
	public List<MobAttractor> attractors;
	
	private Audio wavEffect;
	
	private ArrayList<Mob> closedForDistanceChecking;
	private int distanceCheckingTimeoutCeil = 0;
	private int distanceCheckingTimer = 10;
	
	private HashMap<Mob, List> trackingMobs; 
	
	public MobDirector() {
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mobs = new LoopingList<Mob>();
		attractors = new LoopingList<MobAttractor>();
	}
	
	public MobDirector(List<Mob> mobs) {
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refresh(mobs);
		attractors = new LoopingList<MobAttractor>();
	}
	
	public void init() throws IOException {
		trackingMobs = new HashMap<Mob, List>();
		wavEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sound/sfx/zombiegroan1.wav"));
	}
	
	/**
	 * Updates the moblist the MobDirector uses.
	 * 
	 * @param mobs
	 */
	public void refresh(List<Mob> mobs) {
		this.mobs = mobs;
		for (Mob mob : mobs) {
			updateTrackingMobs(mob);
		}
	}
	
	/**
	 * Executes all logic to make mobs behave accordingly.
	 */
	public void moveMobs(GameContainer container) {
		
		// I think it will work without this bit.
		// So check it out!!!
		//---
			for (MobAttractor attractor : attractors) {
				attractor.update();
			}
		//---
		
		closedForDistanceChecking = new ArrayList<Mob>();
		
		for (Mob mob : mobs) {
			Vector2f v = new Vector2f();
			if (closedForDistanceChecking.contains(mob) != true && distanceCheckingTimer == 0) {
				v = v.add(keepDistanceBetweenAllMobs(mob));
				//closedForDistanceChecking.add(mob);
				distanceCheckingTimer = distanceCheckingTimeoutCeil;
			} else {
				distanceCheckingTimer--;
			}
			
			for (MobAttractor attractor : attractors) {
				attractor.update();
				v = v.add(tendTowardsPoint(mob, new Vector2f(attractor.position.x, attractor.position.y), attractor.power));
				if (attractor.triggerAgression && Math.abs(mob.position.x - attractor.position.x) <= mob.agressionRange) {
					v = new Vector2f(0, 0);
					// End the game
					try {
						container.reinit();
					} catch (SlickException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			mob.velocity.x += v.x;
			mob.velocity.y += v.y;
			
			mob.velocity.x /= 50;
			
			//System.out.println("v.x" + mob.velocity.x);
		}
	}
	
	public void updateTrackingMobs(Mob mob) {
		Mob leftMob = null;
		Mob rightMob = null;
		
		// The distance of the closest mob.
		float leftMobXDis = 0;
		float rightMobXDis = 0;
		
		// Check which mobs are closest on each side.
		for (Mob mob2 : mobs) {
			if (mob2 != mob) {
				float distance = Math.abs(mob.position.x - mob2.position.x);
				if (mob.position.x < mob2.position.x) {
					// mob2 is left of mob
					if (leftMob == null || distance < leftMobXDis)
					{
						leftMob = mob2;
						leftMobXDis = distance;
					}
				} else {
					// mob2 is right of mob
					if (rightMob == null || distance < rightMobXDis)
					{
						rightMob = mob2;
						rightMobXDis = distance;
					}
				}
			}
		}
		
		// Create the list the that the mob should keep track of.
		List<Mob> mobList = new LoopingList<Mob>();
		if (leftMob != null) mobList.add(leftMob);
		if (rightMob != null) mobList.add(rightMob);
		
		// Update the tracking list
		trackingMobs.put(mob, mobList);
	}
	
	/**
	 * Makes all the mobs push themselves away slightly from each other.
	 * The push effect will be set to the velocity.
	 */
	protected Vector2f keepDistanceBetweenAllMobs(Mob mob1) {
		Vector2f v = new Vector2f(0, 0);
		
		List<Mob> mobList = trackingMobs.get(mob1);
		
		if (mobList != null && mobList.size() > 0) {
			for (Mob mob2 : mobList) {
				if (closedForDistanceChecking.contains(mob2) != true) {
					if (mobs.indexOf(mob1) != mobs.indexOf(mob2)) {
						v = v.add(keepDistanceBetweenMobs(mob1, mob2));
					}
				}
			}
		}
		
		return v;
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
	protected Vector2f keepDistanceBetweenMobs(Mob mob1, Mob mob2) {
		Vector2f v = new Vector2f(0, 0);
		
		float mob1Radius = mob1.boundingBox.getWidth() / 4;
		float mob2Radius = mob2.boundingBox.getWidth() / 4;
		
		//System.out.println("mob2.x: " + (mob2.position.x));
		//System.out.println("sum: " + (mob1.position.x - mob2.position.x));
		if (Math.abs(mob1.position.x - mob2.position.x) < mob2Radius) {
			//v.x += (mob2.position.x - mob1.position.x) / Math.abs(mob2.position.x - mob1.position.x) * 5;
			v.x += (mob1.position.x - mob2.position.x);
		}
		//System.out.println("vx: " + v.x);
		return v;
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
	protected Vector2f tendTowardsPoint(Mob mob, Vector2f point, int power) {
		Vector2f v = new Vector2f(0, 0);
		
		// The division by (1 * (1000 / power)) is a limiter.
		// The higher the division the slower the mob will move towards a point.
		//v.x = (10 * (power)) / ((mob.position.x - point.x) * 100);
		if (mob.position.x < point.x) v.x = 1;
		else if (mob.position.x > point.x) v.x = -1;
		
		return v;
	}
	
	/**
	 * Adds an attractor to which the mobs will be attracted.
	 * 
	 * @param object
	 * @param power
	 */
	public void addAttractor(Entity object, int power, boolean triggerAgression) {
		MobAttractor attractor = new MobAttractor(object, power, triggerAgression);
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
