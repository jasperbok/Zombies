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
		//checkForCollisions();
		moveEntities(delta);
		newCheckForTileCollisions();
		newCheckForCollisions();
	}
	
	private void updateEntities(Input input, int delta) {
		for (Entity ent: allEntities) {
			ent.update(input, delta);
			ent.velocity.add(gravity);
		}
	}
	
	private void newCheckForCollisions() {
		
	}
	
	private void newCheckForTileCollisions() {
		for (Entity ent: allEntities) {
			try {
				// Floor collisions.
				int relativeBottomX = (int)Math.floor(ent.boundingBox.getCenterX() / 32);
				int relativeBottomY = (int)Math.floor(ent.boundingBox.getMaxY() / 32);
				if (tiles[relativeBottomX][relativeBottomY].isBlocking) {
					ent.setPosition(ent.position.getX(), + tiles[relativeBottomX][relativeBottomY].position.getY() - ent.boundingBox.getHeight() + 1);
				}
				// Left side collisions.
				int relLeftX = (int)Math.floor(ent.boundingBox.getMinX() / 32);
				int relTopLeftY = (int)Math.floor(ent.boundingBox.getMinY() / 32);
				int relBottomLeftY = (int)Math.floor(ent.boundingBox.getMaxX());
				if (tiles[relLeftX][relBottomLeftY].isBlocking) {
					ent.setPosition(tiles[relLeftX][relBottomLeftY].position.getX() + tiles[relLeftX][relBottomLeftY].width, ent.position.getY());
				} else if (tiles[relLeftX][relTopLeftY].isBlocking) {
					ent.setPosition(tiles[relLeftX][relTopLeftY].position.getX() + tiles[relLeftX][relTopLeftY].width, ent.position.getY());
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				
			}
		}
	}
	
	private void checkForCollisions() {
		ArrayList<Entity> collides = new ArrayList<Entity>();
		
		for (int i = 0; i < allEntities.size(); i++) {
			Entity first = allEntities.get(i);
			for (int j = i + 1; j < allEntities.size(); j++) {
				Entity second = allEntities.get(j);
				checkForTileCollisions(first);
				// If there's a collision between them, move A back its velocity - overlap.
				// If a's velocity == 0, move B
				// Or move them both half the overlap... Dunno yet...
			}
		}
	}
	
	/**
	 * Check whether an entity will collide with any tiles if it moves.
	 * @param ent
	 */
	private void checkForTileCollisions(Entity ent) {
		Rectangle newPos = new Rectangle(
				ent.boundingBox.getMinX() + ent.velocity.getX(),
				ent.boundingBox.getMinY() + ent.velocity.getY(),
				ent.boundingBox.getWidth(),
				ent.boundingBox.getHeight());
		
		ArrayList<Tile> touchingTiles = getTouchingTiles(GeomHelper.combineToRectangle(ent.boundingBox, newPos));
		
		Tile closestTile = null;
		float closestDistance = 1000f;
		// Check what Tile is the closest.
		for (Tile blocker: touchingTiles) {
			if (blocker.isBlocking) {
				Vector2f distance = calcDistance(ent, blocker);
				System.out.println("Current closest: " + closestDistance + ", X: " + distance.getX() + ", Y: " + distance.getY());
				if (closestDistance > distance.getX() || closestDistance > distance.getY()) {
					closestDistance = Math.max(distance.getX(), distance.getY());
					closestTile = blocker;
				}
			}
		}
		
		if (closestTile != null) {
			// If the velocity vector is longer than the distance, shorten it. to match the distance.
			if (ent.velocity.getLength() > closestDistance) {
				//ent.velocity.scale(ent.velocity.getLength() / closestDistance);
				//ent.velocity.scale(0.5f);
				System.out.println("CLOSEST TILEEEEEEEEEEEEEEEEE");
			}
		}
	}
	
	/**
	 * Finds all the tiles that are touching a certain rectangle.
	 * 
	 * @param rect The rectangle the tiles have to overlap.
	 * @return ArrayList<Tile> A list containing all the found tiles.
	 */
	private ArrayList<Tile> getTouchingTiles(Rectangle rect) {
		ArrayList<Tile> foundTiles = new ArrayList<Tile>();
		
		// Get start and end positions in map dimensions.
		int startX = (int)Math.floor(rect.getMinX() / 32);
		int endX = (int)Math.floor(rect.getMaxX() / 32);
		int startY = (int)Math.floor(rect.getMinY() / 32);
		int endY = (int)Math.floor(rect.getMaxY() / 32);
		
		try {
			for (int x = startX; x <= endX; x++) {
				for (int y = startY; y <= endY; y++) {
					foundTiles.add(tiles[x][y]);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			//System.out.println("Illegal Array index used at getTouchingTiles() inside TileEnvironment.java");
		}
		
		return foundTiles;
	}
	
	/**
	 * Calculates the distance between the bounding boxes of two entities.
	 * 
	 * Returns the largest distance between the two entities. Either on the
	 * x axis or the y axis.
	 * 
	 * @param ent1 The first entity.
	 * @param ent2 The second entity.
	 */
	private Vector2f calcDistance(Entity ent1, Entity ent2) {
		Rectangle box1 = new Rectangle(ent1.boundingBox.getMinX(),
				ent1.boundingBox.getMinY(),
				ent1.boundingBox.getWidth(),
				ent1.boundingBox.getHeight());
		Rectangle box2 = new Rectangle(ent1.boundingBox.getMinX(),
				ent1.boundingBox.getMinY(),
				ent1.boundingBox.getWidth(),
				ent1.boundingBox.getHeight());
		box1.grow(box2.getWidth() / 2, box2.getHeight() / 2);
		
		float minXDistance = 1000f;
		float minYDistance = 1000f;
		// Get the smallest distances between the two objects.
		minXDistance = Math.min(Math.abs(box1.getMinX() - box2.getCenterX()), minXDistance);
		minXDistance = Math.min(Math.abs(box1.getMaxX() - box2.getCenterX()), minXDistance);
		minYDistance = Math.min(Math.abs(box1.getMinY() - box2.getCenterY()), minYDistance);
		minYDistance = Math.min(Math.abs(box1.getMaxY() - box2.getCenterY()), minYDistance);
		
		if (minXDistance > minYDistance) {
			return new Vector2f(minXDistance, 0);
		} else {
			return new Vector2f(0, minYDistance);
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
