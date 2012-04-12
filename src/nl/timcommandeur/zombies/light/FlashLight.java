package nl.timcommandeur.zombies.light;

import java.util.Arrays;
import java.util.List;

import nl.jasperbok.zombies.entity.Entity;
import nl.timcommandeur.zombies.screen.Camera;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;

import LightTest.LoopingList;
import LightTest.Vec2;

public class FlashLight {
	
	public LightSource flashLightLight;
	public List<LightSource> lights;
	public List<ShadowHull> hulls;
	public List<ShadowHull> flashLightHulls;
	
	public Camera camera;
	
	protected int height = 30;
	protected int width = 60;
	protected int currentAngle;
	protected boolean on = true;
	
	public Vector2f position;
	
	public FlashLight(List<LightSource> lights, List<ShadowHull> hulls, Vector2f position) {
		this(lights, hulls, position, null);
	}
	
	public FlashLight(List<LightSource> lights, List<ShadowHull> hulls, Vector2f position, Camera camera) {
		this.lights = lights;
		this.hulls = hulls;
		this.camera = camera;
		
		init();
		setPosition(position);
		rotate(0);
	}
	
	/**
	 * Old constructor.
	 * 
	 * @param lights
	 * @param hulls
	 * @param pos
	 */
	public FlashLight(List<LightSource> lights, List<ShadowHull> hulls, Vec2 pos) {
		this.lights = lights;
		this.hulls = hulls;
		
		init();
		setPos(pos);
		rotate(0);
	}
	
	public void switchOnOff() {
		if (on) {
			turnOff();
		} else {
			turnOn();
		}
	}
	
	public void turnOn() {
		on = true;
		setColor(new Color(170, 170, 170));
	}
	
	public void turnOff() {
		on = false;
		setColor(new Color(0, 0, 0, 0));
	}
	
	public void setColor(Color c)
	{
		flashLightLight.setColor(c);
	}
	
	public void setPos(Vec2 v) {
		setPosition(v.x, v.y);
	}
	
	public void setPosition(float x, float y) {
		setPosition(new Vector2f(x, y));
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
		flashLightLight.setPosition(position);
		for (ShadowHull hull : flashLightHulls) {
			//hull.setPos(position);
		}
		rotate(currentAngle);
	}
	
	public void setFlicker(float floor, float ceil) {
		flashLightLight.setFlicker(floor, ceil);
	}
	
	public void init() {
		createLights(new Color(170, 170, 170));
		createHulls();
	}
	
	public void rotate(int angle) {
		currentAngle = angle;
		for (ShadowHull hull : flashLightHulls) {
			hull.rotate(angle, 10, 30);
		}
	}
	
	public void point(Vec2 to) {
		int angle = (int) (vecAngle(new Vec2(to.x - position.x, to.y - position.y)) / Math.PI * 180);
		this.rotate(angle);
	}
	
	public void pointToMouse(GameContainer container) {
		Vec2 to = new Vec2(container.getInput().getAbsoluteMouseX() - 600, container.getInput().getAbsoluteMouseY() - 600);
		int angle = (int) (vecAngle(new Vec2(to.x, to.y)) / Math.PI * 180);
		this.rotate(angle);
	}
	
	public void createLights(Color c) {
		LightSource light = new LightSource(new Vector2f(0, 0), 400, 0, c, camera);
		lights.add(light);
		flashLightLight = light;
	}
	
	public void createHulls() {
		// The drawing is orientated so that the flashlight will be pointing from left to right.
		//
		Vec2 centerLeft = new Vec2(-1, 30);
		Vec2 topRight = new Vec2(17, 33);
		Vec2 bottomRight = new Vec2(17, 27);
		
		Vec2 points2[] = {centerLeft, bottomRight, centerLeft};
		ShadowHull hull2 = new ShadowHull(new Vec2(400, 400), Arrays.asList(points2), 0.1f, Color.black);
		hulls.add(hull2);
		
		Vec2 points3[] = {centerLeft, topRight, centerLeft};
		ShadowHull hull3 = new ShadowHull(new Vec2(400, 400), Arrays.asList(points3), 0.1f, Color.black);
		hulls.add(hull3);
		
		Vec2 points4[] = {new Vec2(centerLeft.x, centerLeft.y + 1), new Vec2(centerLeft.x, centerLeft.y - 1)};
		ShadowHull hull4 = new ShadowHull(new Vec2(400, 400), Arrays.asList(points4), 0.1f, Color.black);
		hulls.add(hull4);
		
        flashLightHulls = new LoopingList<ShadowHull>();
        
        flashLightHulls.add(hull2);
        flashLightHulls.add(hull3);
        flashLightHulls.add(hull4);
	}
	
	public float vecAngle(Vec2 v) {
        return (float) Math.atan2(v.y, v.x);
    }
}
