package nl.jasperbok.zombies.entity.object;

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
		this.setPosition(x, y);
		this.image = new Image("data/sprites/entity/object/wooden_crate.png");
		this.level = level;
		this.boundingBox = new Rectangle(this.position.getX(), this.position.getY(), 80, 80);
		this.addComponent(new GravityComponent(this));
		this.addComponent(new DraggableComponent(this));
	}
	
	protected void updateBoundingBox() {
		this.boundingBox.setBounds(this.position.getX(), this.position.getY(), 80, 80);
	}

	public void update(Input input, int delta){
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
		System.out.println("use");
	}

	@Override
	public boolean canBeUsed(Rectangle rect) {
		return false;
	}
}
