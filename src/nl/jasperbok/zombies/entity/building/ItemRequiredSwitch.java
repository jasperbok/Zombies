package nl.jasperbok.zombies.entity.building;

import java.util.HashMap;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.item.Inventory;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class ItemRequiredSwitch extends Switch {
	private HashMap<Integer, Integer> requiredItems;
	
	public ItemRequiredSwitch(Level level, Vector2f position) throws SlickException {
		super(level, false, position);
		requiredItems = new HashMap<Integer, Integer>();
	}
	
	public void use(Entity user) {
		if (checkRequiredItems(user.inventory)) {
			super.use(user);
		}
	}
	
	public void addRequirement(Integer itemId, Integer amount) {
		requiredItems.put(itemId, amount);
	}
	
	public boolean checkRequiredItems(Inventory inventory) {
		for (Integer i : requiredItems.keySet()) {
			if (inventory.containsAmount(i) < requiredItems.get(i)) {
				return false;
			}
		}
		return true;
	}
}
