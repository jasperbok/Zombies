package nl.jasperbok.zombies.level.environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A loader for a very simple tile based map text format that maps from
 * characters in a text file to tile definitions in an XML file.
 * 
 * @author kevin
 */
public class MapLoader {
	/** The definition of each tile mapped to a single character */
	private TiledMap map;
	
	/**
	 * Create a new loader using a map file.
	 * 
	 * @param mapName The name of the map to load (without the .tmx extension).
	 */
	public MapLoader(String mapName) throws SlickException {
		this.map = new TiledMap("data/maps/" + mapName + ".tmx");
	}
	
	/**
	 * Returns a TileEnvironment build from the internal map.
	 * 
	 * @return The configured enviornment thats been populated with tiles
	 */
	public TileEnvironment load() throws SlickException {
		int width = map.getWidth();
		int height = map.getHeight();
		int tileWidth = map.getTileWidth();
		int tileHeight = map.getTileHeight();
		TileEnvironment env = new TileEnvironment(width, height);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int tileID = map.getTileId(x, y, 0);
				if (tileID != 0) {
					Rectangle rect = new Rectangle(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
					Tile tile = new Tile(map.getTileImage(x, y, 0), rect);
					env.setTile(x, y, tile);
				}
			}
		}
		
		return env;
	}
}