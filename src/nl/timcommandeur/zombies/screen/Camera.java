package nl.timcommandeur.zombies.screen;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import LightTest.Vec2;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.math.Vector2;

/*shit pickle*/

public class Camera {
	public int width;
	public int height;
	public Vector2f position = new Vector2f(0.0f, 0.0f);
	public Entity target = null;
	
	public static Camera instance;
	
	public static Vector2f center = new Vector2f(620, 400);
	
	public void setTarget(Entity target) {
		this.target = target;
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void translate(Graphics g) {
		//g.scale(2, 2);
		g.translate(-Math.round(position.x), -Math.round(position.y));
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		if (target != null) {
			System.out.println("line");
			this.position.x = target.position.x;
			this.position.y = target.position.y;
		}
	}
	
	public static synchronized Camera getInstance() {
		if (instance == null)
			instance = new Camera();
		return instance;
	}
}
