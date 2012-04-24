package nl.jasperbok.zombies;

import java.util.HashMap;

import nl.jasperbok.zombies.level.Tile;

import org.newdawn.slick.geom.Vector2f;

public class Resolve {
	
	public HashMap<String, Boolean> collision = new HashMap<String, Boolean>();
	public Vector2f pos = new Vector2f(0, 0);
	public HashMap<String, Tile> tile = new HashMap<String, Tile>();
	
	public Resolve() {
		this.collision.put("x", false);
		this.collision.put("y", false);
		this.tile.put("y", null);
		this.tile.put("x", null);
	}
}
