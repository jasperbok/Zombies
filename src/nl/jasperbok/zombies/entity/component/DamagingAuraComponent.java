package nl.jasperbok.zombies.entity.component;

import java.util.ArrayList;

import nl.jasperbok.engine.Entity;

import org.newdawn.slick.Input;

public class DamagingAuraComponent extends Component {
	protected int internalDamage = 1;
	protected int externalDamage = 1;
	
	/**
	 * 
	 * @param owner
	 */
	public DamagingAuraComponent(Entity owner) {
		this.owner = owner;
		this.id = Component.DAMAGING_AURA;
	}
	
	/**
	 * 
	 * @param owner
	 * @param internalDamage
	 * @param externalDamage
	 */
	public DamagingAuraComponent(Entity owner, int internalDamage, int externalDamage) {
		this(owner);
		this.internalDamage = internalDamage;
		this.externalDamage = externalDamage;
	}
	
	/**
	 * 
	 * @param owner
	 * @param externalDamage
	 */
	public DamagingAuraComponent(Entity owner, int externalDamage) {
		this(owner);
		this.externalDamage = externalDamage;
	}
	
	public void update(Input input, int delta) {
		ArrayList<Entity> colliding = owner.level.env.checkForEntityCollision(owner);
		if (colliding.size() > 0) {
			// If the external colliding object has a life component, it should take the external damage.
			if (colliding.get(0).hasComponent(Component.LIFE)) {
				LifeComponent externalLifeComponent = (LifeComponent) colliding.get(0).getComponent(Component.LIFE);
				externalLifeComponent.takeDamage(externalDamage);
			}
			
			// If the owner has a life component it should take the internal damage.
			if (owner.hasComponent(Component.LIFE) && colliding.get(0).hasComponent(Component.LIFE)) {
				LifeComponent internalLifeComponent = (LifeComponent) owner.getComponent(Component.LIFE);
				internalLifeComponent.takeDamage(internalDamage);
			}
		}
	}
}
