package nl.jasperbok.zombies.level;

import java.util.ArrayList;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.mob.Mob;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

public class TileEnvironment {
	private TiledMap map;
	private String mapName;
	
	private ArrayList<Entity> entities;
	private ArrayList<Usable> usableEntities;
	private ArrayList<Mob> mobs;
	private Player player;
	
	public TileEnvironment(String mapName) throws SlickException {
		this.map = new TiledMap("data/maps/" + mapName + ".tmx");
		this.mapName = mapName;
		
		this.entities = new ArrayList<Entity>();
		this.usableEntities = new ArrayList<Usable>();
		this.mobs = new ArrayList<Mob>();
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		updateEntities(container.getInput(), delta);
		moveEntities();
		checkForCollisions();
	}
	
	private void updateEntities(Input input, int delta) {
		for (Entity ent: entities) {
			ent.update(input, delta);
		}
		for (Mob mob: mobs) {
			mob.update(delta);
		}
	}
	
	private void checkForCollisions() {
		
	}
	
	/**
	 * Updates the position of all entities in the level according to their
	 * velocity.
	 */
	private void moveEntities() {
		for (Entity ent: entities) {
			ent.setPosition(ent.position.x + ent.velocity.x, ent.position.y + ent.velocity.y);
		}
		for (Mob mob: mobs) {
			mob.setPosition(mob.position.x + mob.velocity.x, mob.position.y + mob.velocity.y);
		}
		player.setPosition(player.position.x + player.velocity.x, player.position.y + player.velocity.y);
	}
	
	/**
	 * Add an entity to the list of entities.
	 * 
	 * @param ent The entity to add to the list.
	 */
	public void addEntity(Entity ent) {
		entities.add(ent);
		if (ent instanceof Usable) usableEntities.add((Usable) ent);
	}
	
	/**
	 * Add a mob to the list of mobs.
	 * 
	 * @param mob The mob to add to the list.
	 */
	public void addMob(Mob mob) {
		mobs.add(mob);
	}
	
	/**
	 * Returns the first Usable who's 'use activation field' lies within the
	 * given rectangle.
	 * 
	 * @param rect The rectangle where the Usable should react on.
	 * @return Reference to the first Usable within the given rectangle.
	 */
	public Usable getUsableEntity(Rectangle rect) {
		for (Usable obj: usableEntities) {
			if (obj.canBeUsed(rect)) return obj;
		}
		return null;
	}
	
	/** GETTERS AND SETTERS **/
	
	public String getMapName() {
		return mapName;
	}
	
	/**
	 * Sets the entity representing the player in the environment.
	 * 
	 * @param player The player.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
}
