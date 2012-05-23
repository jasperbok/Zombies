package nl.jasperbok.engine;

import org.newdawn.slick.tiled.TiledMap;

public class CollisionMap {
	
	public int width = 0;
	public int height = 0;
	
	private Resolve res = null;
	private int tileSize = 80;
	private int[][] data = null;

	public CollisionMap(TiledMap map) {
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.data = new int[this.width][this.height];
		
		int collisionLayer = map.getLayerIndex("collision");
		
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				int tileId = map.getTileId(x, y, collisionLayer);
				this.data[x][y] = ("true".equals(map.getTileProperty(tileId, "blocked", "false")) ? 1 : 0);
			}
		}
		
		/*
		for (int y = 0; y < this.height; y++) {
			System.out.print("\n");
			for (int x = 0; x < this.width; x++) {
				if (this.data[x][y] == 1) {
					System.out.print("=");
				} else {
					System.out.print(" ");
				}
			}
		}
		*/
	}
	
	public Resolve trace(int x, int y, float vx, float vy, int objectWidth, int objectHeight) {
		this.res = new Resolve();
		this.res.pos.x = x;
		this.res.pos.y = y;
		
		// Break the trace down into smaller steps if necessary.
		int steps = (int)Math.ceil(Math.max(Math.abs(vx), Math.abs(vy)));
		if (steps > 1) {
			float sx = vx / steps;
			float sy = vy / steps;
			
			for (int i = 0; i < steps && (sx != 0 || sy != 0); i++) {
				this.traceStep(x, y, sx, sy, objectWidth, objectHeight, vx, vy, i);
				
				x = (int)this.res.pos.x;
				y = (int)this.res.pos.y;
				if (this.res.collision.get("x") == true) {sx = 0; vx = 0;}
				if (this.res.collision.get("y") == true) {sy = 0; vy = 0;}
			}
		}
		
		// Just one step.
		else {
			this.traceStep(x, y, vx, vy, objectWidth, objectHeight, vx, vy, 0);
		}
		
		return this.res;
	}
	
	private void traceStep(int x, int y, float vx, float vy, int width, int height, float rvx, float rvy, int step) {
		this.res.pos.x += vx;
		this.res.pos.y += vy;
		
		int t = 0;
		
		// Horizontal collision.
		if (vx != 0) {
			int pxOffsetX = (vx > 0 ? width : 0);
			int tileOffsetX = (vx < 0 ? this.tileSize : 0);
			
			int firstTileY = (int) Math.max( Math.floor(y / this.tileSize), 0);
			int lastTileY = (int)Math.min(Math.ceil(y + height) / this.tileSize, this.height);
			int tileX = (int)Math.floor((this.res.pos.x + pxOffsetX) / this.tileSize);
			
			//System.out.println("TILEX ======= " + tileX);
			
			int prevTileX = (int)Math.floor((x + pxOffsetX) / this.tileSize);
			if (step > 0 || tileX == prevTileX || prevTileX < 0 || prevTileX >= this.width) {
				prevTileX = -1;
			}
			
			// Still inside the collision map?
			if (tileX >= 0 && tileX < this.width) {
				for (int tileY = firstTileY; tileY < lastTileY; tileY++) {
					if (prevTileX != -1) {
						t = this.data[prevTileX][tileY];
					}
					
					t = this.data[tileX][tileY];
					
					// Missing something here.
					
					// Collision!
					if (t == 1) {
						res.collision.put("x", true);
						res.tile.put("x", t);
						res.pos.x = tileX * this.tileSize - pxOffsetX + tileOffsetX;
						//System.out.println(tileX + " * " + this.tileSize + " - " + pxOffsetX + " + " + tileOffsetX);
						break;
					}
				}
			}
		}
		
		// Vertical collision.
		if (vy != 0) {
			int pxOffsetY = (vy > 0 ? height : 0);
			int tileOffsetY = (vy < 0 ? this.tileSize : 0);
			
			int firstTileX = (int)Math.max(Math.floor(this.res.pos.x / this.tileSize), 0);
			int lastTileX = (int)Math.min(Math.ceil((this.res.pos.x + width) / this.tileSize), this.width);
			int tileY = (int)Math.floor((this.res.pos.y + pxOffsetY) / this.tileSize);
			
			int prevTileY = (int)Math.floor((y + pxOffsetY) / this.tileSize);
			if (step > 0 || tileY == prevTileY || prevTileY < 0 || prevTileY >= this.height) {
				prevTileY = -1;
			}
			
			// Still inside the collision map?
			if (tileY >= 0 && tileY < this.height) {
				for (int tileX = firstTileX; tileX < lastTileX; tileX++) {
					if (prevTileY != -1) {
						t = this.data[tileX][prevTileY];
					}
					
					// Missing something here
					
					t = this.data[tileX][tileY];
					
					// Collision!
					if (t == 1) {
						this.res.collision.put("y", true);
						this.res.tile.put("y", t);
						this.res.pos.y = tileY * this.tileSize - pxOffsetY + tileOffsetY;
						break;
					}
				}
			}
		}
	}
}
