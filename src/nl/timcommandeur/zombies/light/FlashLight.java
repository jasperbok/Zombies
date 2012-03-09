package nl.timcommandeur.zombies.light;

import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.Color;

import LightTest.LoopingList;
import LightTest.Vec2;

public class FlashLight {
	
	public LightSource flashLightLight;
	public List<LightSource> lights;
	public List<ShadowHull> hulls;
	public List<ShadowHull> flashLightHulls;
	
	protected int height = 30;
	protected int width = 60;
	
	public Vec2 pos;
	
	public FlashLight(List<LightSource> lights, List<ShadowHull> hulls, Vec2 pos) {
		this.lights = lights;
		this.hulls = hulls;
		
		init();
		setPos(pos);
		rotate(0);
	}
	
	public void setPos(Vec2 newPos) {
		pos = newPos;
		flashLightLight.setPos(newPos);
		for (ShadowHull hull : flashLightHulls) {
			hull.setPos(newPos);
		}
		rotate(0);
	}
	
	public void setFlicker(float floor, float ceil) {
		flashLightLight.setFlicker(floor, ceil);
	}
	
	public void init() {
		createLights();
		createHulls();
	}
	
	public void rotate(int angle) {
		for (ShadowHull hull : flashLightHulls) {
			hull.rotate(angle, 10, 30);
		}
	}
	
	public void point(Vec2 to) {
		int angle = (int) (vecAngle(new Vec2(to.x - pos.x, to.y - pos.y)) / Math.PI * 180);
		this.rotate(angle);
	}
	
	public void createLights() {
		LightSource light = new LightSource(new Vec2(0, 0), 400, 0, new Color(170, 170, 170));
		lights.add(light);
		flashLightLight = light;
	}
	
	public void createHulls() {
		Vec2 points2[] = {new Vec2(0, height - 2), new Vec2(width + width * 4, 0 - height * 4), new Vec2(width + width * 4, -2 - height * 4), new Vec2(0, height)};
        ShadowHull hull2 = new ShadowHull(new Vec2(400, 400), Arrays.asList(points2), 0.1f, Color.black);
        
        Vec2 points3[] = {new Vec2(0, height + 2), new Vec2(width + width * 4, height * 2 + height * 4), new Vec2(width + width * 4, height * 2 + 2 + height * 4), new Vec2(0, height)};
        ShadowHull hull3 = new ShadowHull(new Vec2(400, 400), Arrays.asList(points3), 0.1f, Color.black);
        
        hulls.add(hull2);
        hulls.add(hull3);
        
        flashLightHulls = new LoopingList<ShadowHull>();
        flashLightHulls.add(hull2);
        flashLightHulls.add(hull3);
	}
	
	public float vecAngle(Vec2 v) {
        return (float) Math.atan2(v.y, v.x);
    }
}
