package nl.jasperbok.zombies.level;

import java.util.ArrayList;

import nl.jasperbok.slickhelp.geom.GeomHelper;
import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.mob.Mob;

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
	
	private Vector2f gravity;
	
	private ArrayList<Entity> entities;
	private ArrayList<Usable> usableEntities;
	private ArrayList<Mob> mobs;
	private Tile[][] tiles;
	/**
	 * Contains all the entities in the environment. This variable is made
	 * so we only have to loop over one ArrayList instead of several.
	 */
	private ArrayList<Entity> allEntities;
	private Player player;
	
	/**
	 * Class constructor.
	 * 
	 * @param mapName The name of the map to load (without .tmx).
	 * @throws SlickException
	 */
	public TileEnvironment(String mapName, Vector2f gravity) throws SlickException {
		this.map = new TiledMap("data/maps/" + mapName + ".tmx");
		this.mapName = mapName;
		this.tileWidth = map.getTileWidth();
		this.tileHeight = map.getTileHeight();
		this.gravity = gravity;
		
		// Load them tiles.
		MapLoader loader = new MapLoader();
		this.tiles = loader.loadTiles(map);
		loader = null;
		
		this.entities = new ArrayList<Entity>();
		this.usableEntities = new ArrayList<Usable>();
		this.mobs = new ArrayList<Mob>();
		this.allEntities = new ArrayList<Entity>();
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		updateEntities(container.getInput(), delta);
		moveEntities(delta);
		checkForTileCollisions();
		checkForCollisions();
	}
	
	private void updateEntities(Input input, int delta) {
		for (Entity ent: allEntities) {
			ent.velocity.add(gravity);
			ent.update(input, delta);
		}
	}
	
	private void checkForCollisions() {
		
	}
	
	private void checkForTileCollisions() {
		for (Entity ent: allEntities) {
			try {
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
				int relTopLeftY = (int)(Math.floor(ent.boundingBox.getMinY() / tileHeight) + 1);
				int relBottomLeftY = (int)(Math.floor(ent.boundingBox.getMaxY() / tileHeight) - 1);
				if (tiles[relLeftX][relBottomLeftY].isBlocking) {
					ent.setPosition(tiles[relLeftX][relBottomLeftY].position.getX() + tiles[relLeftX][relBottomLeftY].width, ent.position.getY());
				} else if (tiles[relLeftX][relTopLeftY].isBlocking) {
					ent.setPosition(tiles[relLeftX][relTopLeftY].position.getX() + tiles[relLeftX][relTopLeftY].width, ent.position.getY());
				}
				// Right side collisions.
				int relRightX = (int)Math.floor(ent.boundingBox.getMaxX() / tileWidth);
				int relTopRightY = (int)(Math.floor(ent.boundingBox.getMinY() / tileHeight) + 1);
				int relBottomRightY = (int)(Math.floor(ent.boundingBox.getMaxY() / tileHeight) - 1);
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
			ent.setPosition(ent.position.x + ent.velocity.x * delta, ent.position.y + ent.velocity.y * delta);
		}
		//player.setPosition(player.position.x + player.velocity.x * delta, player.position.y + player.velocity.y * delta);
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
	
	public boolean canClimbHere(Rectangle bbox) {
		/*int centerX = (int)Math.floor(bbox.getCenterX() / tileWidth);
		int topY = (int)Math.floor((bbox.getMinY() + 10) / tileHeight); // + 10 so part of the box must overlap.
		int bottomY = (int)Math.floor((bbox.getMaxY() - 10) / tileHeight); // - 10... same reason.
		
		return tiles[centerX][topY].isClimable || tiles[centerX][bottomY].isClimable;*/
		return false;
	}
	
	private void updateEntityList() {
		allEntities.addAll(entities);
		allEntities.addAll(mobs);
		allEntities.add(player);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		map.render(0, 0);
		for (Entity ent: allEntities) {
			ent.render(container, g);
		}
		
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
