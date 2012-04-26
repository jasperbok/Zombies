package nl.jasperbok.zombies.level;

import java.util.ArrayList;
import java.util.HashMap;

import nl.jasperbok.zombies.entity.Attractor;
import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
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
	private String mapName;
	private int tileWidth;
	private int tileHeight;
	private Level level;
	private Tile[][] tiles;
	int backgroundLayer;
	int collisionLayer;
	
	SoundManager sounds;
	
	// Entity variables.
	private int nextEntId = 0;
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private HashMap<String, Entity> namedEntities = new HashMap<String, Entity>();
	private ArrayList<Usable> usableEntities = new ArrayList<Usable>();
	private ArrayList<Mob> mobs = new ArrayList<Mob>();
	private ArrayList<Entity> attractors = new ArrayList<Entity>();
	private ArrayList<Entity> garbage = new ArrayList<Entity>();
	/**
	 * Contains all the entities in the environment. This variable is made
	 * so we only have to loop over one ArrayList instead of several.
	 */
	private ArrayList<Entity> allEntities = new ArrayList<Entity>();
	private Player player;
	
	// Utilities.
	public MobDirector mobDirector;
	
	/**
	 * Class constructor.
	 * 
	 * @param mapName The name of the map to load (without .tmx).
	 * @throws SlickException
	 */
	public TileEnvironment(String mapName, Level level) throws SlickException {
		// Load the map and all related variables.
		this.map = new TiledMap("data/maps/" + mapName + ".tmx");
		this.mapName = mapName;
		this.tileWidth = map.getTileWidth();
		this.tileHeight = map.getTileHeight();
		this.level = level;
		this.tiles = MapLoader.loadTiles(map);
		this.backgroundLayer = map.getLayerIndex("background");
		this.collisionLayer = map.getLayerIndex("collision");
		this.mobDirector = new MobDirector(mobs);
		
		MapLoader.loadEntities(this, level, map);
		
		Camera.getInstance().setTarget(this.getEntityByName("player"));
		
		sounds = new SoundManager();
		
		// Neat loop to debug stuff in the map.
		/*for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (tiles[x][y].isBlocking) {
					System.out.println("Blocker!!!");
				}
			}
		}*/
	}
	
	public Entity spawnEntity(Entity ent) {
		ent.id = this.nextEntId;
		this.entities.add(ent);
		this.allEntities.add(ent); // Remove this when we use a single entity array.
		if (ent.name != "") {
			this.namedEntities.put(ent.name, ent);
		}
		this.nextEntId++;
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
	
	public ArrayList<Entity> getAllMobs() {
		ArrayList<Entity> ents = new ArrayList<Entity>();
		for (Entity ent: entities) {
			if (ent instanceof Mob) {
				ents.add(ent);
			}
		}
		return ents;
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
	public void update(GameContainer container, int delta) throws SlickException {
		mobDirector.moveMobs(container);
		updateEntities(container.getInput(), delta);
		moveEntities(delta);
		checkForTileCollisions();
		emptyGarbage();
		//checkForCollisions();
	}
	
	private void updateEntities(Input input, int delta) {
		for (Entity ent: allEntities) {
			ent.update(input, delta);
			ent.updateRenderPosition();
		}
		
		// Render all the attractors.
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
		
		for (Entity entity : allEntities) {
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
		int relativeBottomY = (int)Math.floor(ent.boundingBox.getMaxY() / tileWidth);
		
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
		for (Entity entity: allEntities) {
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
		for (Entity ent: allEntities) {
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
		for (Entity ent: allEntities) {
			ent.setPosition(ent.position.x + (ent.velocity.x * delta), ent.position.y + (ent.velocity.y * delta));
		}
	}
	
	/**
	 * Add an entity to the list of entities.
	 * 
	 * @param ent The entity to add to the list.
	 */
	public void addEntity(Entity ent) {
		entities.add(ent);
		if (ent instanceof Usable) usableEntities.add((Usable) ent);
		updateEntityList();
	}
	
	/**
	 * Add a mob to the list of mobs.
	 * 
	 * @param mob The mob to add to the list.
	 */
	public void addMob(Mob mob) {
		mobs.add(mob);
		updateEntityList();
		mobDirector.refresh(mobs);
	}
	
	public void addAttractor(Rectangle bbox, String type) throws SlickException {		
		switch (type) {
		case "BloodMark":
			BloodMark bm = new BloodMark(level, bbox.getCenterX(), bbox.getCenterY() - 20);
			addEntity(bm);
			this.mobDirector.addAttractor(bm, 60, false);
			break;
		}
	}
	
	public void removeAttractor(Attractor att) {
		attractors.remove(att);
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
		for (Entity ent: entities) {
			if (ent instanceof Usable) {
				if (((Usable)ent).canBeUsed(rect)) {
					return (Usable)ent;
				}
			}
		}
		return null;
	}
	
	private void updateEntityList() {
		allEntities = new ArrayList<Entity>();
		allEntities.addAll(entities);
		allEntities.addAll(mobs);
		//allEntities.add(player);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, backgroundLayer);
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, collisionLayer);
		
		// Render all the attractors.
		for (Entity att: attractors) {
			att.render(container, g);
		}
		
		// Render all the entities.
		for (Entity ent: allEntities) {
			ent.render(container, g);
		}
		
		// Render boundingBoxes if this setting is turned on.
		if (drawBoundingBoxes) {
			for (Entity ent: allEntities) {
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
	
	/**
	 * Clears the garbage array.
	 * @param entity
	 */
	public void emptyGarbage() {
		for (Entity entity : garbage) {
			if (entities.contains(entity)) entities.remove(entity);
			if (mobs.contains(entity)) mobs.remove((Mob)entity);
			if (attractors.contains(entity)) attractors.remove(entity);
			if (usableEntities.contains(entity)) usableEntities.remove(entity);
			if (allEntities.contains(entity)) allEntities.remove(entity);
			entity = null;
		}
		garbage.clear();
	}
	
	/** GETTERS AND SETTERS **/
	
	public String getMapName() {
		return mapName;
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
	 * Sets the entity representing the player in the environment.
	 * 
	 * @param player The player.
	 */
	public void setPlayer(Player player) {
		this.player = player;
		updateEntityList();
	}
}
