package nl.jasperbok.zombies.level;

import org.newdawn.slick.SlickException;

import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Box;

public class Block {
	public int x;
	public int y;
	public Body poly;
	public int tileID;
	
	public Block(int x, int y, int tileID) throws SlickException {
		this.x = x;
		this.y = y;
		this.tileID = tileID;
		poly = new StaticBody("" + tileID, new Box(32, 32));
		poly.setPosition(x,y);
	}
	
	public void update(int delta) {
		x = (int)poly.getPosition().getX() - 25;
		y = (int)poly.getPosition().getY() - 25;
	}
}
