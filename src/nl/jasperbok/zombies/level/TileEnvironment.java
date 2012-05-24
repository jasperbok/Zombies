package nl.jasperbok.zombies.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import nl.jasperbok.engine.CollisionMap;
import nl.jasperbok.engine.Entity;
import nl.jasperbok.engine.Timer;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Trigger;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.mob.Mob;
import nl.jasperbok.zombies.entity.mob.MobAttractor;
import nl.jasperbok.zombies.sound.SoundManager;
import nl.timcommandeur.zombies.screen.Camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import LightTest.LoopingList;

public class TileEnvironment {
	// Settings.
	private boolean drawBoundingBoxes = false;
	
	// Map variables.
	private TiledMap map;
	private int tileWidth;
	private int tileHeight;
	private Level level;
	int backgroundLayer;
	int collisionLayer;
	public List<MobAttractor> attractors;
	public List<MobAttractor> attractorGarbage;
	
	// Entity variables.
	private int nextEntId = 0;
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList<Trigger> triggers = new ArrayList<Trigger>();
	private HashMap<String, Entity> namedEntities = new HashMap<String, Entity>();
	private ArrayList<Usable> usableEntities = new ArrayList<Usable>();
	private ArrayList<Entity> garbage = new ArrayList<Entity>();
	private ArrayList<Entity> deferredSpawn = new ArrayList<Entity>();
	
	// Utilities.
	public SoundManager sounds;
	public CollisionMap collisionMap;
	public ArrayList<Timer> timers = new ArrayList<Timer>();
	
	private boolean sortNow = false;
	
	/**
	 * Class constructor.
	 * 
	 * @param mapName The name of the map to load (without .tmx).
	 * @throws SlickException
	 */
	public TileEnvironment(String mapName, Level level) throws SlickException {
		// Load the map and all related variables.
		this.sounds = new SoundManager();
		this.map = new TiledMap("data/maps/" + mapName + ".tmx");
		this.tileWidth = map.getTileWidth();
		this.tileHeight = map.getTileHeight();
		this.level = level;
		this.backgroundLayer = map.getLayerIndex("background");
		this.collisionLayer = map.getLayerIndex("collision");

		attractors = new LoopingList<MobAttractor>();
		attractorGarbage = new LoopingList<MobAttractor>();
		
		this.collisionMap = new CollisionMap(this.map);
		MapLoader.loadEntities(this, level, map);
		spawnDeffered();
		Camera.getInstance().setTarget(this.getEntityByName("player"));
		
		//this.collisionMap.trace(400, 560, 80, 400, 1, 1).printInfo();
		//this.collisionMap.trace(560, 400, 320, -80, 1, 1).printInfo();
		//this.collisionMap.trace(400, 719, 160, 80, 1, 1).printInfo();
		
		// If there is no checkpoint for this level yet, create one
		// at the player's starting position.
		if ( !((MergedLevel)this.level).checkPoint.containsKey(mapName) ) {
			Entity player = this.getEntityByName("player");
			this.setCheckpoint((int)player.position.x, (int)player.position.y, player.health);
			((MergedLevel)this.level).checkPoint.put(mapName, 1);
		}
		
		// Neat loop to debug stuff in the map.
		/*for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (tiles[x][y].isBlocking) {
					System.out.println("Blocker!!!");
				}
			}
		}*/
	}
	
	public void addTrigger(Trigger trigger) {
		this.triggers.add(trigger);
	}
	
	public Timer addTimer(float seconds) {
		Timer timer = new Timer(seconds);
		if (this.timers.add(timer)) {
			return timer;
		} else {
			return null;
		}
	}
	
	public Entity spawnEntity(Entity ent) {
		ent.id = this.nextEntId;
		this.deferredSpawn.add(ent);
		if (ent.name != "") {
			this.namedEntities.put(ent.name, ent);
		}
		this.nextEntId++;
		this.sortNow = true;
		return ent;
	}
	
