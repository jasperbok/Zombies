package nl.jasperbok.zombies.level;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Box;
import nl.jasperbok.zombies.entity.Entity;

public class Block extends Entity {
	public int tileID;
	
	public boolean isClimable = false;
	
	public Block(int x, int y, int tileID, int tileSize, TiledMap map) throws SlickException {
		position.x = x * tileSize;
		position.y = y * tileSize;
		this.tileID = tileID;
		boundingBox = new Rectangle(position.x, position.y, tileSize, tileSize);
		
		if ("false".equals(map.getTileProperty(tileID, "blocked", "true"))) {
			isBlocking = false;
		}
		if ("true".equals(map.getTileProperty(tileID, "climable", "false"))) {
			isClimable = true;
		}
	}
}
