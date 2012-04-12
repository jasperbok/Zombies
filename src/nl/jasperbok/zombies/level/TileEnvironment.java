package nl.jasperbok.zombies.level;

import java.util.ArrayList;

import nl.jasperbok.slickhelp.geom.GeomHelper;
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
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class TileEnvironment {
	private boolean drawBoundingBoxes = false;
	private TiledMap map;
	private String mapName;
	private int tileWidth;
	private int tileHeight;
	private Level level;
	
	private Vector2f gravity;
	
	private ArrayList<Entity> entities;
	private ArrayList<Usable> usableEntities;
	private ArrayList<Mob> mobs;
	private Tile[][] tiles;
	private ArrayList<Entity> attractors;
	/**
	 * Contains all the entities in the environment. This variable is made
	 * so we only have to loop over one ArrayList instead of several.
	 */
	private ArrayList<Entity> allEntities;
	private Player player;
	public MobDirector mobDirector;
	
	/**
	 * Class constructor.
	 * 
	 * @param mapName The name of the map to load (without .tmx).
	 * @throws SlickException
	 */
	public TileEnvironment(String mapName, Vector2f gravity, Level level) throws SlickException {
		this.map = new TiledMap("data/maps/" + mapName + ".tmx");
		this.mapName = mapName;
		this.tileWidth = map.getTileWidth();
		this.tileHeight = map.getTileHeight();
		this.gravity = gravity;
		this.level = level;
		
		CollisionHelper.setTileWidth(tileWidth);
		CollisionHelper.setTileHeight(tileHeight);
		
		// Load them tiles.
		MapLoader loader = new MapLoader();
		this.tiles = loader.loadTiles(map);
		loader = null;
		
		this.entities = new ArrayList<Entity>();
		this.usableEntities = new ArrayList<Usable>();
		this.mobs = new ArrayList<Mob>();
		this.allEntities = new ArrayList<Entity>();
		this.attractors = new ArrayList<Entity>();
		this.mobDirector = new MobDirector(mobs);
		
		/*for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (tiles[x][y].isBlocking) {
					System.out.println("Blocker!!!");
				}
			}
		}*/
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		mobDirector.moveMobs();
		updateEntities(container.getInput(), delta);
		moveEntities(delta);
		checkForTileCollisions();
		//checkForCollisions();
	}
	
	private void updateEntities(Input input, int delta) {
		for (Entity ent: allEntities) {
			if (ent.gravityAffected) ent.velocity.add(gravity);
			ent.update(input, delta);
			ent.updateRenderPosition();
		}
		
		// Render all the attractors.
		for (Entity att: attractors) {
			att.update(input, delta);
		}
	}
	
	private void checkForCollisions() {
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
	}
	
	/**
	 * Checks whether an entity is on something solid with his feet.
	 * 
	 * @param ent The entity to check.
	 * @return boolean True if the entity is on top of something solid.
	 */
	public boolean isOnGround(Entity ent, boolean resolveCollision) {
		int relativeLeftX = (int)Math.floor((ent.boundingBox.getCenterX() - 10) / tileWidth);
		int relativeRightX = (int)Math.floor((ent.boundingBox.getCenterX() + 10) / tileWidth);
		int relativeBottomY = (int)Math.floor(ent.boundingBox.getMaxY() / tileWidth);
		return tiles[relativeLeftX][relativeBottomY].isBlocking || tiles[relativeRightX][relativeBottomY].isBlocking;
	}
	
	/**
	 * Checks whether an entity is on something climable.
	 * 
	 * To decide whether the entity is a climable tile, the point at the
	 * entities center X and bottom Y is checked for a collision with a
	 * climable tile.
	 * 
	 * @param ent The entity to check.
	 * @return boolean True if the entity's feet are on something climable.
	 */
	public boolean isOnClimableSurface(Entity ent) {
		int relativeX = (int)Math.floor(ent.boundingBox.getCenterX() / tileWidth);
		int relativeY = (int)Math.floor(ent.boundingBox.getMaxY() / tileWidth);
		try {
			return tiles[relativeX][relativeY].isClimable;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("isOnClimableSurface() is invoked on an entity that's outside the level bounds.");
		}
		return false;
	}
	
	private void checkForTileCollisions() {
		for (Entity ent: allEntities) {
			try {
				//CollisionHelper.entityVsTiles(ent, tiles);
				// Floor collisions.
				int relativeBottomX = (int)Math.floor(ent.boundingBox.getCenterX() / tileWidth);
				int relativeBottomY = (int)Math.floor(ent.boundingBox.getMaxY() / tileHeight);
				if (tiles[relativeBottomX][relativeBottomY].isBlocking) {
					ent.setPosition(ent.position.getX(), + tiles[relativeBottomX][relativeBottomY].position.getY() - ent.boundingBox.getHeight() + 1);
					// The entity is standing on something solid, so change his y velocity to 0 or less.
					if (ent.velocity.getY() > 0) ent.velocity.set(ent.velocity.getX(), 0);
				}
				// Left side collisions.
				int relLeftX = (int)Math.floor(ent.boundingBox.getMinX() / tileWidth);
				int relTopLeftY = (int)(Math.floor((ent.boundingBox.getMinY() + 10) / tileHeight));
				int relBottomLeftY = (int)(Math.floor((ent.boundingBox.getMaxY() - 10) / tileHeight));
				if (tiles[relLeftX][relBottomLeftY].isBlocking) {
					System.out.println("Collision on a side");
					ent.setPosition(tiles[relLeftX][relBottomLeftY].position.getX() + tiles[relLeftX][relBottomLeftY].width, ent.position.getY());
				} else if (tiles[relLeftX][relTopLeftY].isBlocking) {
					System.out.println("Collision on a side");
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
			attractors.add(new BloodMark(level, bbox.getCenterX(), bbox.getCenterY() - 20));
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
