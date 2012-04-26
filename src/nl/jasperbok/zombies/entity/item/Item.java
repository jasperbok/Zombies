package nl.jasperbok.zombies.entity.item;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;

public class Item extends Entity {	
	/**
	 * Create an item.
	 * 
	 * @param level
	 * @param itemId
	 */
	public Item(Level level, String type) throws SlickException {
		super.init(level);
		this.components.add(new GravityComponent(0.01f, this));
		
		Animation staticAnim = new Animation();
		try {
			staticAnim.addFrame(new Image("data/sprites/entity/object/item/" + type + ".png"), 5000);
		} finally {
			
		}
		this.anims.put("static", staticAnim);
		this.currentAnim = this.anims.get("static");
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);
		
		this.isOnGround = level.env.isOnGround(this, false);
		
		if (level.env.getEntityByName("player").touches(this)) {
			level.env.getPlayer().inventory.add(collect());
		}
	}
	
	/**
	 * Removes the item and returns its name.
	 * 
	 * @return
	 */
	public String collect() {
		level.env.removeEntity(this);
		return this.name;
	}
}
