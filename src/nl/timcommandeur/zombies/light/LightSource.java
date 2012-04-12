package nl.timcommandeur.zombies.light;

import java.util.Random;

import nl.timcommandeur.zombies.screen.Camera;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import LightTest.Light;
import LightTest.Vec2;

public class LightSource extends Light {
	public float intensity;
	public float intensityFloor = 0;
	public float intensityCeil = 0;
	public Vector2f position;
	
	private int screenHeight = 720;
	private Camera camera;
	
	public LightSource(Vector2f position, float radius, float depth, Color color) {
		super(new Vec2(position.x, position.y), radius, depth, color);
		setPosition(position.x, position.y);
		this.camera = null;
	}
	
	public LightSource(Vector2f position, float radius, float depth, Color color, Camera camera) {
		super(new Vec2(position.x, position.y), radius, depth, color);
		setPosition(position.x, position.y);
		this.camera = camera;
	}
	
	public void render() {
		updateRenderPosition();
		if (!(intensityFloor == 0 && intensityCeil == 0))
			flicker();
		super.render(intensity);
	}
	
	public void setPosition(float x, float y) {
		setPosition(new Vector2f(x, y));
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	public void updateRenderPosition() {
		if (camera == null) {
			setPos(new Vec2(position.x, position.y));
		} else {
			setPos(new Vec2(position.x - camera.position.x, position.y - camera.position.y));
		}
	}
	
	public void setPos(Vec2 pos) {
		super.setPos(new Vec2(pos.x, (pos.y * -1) + screenHeight));
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
