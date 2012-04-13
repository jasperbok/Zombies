package nl.jasperbok.zombies.entity.mob;

import java.util.ArrayList;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.object.Crate;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Zombie extends Mob {
	// Animations
	private SpriteSheet sprites;
	private Animation idleAnimation;
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	private Animation currentAnimation;
	private boolean draw = true;
	
	private ArrayList<Vector2f> blockingPointsLeft;
	private ArrayList<Vector2f> blockingPointsRight;
	
	public Zombie(float x, float y) throws SlickException {
		this.blockingPointsLeft = new ArrayList<Vector2f>();
		this.blockingPointsRight = new ArrayList<Vector2f>();
		this.position.x = x;
		this.position.y = y;
		this.velocity.x = 0;
		this.velocity.y = 0;

		sprites = new SpriteSheet("data/sprites/entity/zombie_walk.png", 102, 150);
		idleAnimation = new Animation();
		idleAnimation.addFrame(sprites.getSprite(0, 0), 500);
		walkLeftAnimation = new Animation();
		for (int i = 0; i < 3; i++) {
			walkLeftAnimation.addFrame(sprites.getSprite(i, 0).getFlippedCopy(false, false), 150);
		}
		walkRightAnimation = new Animation();
		for (int i = 0; i < 3; i++) {
			walkRightAnimation.addFrame(sprites.getSprite(i, 0).getFlippedCopy(true, false), 150);
		}
		currentAnimation = idleAnimation;
		boundingBox = new Rectangle(0, 0, 4, 4);
	}
	
	public void update(Input input, int delta) {
		//position.add(velocity);
		
		//String moveStatus = level.movingStatus(this);
		
		//if (moveStatus == "falling") {
		//	velocity.add(level.gravity);
		//}
		
		if (velocity.x < 0) {
			currentAnimation = walkLeftAnimation;
		} else if (velocity.x > 0) {
			currentAnimation = walkRightAnimation;
		} else {
			currentAnimation = idleAnimation;
		}
		
		/*if (Math.abs(velocity.x) > 5) {
			velocity.x = velocity.x / Math.abs(velocity.x) * 5;
		}//*/
		
		for (Vector2f pl : blockingPointsLeft) {
			if (position.x < pl.x) {
				position.x += 2;
			}
		}
		
		for (Vector2f pr : blockingPointsRight) {
			if (position.x > pr.x) {
				position.x -= 2;
			}
		}
		
		updateBoundingBox();
		
		//if (draw) draw = !crateOnHead();
		//System.out.println(crateOnHead());
	}
	
	public void addBlockingPointLeft(float x) {
		blockingPointsLeft.add(new Vector2f(x, 0));
	}
	
	public void addBlockingPointRight(float x) {
		blockingPointsRight.add(new Vector2f(x, 0));
	}
	
	public void updateBoundingBox() {
		boundingBox.setBounds(position.x, position.y, currentAnimation.getCurrentFrame().getWidth(), currentAnimation.getCurrentFrame().getHeight());
	}
	
	/* OLD STUFF
	public boolean crateOnHead() {
		System.out.println("Checking for falling crates :)");
		return crate.boundingBox.intersects(boundingBox) && crate.velocity.y > 0;
	}*/
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		currentAnimation.draw(renderPosition.getX(), renderPosition.getY());
	}
	
	public void zombieDie() {
		draw = false;
	}
}
