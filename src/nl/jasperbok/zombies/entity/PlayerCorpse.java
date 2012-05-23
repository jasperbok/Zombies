package nl.jasperbok.zombies.entity;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.level.Level;

public class PlayerCorpse extends Entity {

	public boolean flip = false;
	public int timer = 0;
	public int dieTime = 5000;
	
	public PlayerCorpse (Level level, int x, int y) throws SlickException {
		super.init(level);
		this.position.x  = x;
		this.position.y = y;
		
		this.animSheet = new SpriteSheet("data/sprites/entity/fall_sit.png", 75, 150);
		this.addAnim("die", 250, new int[]{0, 1}, false);
		this.currentAnim = this.anims.get("die");
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);
		this.timer += delta;
		
		if (this.timer > this.dieTime) {
			this.level.reInit();
		}
	}
}
