package nl.jasperbok.zombies.level;

import nl.jasperbok.zombies.entity.Entity;

public class Tile extends Entity {
	public int id;
	public int width;
	public int height;
	public int relativeX;
	public int relativeY;
	public boolean isClimable;
	
	public Tile(int id, int width, int height, int xPos, int yPos, boolean blocking, boolean climable) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.relativeX = xPos;
		this.relativeY = yPos;
		this.isBlocking = blocking;
		this.isMovable = false;
		this.isClimable = climable;
		this.setPosition(xPos, yPos);
	}
}
