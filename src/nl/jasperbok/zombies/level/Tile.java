package nl.jasperbok.zombies.level;

import nl.jasperbok.zombies.entity.Entity;

public class Tile extends Entity {
	public int width;
	public int height;
	
	public Tile(int width, int height, int xPos, int yPos, boolean blocking) {
		this.width = width;
		this.height = height;
		this.isBlocking = blocking;
		this.isMovable = false;
		this.setPosition(xPos, yPos);
	}
}
