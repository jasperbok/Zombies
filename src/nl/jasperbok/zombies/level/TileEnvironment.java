package nl.jasperbok.zombies.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import nl.jasperbok.zombies.entity.Attractor;
import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Trigger;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.mob.Mob;
import nl.jasperbok.zombies.entity.object.BloodMark;
import nl.jasperbok.zombies.entity.mob.MobDirector;
import nl.jasperbok.zombies.sound.SoundManager;
import nl.timcommandeur.zombies.screen.Camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

public class TileEnvironment {
	// Settings.
	private boolean drawBoundingBoxes = false;
	
	// Map variables.
	private TiledMap map;
	private int tileWidth;
	private int tileHeight;
	private Level level;
	private Tile[][] tiles;
	int backgroundLayer;
	int collisionLayer;
	
	// Entity variables.
	private int nextEntId = 0;
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList<Trigger> triggers = new ArrayList<Trigger>();
	private HashMap<String, Entity> namedEntities = new HashMap<String, Entity>();
	private ArrayList<Usable> usableEntities = new ArrayList<Usable>();
	private ArrayList<Entity> attractors = new ArrayList<Entity>();
	private ArrayList<Entity> garbage = new ArrayList<Entity>();
	private ArrayList<Entity> deferredSpawn = new ArrayList<Entity>();
	
	// Utilities.
	public SoundManager sounds;
	public MobDirector mobDirector;
	
	private boolean sortNow = false;
	
