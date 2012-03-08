package nl.timcommandeur.zombies.light;

import java.util.Random;

import org.newdawn.slick.Color;

import LightTest.Light;
import LightTest.Vec2;

public class LightSource extends Light
{
	public float intensity;
	public float intensityFloor = 0;
	public float intensityCeil = 0;
	
	private int screenHeight = 720;
	
	public LightSource(Vec2 pos, float radius, float depth, Color color) {
		super(pos, radius, depth, color);
		setPos(pos);
	}
	
	public void render() {
		if (!(intensityFloor == 0 && intensityCeil == 0))
			flicker();
		super.render(intensity);
	}
	
	public void setPos(Vec2 pos) {
		super.setPos(new Vec2(pos.x, (pos.y * -1) + screenHeight));
		return;
	}
	
	public void flicker() {
		Random r = new Random();
		intensity = r.nextFloat() * (intensityCeil - intensityFloor) + intensityFloor;
	}
	
	public void setFlicker(float floor, float ceil) {
		intensityFloor = floor;
		intensityCeil = ceil;
	}
}
