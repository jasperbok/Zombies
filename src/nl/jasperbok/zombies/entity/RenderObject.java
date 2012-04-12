package nl.jasperbok.zombies.entity;

import nl.jasperbok.zombies.math.Vector2;
import nl.timcommandeur.zombies.screen.Camera;

import org.newdawn.slick.geom.Vector2f;

public class RenderObject {
	public Vector2f position;
	public Vector2f renderPosition;
	public Camera camera = null;
	public boolean staticPosition = false;
	
	public RenderObject() {
		position = new Vector2f(0.0f, 0.0f);
		renderPosition = new Vector2f(0.0f, 0.0f);
	}
	
	public void updateRenderPosition() {
		if (camera == null) {
			camera = Camera.getInstance();
		}
		
		if (!staticPosition) {
			renderPosition.x = (position.x - camera.position.x) + Camera.center.x;
			renderPosition.y = (position.y - camera.position.y) + Camera.center.y;
		}
	}
}
