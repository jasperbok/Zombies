package nl.jasperbok.zombies.entity;

import java.util.HashMap;

import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;

public class Trigger {
	public Vector2f pos = new Vector2f(0, 0);
	public Vector2f size = new Vector2f(0, 0);
	public String name = "";
	public HashMap<String, String> settings = new HashMap<String, String>();
	
	public boolean active = true;
	public boolean repeat = false;
	public int activationDelay = 0;
	public int inactiveTime = 0;
	
	public Level level = null;
	
	public Trigger(Level level, Boolean repeat, Vector2f pos, Vector2f size) {
		this.level = level;
		this.pos = pos;
		this.size = size;
		this.repeat = repeat;
	}
	
	public void update(GameContainer container, int delta) {
		if (this.active) {
			Entity player = this.level.env.getEntityByName("player");
			if (this.touches(player)) {
				this.activate();
			}
		}
		
		if (this.repeat && !this.active) {
			this.inactiveTime += delta;
			if (this.inactiveTime > this.activationDelay) {
				this.active = true;
				this.inactiveTime = 0;
			}
		}
	}
	
	public void activate() {
		if (this.settings.get("sfx") != "") {
			//this.level.env.sounds.play(this.sfx);
		}
		
		if (this.settings.get("target") != "") {
			this.level.env.getEntityByName(this.settings.get("target")).call("");
		}
		
		this.active = false;
	}
	
	public boolean touches(Entity other) {
		return !(
				this.pos.x >= other.position.x + other.boundingBox.getWidth() ||
				this.pos.x + this.size.x <= other.position.x ||
				this.pos.y >= other.position.y + other.boundingBox.getHeight() ||
				this.pos.y + this.size.y <= other.position.y
				);
	}
}
