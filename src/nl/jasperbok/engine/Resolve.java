package nl.jasperbok.engine;

import java.util.HashMap;

import org.newdawn.slick.geom.Vector2f;

public class Resolve {
	
	public HashMap<String, Boolean> collision = new HashMap<String, Boolean>();
	public Vector2f pos = new Vector2f(0, 0);
	public HashMap<String, Integer> tile = new HashMap<String, Integer>();
	
	public Resolve() {
		this.collision.put("x", false);
		this.collision.put("y", false);
		this.tile.put("y", 0);
		this.tile.put("x", 0);
	}
	
	public void printInfo() {
		System.out.println("===Resolve===========================");
		System.out.println("Collision: x = " + this.collision.get("x").toString() + ", y = " + this.collision.get("y").toString());
		System.out.println("Position: x = " + this.pos.x + ", y = " + this.pos.y);
		System.out.println("Tile: x = " + this.tile.get("x") + ", y = " + this.tile.get("y"));
		System.out.println("=====================================");
	}
}
