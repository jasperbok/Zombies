package nl.jasperbok.zombies.level.Environment;

import java.util.ArrayList;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.mob.Mob;

public class EntityManager {

	private ArrayList<Entity> entities;
	private ArrayList<Usable> usableEntities;
	private ArrayList<Mob> mobs;
	private ArrayList<Entity> attractors;
	private ArrayList<Entity> garbage;
	/**
	 * Contains all the entities in the environment. This variable is made
	 * so we only have to loop over one ArrayList instead of several.
	 */
	private ArrayList<Entity> allEntities;
	private Player player;
	
	public EntityManager() {
		this.entities = new ArrayList<Entity>();
		this.usableEntities = new ArrayList<Usable>();
		this.mobs = new ArrayList<Mob>();
		this.allEntities = new ArrayList<Entity>();
		this.attractors = new ArrayList<Entity>();
	}
}
