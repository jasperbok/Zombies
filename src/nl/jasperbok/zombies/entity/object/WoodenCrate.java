package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.component.DraggableComponent;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class WoodenCrate extends Entity implements Usable {
	private Image image;

	/**
	 * WoodenCrate constructor creates a new crate.
	 * 
	 * @param level The level the crate is part of.
	 * @throws SlickException
	 */
	public WoodenCrate(Level level, int x, int y) throws SlickException {
		super.init(level);
		this.gravityAffected = false;
		setPosition(x, y);
		image = new Image("data/sprites/entity/object/wooden_crate.png", new Color(255, 255, 255));
		this.level = level;
		this.boundingBox = new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
		components.add(new GravityComponent(this));
		components.add(new DraggableComponent(this));
	}
	
	protected void updateBoundingBox() {
		//this.boundingBox.setBounds(position.x, position.y, image.getWidth(), image.getHeight());
		this.boundingBox.setBounds(0, 0, 0, 0);
	}

	public void update(Input input, GameContainer container, int delta) throws SlickException {
		this.isOnGround = level.env.isOnGround(this, false);
		updateBoundingBox();
		super.update(input, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(renderPosition.x, renderPosition.y);
	}

	@Override
	public void use(Entity user) {
		this.user = user;
	}

	@Override
	public boolean canBeUsed(Rectangle rect) {
		return false;
	}
}
