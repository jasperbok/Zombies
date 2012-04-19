package nl.jasperbok.zombies.entity.item;

import java.util.ArrayList;

public class Inventory {
	private ArrayList<Integer> items;
	
	public Inventory() {
		this.items = new ArrayList<Integer>();
	}
	
	/**
	 * Adds an item to the inventory.
	 * 
	 * @param itemId
	 */
	public void add(int itemId) {
		items.add(itemId);
	}
	
	/**
	 * Removes an item from the inventory.
	 * 
	 * @param itemId
	 */
	public void remove(int itemId) {
		if (items.contains(itemId)) {
			items.remove((Integer)itemId);
		}
	}
	
	/**
	 * Checks if the inventory contains an item.
	 * 
	 * @param itemId
	 * @return
	 */
	public boolean contains(int itemId) {
		return items.contains((Integer)itemId);
	}
	
	/**
	 * Returns the amount of times an item id is present inside the inventory.
	 * @param itemId
	 * @return
	 */
	public int containsAmount(int itemId) {
		int i = 0;
		for (Integer item : items) {
			if (item == itemId) {
				i++;
			}
		}
		return i;
	}
	
	/**
	 * Logs all the items inside the inventory.
	 */
	public void log() {
		int i = 0;
		for (int itemId : items) {
			System.out.println("inventory-slot: " + i + " item-id: " + itemId);
			i++;
		}
	}
}
