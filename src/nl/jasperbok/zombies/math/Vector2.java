package nl.jasperbok.zombies.math;

public class Vector2 {
	public float x;
	public float y;
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getLength() {
		return (float)Math.sqrt(x * x + y * y);
	}
}
