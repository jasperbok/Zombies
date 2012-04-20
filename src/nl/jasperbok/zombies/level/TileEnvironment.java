package nl.jasperbok.zombies.level;

import java.util.ArrayList;

import nl.jasperbok.zombies.entity.Attractor;
import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.mob.Mob;
import nl.jasperbok.zombies.entity.object.BloodMark;
import nl.jasperbok.zombies.entity.mob.MobDirector;
import nl.timcommandeur.zombies.screen.Camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
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
	
	// Entity variables.
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
		// Load the tiles.
		MapLoader loader = new MapLoader();
		this.tiles = loader.loadTiles(map);
		loader = null;
		
		// Initialize the Entity ArrayLists.
		this.entities = new ArrayList<Entity>();
		this.usableEntities = new ArrayList<Usable>();
		this.mobs = new ArrayList<Mob>();
		this.allEntities = new ArrayList<Entity>();
		this.attractors = new ArrayList<Entity>();
		this.mobDirector = new MobDirector(mobs);
		this.garbage = new ArrayList<Entity>();
		
		// Neat loop to debug stuff in the map.
		/*for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (tiles[x][y].isBlocking) {
					System.out.println("Blocker!!!");
				}
			}
		}*/
	}
	
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
			if (checkingEntity != entity && checkingEntity.boundingBox.intersects(entity.boundingBox)) {
				colliding.add(entity);
			}
		}
		
		return colliding;
	}
	
	/*private void checkForCollisions() {
		for (int i = 0; i < allEntities.size(); i++) {
			for (int j = i + 1; j < allEntities.size(); j++) {
				if (allEntities.get(i).isBlocking || allEntities.get(j).isBlocking) {
					Rectangle bbox1 = allEntities.get(i).boundingBox;
					Rectangle bbox2 = allEntities.get(j).boundingBox;
					if (bbox1.intersects(bbox2)) {
						int horizontalOverlap = 0;
						int verticalOverlap = 0;
						boolean leftOverlap = bbox1.contains(bbox2.getMinX(), bbox2.getMinY()) || bbox1.contains(bbox2.getMinX(), bbox2.getMaxY());
						boolean rightOverlap = bbox1.contains(bbox2.getMaxX(), bbox2.getMinY()) || bbox1.contains(bbox2.getMaxX(), bbox2.getMaxY());
						
						if (leftOverlap && rightOverlap) {
							// Fuck this shit... That thing's inside her!
						} else if (leftOverlap) {
							// There's a left overlap.
							float overlap = Math.abs(bbox1.getMaxX() - bbox2.getMinX());
							if (allEntities.get(j).isMovable) {
								if (allEntities.get(i).isMovable) {
									// Both movable, move 'em both.
									allEntities.get(j).setPosition((float)(allEntities.get(j).position.getX() - overlap / 2), allEntities.get(j).position.getY());
									allEntities.get(i).setPosition((float)(allEntities.get(i).position.getX() + overlap / 2), allEntities.get(i).position.getY());
								} else {
									// Only j is movable.
									allEntities.get(j).setPosition((float)(allEntities.get(j).position.getX() - overlap), allEntities.get(j).position.getY());
								}
							} else {
								// Only i is movable.
								allEntities.get(i).setPosition((float)(allEntities.get(i).position.getX() + overlap), allEntities.get(i).position.getY());
							}
						} else if (rightOverlap) {
							// There's a right overlap.
						}
					}
				}
			}
		}
	}*/
	
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
		int entLeftX = (int)(ent.boundingBox.getCenterX() - 10);
		int entRightX = (int)(ent.boundingBox.getCenterX() + 10);
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
		Attractor attractor = null;
		
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
		return null;
	}
	
	private void updateEntityList() {
		allEntities = new ArrayList<Entity>();
		allEntities.addAll(entities);
		allEntities.addAll(mobs);
		allEntities.add(player);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		// Render the background and the environment.
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, 0);
		// Render the actual level where the entities collide with.
		map.render(0 - (int)Camera.getInstance().position.getX() + (int)Camera.center.x, 0 - (int)Camera.getInstance().position.getY() + (int)Camera.center.y, 1);
		
		// Render all the attractors.
		for (Entity att: attractors) {
			att.render(container, g);
		}
		
		// Render all the entities.
		for (Entity ent: allEntities) {
			ent.render(container, g);
		}
		
		// Render the foreground.
		//map.render(0,  0, 3);
		
		// Render boundingBoxes if this setting is turned on.
		if (drawBoundingBoxes) {
			for (Entity ent: allEntities) {
				g.draw(ent.boundingBox);
			}
		}
	}
	
	/**
	 * Removes the given entity.
	 * 
	 * @param entity
	 */
	public void remove(Entity entity) {
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
		return player;
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
