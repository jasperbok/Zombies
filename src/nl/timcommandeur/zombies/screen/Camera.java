package nl.timcommandeur.zombies.screen;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import LightTest.Vec2;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.math.Vector2;

/*shit pickle*/

public class Camera {
	public int width;
	public int height;
	public Vector2f position = new Vector2f(0.0f, 0.0f);
	public Entity target = null;
	public Vector2f displacement;
	
	public static Camera instance;
	
	public static Vector2f center = new Vector2f(620, 400);
	
	/**
	 * Sets the target.
	 * 
	 * @param target
	 */
	public void setTarget(Entity target) {
		setTarget(target, new Vector2f(0, 0));
	}
	
	/**
	 * Sets the target and moves the given displacement from it.
	 * 
	 * @param target
	 * @param displacementX
	 * @param displacementY
	 */
	public void setTarget(Entity target, float displacementX, float displacementY) {
		setTarget(target, new Vector2f(displacementX, displacementY));
	}
	
	/**
	 * Sets the target and moves the given displacement from it.
	 * 
	 * @param target
	 * @param displacement
	 */
	public void setTarget(Entity target, Vector2f displacement) {
		this.target = target;
		this.displacement = displacement;
	}
	
	/**
	 * Sets the position of the camera.
	 * 
	 * @param x
	 * @param y
	 */
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
			this.position.x = target.position.x + displacement.x;
			this.position.y = target.position.y + displacement.y;
		}
	}
	
	public static synchronized Camera getInstance() {
		if (instance == null)
			instance = new Camera();
		return instance;
	}
}
