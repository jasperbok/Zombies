package nl.jasperbok.zombies.entity.object;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.entity.component.DraggableComponent;
import nl.jasperbok.zombies.entity.component.GravityComponent;
import nl.jasperbok.zombies.level.Level;

public class WoodenCrate extends Entity implements Usable {
	private Image image;
	private Rectangle useBox;
	public boolean playerControlled = false;

	/**
	 * WoodenCrate constructor creates a new crate.
	 * 
	 * @param level The level the crate is part of.
	 * @throws SlickException
	 */
	public WoodenCrate(Level level, int x, int y) throws SlickException {
		super.init(level);
		this.gravityAffected = false;
		this.isTopSolid = true;
		this.position = new Vector2f(x, y);
		this.image = new Image("data/sprites/entity/object/wooden_crate.png");
		this.level = level;
		this.boundingBox = new Rectangle(this.position.getX(), this.position.getY(), 80, 80);
		this.useBox = new Rectangle(this.position.getX() - 30, this.position.getY(), this.image.getWidth() + 60, this.image.getHeight());
		this.addComponent(new GravityComponent(this));
		this.addComponent(new DraggableComponent(this));
	}
	
	protected void updateBoundingBox() {
		this.boundingBox.setBounds(this.position.getX(), this.position.getY(), 80, 80);
		useBox.setBounds(position.getX() - 30, position.getY(), 140, 80);
	}

	public void update(Input input, int delta){
		this.isOnGround = level.env.isOnGround(this, false);
		updateBoundingBox();
		
		if (playerControlled) {
			this.velocity = user.velocity.copy();
			if (input.isKeyPressed(Input.KEY_E)) {
				playerControlled = false;
				user = null;
			}
		}
		
		super.update(input, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		image.draw(renderPosition.x, renderPosition.y);
	}

	@Override
	public void use(Entity user) {
		this.user = user;
		this.playerControlled = true;
		System.out.println("use");
	}

	@Override
	public boolean canBeUsed(Rectangle rect) {
		return rect.intersects(useBox);
	}
}
