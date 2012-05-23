package nl.jasperbok.zombies.entity.building;

import nl.jasperbok.engine.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.LifeComponent;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class BreakableFloor extends Entity {
	
	public BreakableFloor(Level level) throws SlickException {
		super.init(level);
		
		this.animSheet = new SpriteSheet("data/sprites/entity/building/breakablefloor.png", 240, 80);
		this.addAnim("idle", 100, new int[]{0});
		this.currentAnim = this.anims.get("idle");
		
		this.components.add(new LifeComponent(this, 1));
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);
		
		Player player = level.env.getPlayer();
		if (this.touches(player)) {
		//if (player.position.x < this.position.x + this.size.x && player.position.x > this.position.x && Math.abs(this.position.y - player.position.y + player.size.y) < 6) {
			// break the platform.
			((LifeComponent)getComponent(Component.LIFE)).takeDamage(1);
		}
	}
}
