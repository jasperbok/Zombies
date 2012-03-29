package nl.jasperbok.zombies.math;

import org.newdawn.slick.geom.Vector2f;

public class Vector2 extends Vector2f {
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getLength() {
		return (float)Math.sqrt(x * x + y * y);
	}
}
