package nl.jasperbok.zombies.entity.item;

import java.util.HashMap;

public class Inventory {
	private HashMap<String, Integer> items = new HashMap<String, Integer>();
	
	public Inventory() {
	}
	
	/**
	 * Adds an item to the inventory.
	 * 
	 * @param itemName
	 */
	public void add(String itemName) {
		if (this.items.containsKey(itemName)) {
			this.items.put(itemName, this.items.get(itemName) + 1);
		} else {
			this.items.put(itemName, 1);
		}
	}
	
	/**
	 * Removes an item from the inventory.
	 * 
	 * @param itemName
	 */
	public void remove(String itemName) {
		if (this.items.containsKey(itemName)) {
			if (this.items.get(itemName) > 1) {
				this.items.put(itemName, this.items.get(itemName) - 1);
			} else {
				this.items.remove(itemName);
			}
		}
	}
	
	/**
	 * Checks if the inventory contains an item.
	 * 
	 * @param itemName
	 * @return
	 */
	public boolean contains(String itemName) {
		return this.items.containsKey(itemName);
	}
	
	/**
	 * Returns the amount of times an item id is present inside the inventory.
	 * @param itemName
	 * @return
	 */
	public int containsAmount(String itemName) {
		if (this.items.containsKey(itemName)) {
			return this.items.get(itemName);
		} else {
			return 0;
		}
	}
	
	/**
	 * Logs all the items inside the inventory.
	 */
	public void log() {
		// Bleargh
	}
}
