package nl.jasperbok.zombies.entity.mob;

import nl.jasperbok.zombies.entity.Entity;

public abstract class Mob extends Entity {
	protected int health = 50;
	protected int maxHealth = 100;
	
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
	 * Makes the mob die.
	 */
	protected void die() {
		//this.level.remove(this);
	}
	
	public void update(int delta) {
		
	}
}
