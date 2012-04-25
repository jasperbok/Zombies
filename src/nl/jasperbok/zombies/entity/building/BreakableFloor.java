package nl.jasperbok.zombies.entity.building;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.component.Component;
import nl.jasperbok.zombies.entity.component.LifeComponent;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class BreakableFloor extends Entity {
	public BreakableFloor(Level level) throws SlickException {
		this(level, 0, 0);
	}
	
	public BreakableFloor(Level level, float x, float y) throws SlickException {
		super.init(level);
		Animation breakableFloor = new Animation();
		breakableFloor.addFrame(new Image("data/sprites/entity/building/breakablefloor.png"), 5000);
		this.anims.put("breakableFloor", breakableFloor);
		this.currentAnim = this.anims.get("breakableFloor");
		this.boundingBox = new Rectangle(position.x, position.y, this.currentAnim.getWidth(), this.currentAnim.getHeight());
		this.isBlocking = true;
		
		this.components.add(new LifeComponent(this, 1));
		
		setPosition(x, y);
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);
		boundingBox.setBounds(position.x, position.y, this.currentAnim.getWidth(), this.currentAnim.getHeight());
		
		Player player = level.env.getPlayer();
		if (player.position.x <  boundingBox.getMaxX() && player.position.x > boundingBox.getMinX() && Math.abs(boundingBox.getMinY() - player.boundingBox.getMaxY()) < 6) {
			// break the platform.
			((LifeComponent)getComponent(Component.LIFE)).takeDamage(1);
		}
	}
}