	/**
	 * Returns the Entity with the given name.
	 * 
	 * @param name The name of the Entity you're looking for.
	 * @return An Entity if an Entity with the given name exists, otherwise
	 * null.
	 */
	public Entity getEntityByName(String name) {
		if (this.namedEntities.containsKey(name)){
			return this.namedEntities.get(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the Entity with the given id.
	 * 
	 * @param id The id of the Entity you're looking for.
	 * @return An Entity if an Entity with the given id exists, otherwise null.
	 */
	public Entity getEntityById(int id) {
		for (Entity ent: entities) {
			if (ent.id == id) {
				return ent;
			}
		}
		return null;
	}
	
	public ArrayList<Entity> getAllEntities() {
		ArrayList<Entity> ents = new ArrayList<Entity>();
		for (Entity ent: entities) {
			if (ent instanceof Entity) {
				ents.add(ent);
			}
		}
		return ents;
	}
	
	public ArrayList<Mob> getAllMobs() {
		ArrayList<Mob> ents = new ArrayList<Mob>();
		for (Entity ent: entities) {
			if (ent instanceof Mob) {
				ents.add((Mob)ent);
			}
		}
		return ents;
	}
	
	private void sortEntities() {
		Collections.sort(this.entities, new Comparator<Entity>() {
			public int compare(Entity one, Entity two) {
				return one.zIndex - two.zIndex;
			}
		});
	}
	
	/**
	 * Returns the first Usable who's 'use activation field' lies within the
	 * given rectangle.
	 * 
	 * @param rect The rectangle where the Usable should react on.
	 * @return Reference to the first Usable within the given rectangle.
	 */
	public ArrayList<Entity> getUsableEntities(Rectangle rect) {
		ArrayList<Entity> usables = new ArrayList<Entity>();

		for (Entity ent: entities) {
			if (ent.canBeUsed(rect)) {
				usables.add(ent);
			}
		}
		
		return usables;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		updateTimers(delta);
		updateEntities(container.getInput(), delta);
		updateTriggers(container, delta);
		checkEntities();
		emptyGarbage();
		
		spawnDeffered();
		
		for (Entity ent1 : entities) {
			for (Entity ent2 : entities) {
				if (ent1 != ent2 && ent1.isSolid == false && ent2.isSolid == true && ent1.touches(ent2)) {
					System.out.println(ent1.boundingBox.getMaxX() + " " + ent2.boundingBox.getMinX());
					if (ent1.boundingBox.getMaxX() > ent2.boundingBox.getMinX() && ent1.position.x < ent2.position.x) {
						ent1.position.x -= 1;
						ent1.vel.x = -0.06f;
					} else if (ent1.boundingBox.getMinX() < ent2.boundingBox.getMaxX() && ent1.position.x > ent2.position.x) {
						ent1.position.x += 1;
						ent1.vel.x = 0.06f;
					}
				}
			}
		}
		
		if (sortNow) {
			this.sortEntities();
			this.sortNow = false;
		}
	}
	
	public void updateTimers(int delta) {
		for (Timer timer: this.timers) {
			timer.update(delta);
		}
	}
	
	public void checkEntities() {
		for (int i = 0; i < this.entities.size(); i++) {
			Entity entity = this.entities.get(i);
			
			// Skip this Entity if it doesn't check.
			if (
					entity.type == Entity.Type.NONE &&
					entity.checkAgainst == Entity.Type.NONE &&
					entity.collides == Entity.Collides.NEVER
			) {
				continue;
			}
			
			for (int j = i+1; j < this.entities.size(); j++) {
				if (entity.touches(this.entities.get(j))) {
					Entity.checkPair(entity, this.entities.get(j));
				}
			}
		}
	}
	
	public void spawnDeffered() {
		if (this.deferredSpawn.size() > 0) {
			for (Entity ent: this.deferredSpawn) {
				this.entities.add(ent);
			}
			this.deferredSpawn = new ArrayList<Entity>();
		}
	}
	
	private void updateTriggers(GameContainer container, int delta) {
		for (Trigger trigger: this.triggers) {
			trigger.update(container, delta);
		}
	}
	
	private void updateEntities(Input input, int delta) {
		for (Entity ent: this.entities) {
			ent.update(input, delta);
		}
	}
	
	/**
	 * Checks if the given entity collides with another entity.
	 * 
	 * @param checkingEntity
	 * @return ArrayList<Entity> The entities the given entity collides with.
	 */
	public ArrayList<Entity> checkForEntityCollision(Entity checkingEntity) {
		ArrayList<Entity> colliding = new ArrayList<Entity>();
		
		for (Entity entity : entities) {
			if (checkingEntity != entity && checkingEntity.touches(entity)) {
				colliding.add(entity);
			}
		}
		
		return colliding;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, backgroundLayer);
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, collisionLayer);
		
		// Render all the entities.
		for (Entity ent: entities) {
			ent.render(container, g);
		}
		
		// Render boundingBoxes if this setting is turned on.
		if (drawBoundingBoxes) {
			for (Entity ent: entities) {
				g.draw(ent.boundingBox);
			}
		}
	}
	
	/**
	 * Removes the given Entity.
	 * 
	 * @param entity
	 */
	public void removeEntity(Entity entity) {
		garbage.add(entity);
	}
	
	public void addEntity(Entity ent) {
		this.spawnEntity(ent);
	}
	
	/**
	 * Clears the garbage array.
	 * @param entity
	 */
	public void emptyGarbage() {
		for (Entity entity : garbage) {
			if (entities.contains(entity)) entities.remove(entity);
			if (usableEntities.contains(entity)) usableEntities.remove(entity);
			entity = null;
		}
		garbage.clear();
	}
	
	public void setCheckpoint(int x, int y, int hp) {
		((MergedLevel)this.level).checkPoint.put("x", x);
		((MergedLevel)this.level).checkPoint.put("y", y);
		((MergedLevel)this.level).checkPoint.put("hp", hp);
		System.out.println("saved");
	}
	
	/**
	 * Returns the player.
	 * 
	 * @return The player.
	 */
	public Player getPlayer() {
		return (Player)this.getEntityByName("player");
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
				attractorGarbage.add(attractor);
			}
		}
	}
}
