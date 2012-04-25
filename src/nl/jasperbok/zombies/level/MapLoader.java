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
		Tile[][] tiles = new Tile[mapWidth][mapHeight];
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				int tileId = map.getTileId(x, y, 1);
				tiles[x][y] = new Tile(
					tileId,
					tileWidth,
					tileHeight,
					tileWidth * x,
					tileHeight * y,
					"true".equals(map.getTileProperty(tileId, "blocked", "false")),
					"true".equals(map.getTileProperty(tileId, "climable", "false"))
				);
			}
		}
		
		
		return tiles;
	}
}
