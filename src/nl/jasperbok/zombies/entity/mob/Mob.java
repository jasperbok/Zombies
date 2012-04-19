package nl.jasperbok.zombies.entity.mob;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;

public abstract class Mob extends Entity {
	public int agressionRange = 50;
	protected int health = 50;
	protected int maxHealth = 100;
	
	// Animations
	public Animation idleAnimation;
	public Animation walkLeftAnimation;
	public Animation walkRightAnimation;
	public Animation climbAnimation;
	public Animation fallAnimation;
	public Animation currentAnimation;
	public Animation attackAnimation;
	
	/**
	 * Heals the mob.
	 * 
	 * @param amount The amount of HP to heal.
	 */
	public void heal(int amount) {
		health += amount;
		if (health > maxHealth) health = maxHealth;
	}
	
	/**
	 * Deals damage to the mob.
	 * 
	 * @param damage The amount of damage to deal.
	 */
	public void hurt(int damage) {
		health -= damage;
		if (health <= 0) die();
	}
	
	/**
	 * Makes the mob hop with a given velocity.
	 * 
	 * @param x
	 * @param y
	 */
	public void hop(float x, float y) {
		this.velocity.x = x;
		this.velocity.y = y;
	}
	
	public void update(int delta) {
		
	}
}
