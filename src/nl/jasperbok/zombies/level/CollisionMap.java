package nl.jasperbok.zombies.level;

import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import nl.jasperbok.zombies.Resolve;

public class CollisionMap extends Map {
	
	public int tileSize;

	public CollisionMap(TiledMap map) throws SlickException {
		super(map);
	}
	
	public Resolve trace(float x, float y, float vx, float vy, int width, int height) {
		// Set up the trace result.
		Resolve res = new Resolve();
		res.collision.put("x", false);
		res.collision.put("y", false);
		res.collision.put("slope", false);
		res.pos.x = x;
		res.pos.y = y;
		res.tile.x = 0f;
		res.tile.x = 0f;
		
		// Break the trace down into smaller steps if necessary.
		int steps = (int)Math.ceil(Math.max(Math.abs(vx), Math.abs(vy)) / this.tileSize);
		if (steps > 1) {
			float sx = vx / steps;
			float sy = vy / steps;
			
			for (int i = 0; i < steps && (sx > 0 || sy > 0); i++) {
				this.traceStep(res, x, y, sx, sy, width, height, vx, vy, i);
				
				x = res.pos.x;
				y = res.pos.y;
				if (res.collision.get("x")) {
					sx = 0;
					vx = 0;
				}
				if (res.collision.get("y")) {
					sy = 0;
					vy = 0;
				}
				if (res.collision.get("slope")) break;
			}
		}
		
		// Just one step.
		else {
			this.traceStep(res, x, y, vx, vy, width, height, vx, vy, 0);
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param res
	 * @param x The x position.
	 * @param y The y position.
	 * @param vx The x velocity.
	 * @param vy The y velocity.
	 * @param width
	 * @param height
	 * @param rvx
	 * @param rvy
	 * @param step
	 */
	private void traceStep(Resolve res, float x, float y, float vx, float vy, int width, int height, float rvx, float rvy, int step) {
		res.pos.x += vx;
		res.pos.y += vy;
		
		int t = 0;
		
		// Horizontal collision (walls).
		if (vx != 0) {
			float pxOffsetX = vx > 0 ? width : 0;
			float tileOffsetX = vx < 0 ? this.tileSize : 0;
			
			int firstTileY = (int)Math.max(Math.floor(y / this.tileSize), 0);
			int lastTileY = (int)Math.min(Math.ceil((y + height) / this.tileSize), this.height);
			int tileX = (int)Math.floor((res.pos.x + pxOffsetX) / this.tileSize);
			
			// We need to test the new tile position as well as the current one, as we
			// could still collide with the current tile if it's a line def.
			// We can skip this test if this is not the first step or the new tile position
			// is the same as the current one.
			int prevTileX = (int)Math.floor((x + pxOffsetX) / this.tileSize);
			if (step > 0 || tileX == prevTileX || prevTileX < 0 || prevTileX >= this.width) {
				prevTileX -= 1;
			}
			
			// Still inside this collision map?
			if (tileX >= 0 && tileX < this.width) {
				for (int tileY = firstTileY; tileY < lastTileY; tileY++) {
					if (prevTileX != -1) {
						Tile t = this.data[tileY][prevTileX];
						if (t > 1 && this.checkTileDef(res, t, x, y, rvx, rvy, width, height, prevTileX, tileY)) {
							break;
						}
					}
					
					Tile t = this.data[tileY][tileX];
					if (t == 1 || // fully solid tile?
							(t > 1 && this.checkTileDef(res, t, x, y, rvx, rvy, width, height, tileX, tileY)) // slope?
							) {
						if (t > 1 && res.collision.get("slope")) {
							break;
						}
						
						// full tile collision!
						res.collision.put("x", true);
						res.tile.x = t;
						res.pos.x = tileX * this.tileSize - pxOffsetX + tileOffsetX;
						break;
					}
				}
			}
		}
		
		// Vertical collision (floor, ceiling).
		if (vy != 0) {
			int pxOffsetY = (vy > 0 ? height : 0);
			int tileOffsetY = (vy < 0 ? this.tileSize : 0);
			
			int firstTileX = (int)Math.max(Math.floor(res.pos.x / this.tileSize), 0);
			int lastTileX = (int)Math.min(Math.ceil((res.pos.x + width) / this.tileSize), this.width);
			int tileY = (int)Math.floor((res.pos.y + pxOffsetY) / this.tileSize);
			int prevTileY = (int)Math.floor((y + pxOffsetY) / this.tileSize);
			
			if (step > 0 || tileY == prevTileY || prevTileY < 0 || prevTileY >= this.height) {
				prevTileY = -1;
			}
			
			// Stil inside the collision map?
			if (tileY >= 0 && tileY < this.height) {
				for (int tileX = firstTileX; tileX < lastTileX; tileX++) {
					if (prevTileY != -1) {
						t = this.data[prevTileY][tileX];
						if (t > 1 && this.checkTileDef(res, t, x, y, rvx, rvy, width, height, tileX, prevTileY)) {
							break;
						}
					}
					
					t = this.data[tileY][tileX];
					if (t == 1 || // fully solid tile?
							(t > 1 && this.checkTileDef(res, t, x, y, rvx, rvy, width, height, tileX, tileY))){
						if (t > 1 && res.collision.get("slope")) {
							break;
						}
						
						// Full tile collision!
						res.collision.put("y", true);
						res.tile.y = t;
						res.pos.y = tileY * this.tileSize - pxOffsetY + tileOffsetY;
						break;
					}
				}
			}
		}
		
		// res is changed in place, nothing to return.
	}
	
	private void checkTileDef(Resolve res, int t, float x, float y, int width, int height, int tileX, int tileY) {
		
	}
}
