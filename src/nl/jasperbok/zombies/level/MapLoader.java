package nl.jasperbok.zombies.level;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class MapLoader {

	public MapLoader() throws SlickException {
	}
	
	public static Tile[][] loadTiles(TiledMap map) throws SlickException {
		int mapWidth = map.getWidth();
		int mapHeight = map.getHeight();
		int tileWidth = map.getTileWidth();
		int tileHeight = map.getTileHeight();
		
		int backgroundLayer = map.getLayerIndex("background");
		int collisionLayer = map.getLayerIndex("collision");
		int entityLayer = map.getLayerIndex("entities");
		
		Tile[][] tiles = new Tile[mapWidth][mapHeight];
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				int tileId = map.getTileId(x, y, collisionLayer);
				tiles[x][y] = new Tile(
					tileId,
					tileWidth,
					tileHeight,
					tileWidth * x,
					tileHeight * y,
					"true".equals(map.getTileProperty(tileId, "blocked", "false")),
					"true".equals(map.getTileProperty(tileId, "climable", "false")),
					"true".equals(map.getTileProperty(tileId, "hideable", "false"))
				);
			}
		}
		
		return tiles;
	}
	
	public static void loadEntities(TiledMap map) throws SlickException {
		// The number of object layers.
		int numGroups = map.getObjectGroupCount();
		
		for (int i = 0; i < numGroups; i++) {
			// The number of objects in the current object layer.
			int numObjects = map.getObjectCount(i);
			
			for (int j = 0; j < numObjects; j++) {
				System.out.println("Found a " + map.getObjectName(i, j));
			}
		}
	}
}
