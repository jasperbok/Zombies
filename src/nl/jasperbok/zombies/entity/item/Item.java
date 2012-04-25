package nl.jasperbok.zombies.entity.item;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;

public class Item extends Entity {
	/**
	 * The item id constants.
	 */
	public static final int KEY = 1;
	public static final int KEY_CARD = 2;
	/**
	 * Instance properties.
	 */
	public int id;
	/**
	 * Display properties.
	 */
	private Image image = null;
	private Animation animation = null;
	
	/**
	 * Create an item.
	 * 
	 * @param level
	 * @param itemId
	 */
	public Item(Level level, int itemId) {
		super.init(level);
		this.id = itemId;
		
		components.add(new GravityComponent(0.01f, this));
	}
	
	/**
	 * Create an item with a static image.
	 * 
	 * @param level
	 * @param offset
	 * @param width
	 * @param height
	 * @throws SlickException
	 */
	public Item(Level level, int itemId, Vector2f offset, int width, int height) throws SlickException {
		this(level, itemId);
		
		// Initialize the image.
		this.image = new Image("data/sprites/entity/object/item/items.png").getSubImage((int)offset.x, (int)offset.y, width, height);
		
		// Initialize the boundingbox.
		this.boundingBox = new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
	}
	
	public void update(Input input, int delta) {
		super.update(input, delta);
		boundingBox.setBounds(position.x, position.y, image.getWidth(), image.getHeight());
		this.isOnGround = level.env.isOnGround(this, false);
		
		if (level.env.getEntityByName("player").touches(this)) {
			level.env.getPlayer().inventory.add(collect());
		}
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(renderPosition.getX(), renderPosition.getY());
	}
	
	/**
	 * Removes the item and returnes its id.
	 * 
	 * @return
	 */
	public int collect() {
		level.env.removeEntity(this);
		return this.id;
	}
}
