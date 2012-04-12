package nl.jasperbok.zombies.level;

import nl.jasperbok.zombies.entity.Entity;

public class CollisionHelper {
	public static int tileWidth;
	public static int tileHeight;
	
	public static void setTileWidth(int width) {
		tileWidth = width = 0;
	}
	
	public static void setTileHeight(int height) {
		tileHeight = height = 0;
	}
	
	public static void entityVsTiles(Entity ent, Tile[][] tiles) {
		if (tileWidth == 0 || tileHeight == 0) {
			System.out.println("CollisionHelper needs a tileWidth and tileHeight before it functions");
			return;
		}
		// Floor collisions.
		int relativeBottomX = (int)Math.floor(ent.boundingBox.getCenterX() / tileWidth);
		int relativeBottomY = (int)Math.floor(ent.boundingBox.getMaxY() / tileHeight);
		if (tiles[relativeBottomX][relativeBottomY].isBlocking) {
			ent.setPosition(ent.position.getX(), + tiles[relativeBottomX][relativeBottomY].position.getY() - ent.boundingBox.getHeight() + 1);
			// The entity is standing on something solid, so change his y velocity to 0 or less.
			if (ent.velocity.getY() > 0) ent.velocity.set(ent.velocity.getX(), 0);
		}
		// Left side collisions.
		int relLeftX = (int)Math.floor(ent.boundingBox.getMinX() / tileWidth);
		int relTopLeftY = (int)(Math.floor((ent.boundingBox.getMinY() + 10) / tileHeight));
		int relBottomLeftY = (int)(Math.floor((ent.boundingBox.getMaxY() - 10) / tileHeight));
		if (tiles[relLeftX][relBottomLeftY].isBlocking) {
			System.out.println("Collision on a side");
			ent.setPosition(tiles[relLeftX][relBottomLeftY].position.getX() + tiles[relLeftX][relBottomLeftY].width, ent.position.getY());
		} else if (tiles[relLeftX][relTopLeftY].isBlocking) {
			System.out.println("Collision on a side");
			ent.setPosition(tiles[relLeftX][relTopLeftY].position.getX() + tiles[relLeftX][relTopLeftY].width, ent.position.getY());
		}
		// Right side collisions.
		int relRightX = (int)Math.floor(ent.boundingBox.getMaxX() / tileWidth);
		int relTopRightY = (int)(Math.floor((ent.boundingBox.getMinY() + 10) / tileHeight));
		int relBottomRightY = (int)(Math.floor((ent.boundingBox.getMaxY() - 10) / tileHeight));
		if (tiles[relRightX][relBottomRightY].isBlocking) {
			ent.setPosition(tiles[relRightX][relBottomRightY].position.getX() - ent.boundingBox.getWidth(), ent.position.getY());
		} else if (tiles[relRightX][relTopRightY].isBlocking) {
			ent.setPosition(tiles[relRightX][relTopRightY].position.getX() - ent.boundingBox.getWidth(), ent.position.getY());
		}
	}
}
