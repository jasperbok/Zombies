package nl.jasperbok.zombies.level;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class Map {
	
	public int tileSize;
	public int width;
	public int height;
	public Tile[][] data;

	public Map(TiledMap map) throws SlickException {
		this.tileSize = map.getTileHeight();
		this.data = MapLoader.loadTiles(map);
	}
	
	public Tile getTile(int xOffset, int yOffset){
		if (
			(xOffset > 0 && xOffset < this.width) &&
			(yOffset > 0 && yOffset < this.height)
		) {
			return this.data[xOffset][yOffset];
		}
		return null;
	}
}