	/**
	 * Class constructor.
	 * 
	 * @param mapName The name of the map to load (without .tmx).
	 * @throws SlickException
	 */
	public TileEnvironment(String mapName, Level level) throws SlickException {
		// Load the map and all related variables.
		this.map = new TiledMap("data/maps/" + mapName + ".tmx");
		this.tileWidth = map.getTileWidth();
		this.tileHeight = map.getTileHeight();
		this.level = level;
		this.tiles = MapLoader.loadTiles(map);
		this.backgroundLayer = map.getLayerIndex("background");
		this.collisionLayer = map.getLayerIndex("collision");
		this.sounds = new SoundManager();
		
		MapLoader.loadEntities(this, level, map);
		spawnDeffered();
		this.mobDirector = new MobDirector(level, sounds, getAllMobs());
		Camera.getInstance().setTarget(this.getEntityByName("player"));
		
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
	/*
	public ArrayList<Entity> getAllZombies() {
		ArrayList<Entity> ents = new ArrayList<Entity>();
		for (Entity ent: entities) {
			if (ent instanceof Zombie) {
				ents.add(ent);
			}
		}
		return ents;
	}
	*/
	
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
		mobDirector.moveMobs(container);
		updateEntities(container.getInput(), delta);
		moveEntities(delta);
		updateTriggers(container, delta);
		checkForTileCollisions();
		emptyGarbage();
		
		spawnDeffered();
		
		if (sortNow) {
			this.sortEntities();
			this.sortNow = false;
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
		
		// Update all the attractors.
		for (Entity att: attractors) {
			att.update(input, delta);
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
	
	/**
	 * Checks whether an entity is on something solid with his feet.
	 * 
	 * We don't actually check the whole bottom side of the Entity, but only
	 * two points: one 10px to the left of his center point and one 10px to
	 * the right. If either or both of these points overlaps a solid object
	 * true is returned, otherwise false.
	 * 
	 * @param ent The entity to check.
	 * @return boolean True if the entity is on top of something solid.
	 */
	public boolean isOnGround(Entity ent, boolean resolveCollision) {
		int entY = (int)(ent.boundingBox.getMaxY());
		int relativeLeftX = (int)Math.floor((ent.boundingBox.getCenterX() - 10) / tileWidth);
		int relativeRightX = (int)Math.floor((ent.boundingBox.getCenterX() + 10) / tileWidth);
		int relativeBottomY = (int)Math.floor((ent.boundingBox.getMaxY() + 3) / tileWidth);
		
		// Becomes true if the Entity is on a Tile that has its isBlocking property set to true.
		boolean onAllSolidBlock =  tiles[relativeLeftX][relativeBottomY].isBlocking || tiles[relativeRightX][relativeBottomY].isBlocking;
		
		// Becomes true if the Entity is on a Tile that has its isClimable property set to true.
		boolean onClimableTile = tiles[relativeLeftX][relativeBottomY].isClimable || tiles[relativeRightX][relativeBottomY].isClimable;
		
		// Becomes true if the Entity is anywhere on the top 10 pixels of a Tile that has its isTopSolid property set to true.
		boolean onTopSolidBlock = false;
		if (entY % tileHeight <= 10) {
			if (tiles[relativeLeftX][relativeBottomY].isTopSolid) {
				onTopSolidBlock = true;
			} else if (tiles[relativeRightX][relativeBottomY].isTopSolid) {
				onTopSolidBlock = true;
			}
		}
		
		boolean onEntity = false;
		// This should probably go in the hittest section...
		for (Entity entity: entities) {
			if (entity != ent && entity.isTopSolid) {
				Rectangle topBox = new Rectangle(entity.position.getX(), entity.position.getY(), entity.boundingBox.getWidth(), 10);
				if (topBox.contains(ent.boundingBox.getCenterX() - 10, ent.boundingBox.getMaxY()) || topBox.contains(ent.boundingBox.getCenterX() + 10, ent.boundingBox.getMaxY())) {
					//ent.setPosition(ent.position.getX(), entity.boundingBox.getMinY() - ent.boundingBox.getHeight());
					onEntity = true;
					break;
				}
			}
		}
		return onAllSolidBlock || onClimableTile || onTopSolidBlock || onEntity;
	}
	
	/**
	 * Checks whether an Entity is on a Tile that has its isClimable property set to true.
	 * 
	 * To decide whether the Entity is a climable tile, two points are checked
	 * for collisions with a Tile with its isClimable property set to true.
	 * First the Entity's lowest center point is checked. Second the point
	 * that lies 20px below its top center point is checked.
	 * 
	 * @param ent The Entity to check.
	 * @return boolean True if the Entity's feet are on something climable.
	 */
	public boolean isOnClimableSurface(Entity ent) {
		int relativeX = (int)Math.floor(ent.boundingBox.getCenterX() / tileWidth);
		int relativeBottomY = (int)Math.floor(ent.boundingBox.getMaxY() / tileWidth);
		int relativeTopY = (int)Math.floor((ent.boundingBox.getMinY() + 20) / tileHeight);
		
		return tiles[relativeX][relativeBottomY].isClimable || tiles[relativeX][relativeTopY].isClimable;
	}
	
	public boolean isOnHideableSurface(Entity ent) {
		int relativeX = (int)Math.floor(ent.boundingBox.getCenterX() / tileWidth);
		int relativeBottomY = (int)Math.floor((ent.boundingBox.getMaxY() - 10) / tileWidth);
		int relativeTopY = (int)Math.floor((ent.boundingBox.getMinY() + 50) / tileHeight);
		
		return tiles[relativeX][relativeBottomY].isHideable || tiles[relativeX][relativeTopY].isHideable;
	}
	
	/**
	 * Handles collisions between Entities and Tiles.
	 */
	private void checkForTileCollisions() {
		for (Entity ent: entities) {
			try {
				// Floor collisions.
				int entLeftX = (int)(ent.boundingBox.getCenterX() - 10);
				int entRightX = (int)(ent.boundingBox.getCenterX() + 10);
				int entY = (int)(ent.boundingBox.getMaxY());
				int relativeLeftX = (int)Math.floor(entLeftX / tileWidth);
				int relativeRightX = (int)Math.floor(entRightX / tileWidth);
				int relativeBottomY = (int)Math.floor(entY / tileWidth);
				
				//if (tiles[relativeLeftX][relativeBottomY].isBlocking || (tiles[relativeLeftX][relativeBottomY].isClimable && !ent.isClimbing)) {
				if (tiles[relativeLeftX][relativeBottomY].isBlocking) {
					ent.setPosition(ent.position.getX(), + tiles[relativeLeftX][relativeBottomY].position.getY() - ent.boundingBox.getHeight() + 1);
					// The entity is standing on something solid, so change his y velocity to 0 or less.
					if (ent.velocity.getY() > 0) ent.velocity.set(ent.velocity.getX(), 0);
				//} else if (tiles[relativeRightX][relativeBottomY].isBlocking || (tiles[relativeRightX][relativeBottomY].isBlocking && !ent.isClimbing)) {
				} else if (tiles[relativeRightX][relativeBottomY].isBlocking) {
					ent.setPosition(ent.position.getX(), + tiles[relativeRightX][relativeBottomY].position.getY() - ent.boundingBox.getHeight() + 1);
					// The entity is standing on something solid, so change his y velocity to 0 or less.
					if (ent.velocity.getY() > 0) ent.velocity.set(ent.velocity.getX(), 0);
				}
				
				// Left side collisions.
				int relLeftX = (int)Math.floor(ent.boundingBox.getMinX() / tileWidth);
				int relTopLeftY = (int)(Math.floor((ent.boundingBox.getMinY() + 10) / tileHeight));
				int relBottomLeftY = (int)(Math.floor((ent.boundingBox.getMaxY() - 10) / tileHeight));
				if (tiles[relLeftX][relBottomLeftY].isBlocking) {
					ent.setPosition(tiles[relLeftX][relBottomLeftY].position.getX() + tiles[relLeftX][relBottomLeftY].width, ent.position.getY());
				} else if (tiles[relLeftX][relTopLeftY].isBlocking) {
					//System.out.println("Collision on a side");
					ent.setPosition(tiles[relLeftX][relBottomLeftY].position.getX() + tiles[relLeftX][relBottomLeftY].width, ent.position.getY());
				} else if (tiles[relLeftX][relTopLeftY].isBlocking) {
					//System.out.println("Collision on a side");
					ent.setPosition(tiles[relLeftX][relTopLeftY].position.getX() + tiles[relLeftX][relTopLeftY].width, ent.position.getY());
				}
				
				// Right side collisions.
				int relRightX = (int)Math.floor(ent.boundingBox.getMaxX() / tileWidth);
				int relTopRightY = (int)(Math.floor((ent.boundingBox.getMinY() + 10) / tileHeight));
				int relBottomRightY = (int)(Math.floor((ent.boundingBox.getMaxY() - 10) / tileHeight));
				if (tiles[relRightX][relBottomRightY].isBlocking) {
					ent.setPosition(tiles[relRightX][relBottomRightY].position.getX() - ent.boundingBox.getWidth(), ent.position.getY());
				} else if (tiles[relRightX][relTopRightY].isBlocking) {
					ent.setPosition(tiles[relRightX][relTopRightY].position.getX() - ent.boundingBox.getWidth(), ent.position.getY());
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				
			}
		}
	}
	
	/**
	 * Updates the position of all entities in the level according to their
	 * velocity.
	 */
	private void moveEntities(int delta) {
		for (Entity ent: entities) {
			ent.setPosition(ent.position.x + (ent.velocity.x * delta), ent.position.y + (ent.velocity.y * delta));
		}
	}
	
	public void addAttractor(Rectangle bbox, String type) throws SlickException {		
		switch (type) {
		case "BloodMark":
			BloodMark bm = new BloodMark(level, bbox.getCenterX(), bbox.getCenterY() - 20);
			spawnEntity(bm);
			this.mobDirector.addAttractor(bm, 60, false);
			break;
		}
	}
	
	public void removeAttractor(Attractor att) {
		attractors.remove(att);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, backgroundLayer);
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, collisionLayer);
		
		// Render all the attractors.
		for (Entity att: attractors) {
			att.render(container, g);
		}
		
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
			if (attractors.contains(entity)) attractors.remove(entity);
			if (usableEntities.contains(entity)) usableEntities.remove(entity);
			entity = null;
		}
		garbage.clear();
	}
	
	/**
	 * Returns the player.
	 * 
	 * @return The player.
	 */
	public Player getPlayer() {
		return (Player)this.getEntityByName("player");
	}
}
