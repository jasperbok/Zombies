package nl.jasperbok.engine;

public class Timer {
	
	private float target;
	private float ticks;

	public Timer(float seconds) {
		this.target = seconds * 1000;
	}
	
	public void update(int delta) {
		this.ticks += delta;
	}
	
	public void set(float seconds) {
		this.target = seconds * 1000;
		this.ticks = 0;
	}
	
	public float delta() {
		return (this.ticks - this.target) / 1000;
	}
}
