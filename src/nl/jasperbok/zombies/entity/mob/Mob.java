package nl.jasperbok.zombies.entity.mob;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;

public abstract class Mob extends Entity {
	public int agressionRange = 50;
	protected int maxHealth = 100;
	
	// Animations
	public Animation idleAnimation;
	public Animation walkLeftAnimation;
	public Animation walkRightAnimation;
	public Animation climbAnimation;
	public Animation fallAnimation;
	public Animation currentAnimation;
	public Animation attackAnimation;
}
