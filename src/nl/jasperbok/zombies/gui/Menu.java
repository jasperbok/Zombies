package nl.jasperbok.zombies.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nl.jasperbok.zombies.StateManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Menu {
	private HashMap<String, MenuItem> items;
	private String currentItemKey;
	
	private Image background;
	
	public Menu(String background) {
		this.items = new HashMap<String, MenuItem>();
		try {
			this.background = new Image(background);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a menu item.
	 * 
	 * @param key
	 * @param item
	 */
	protected void addItem(String key, MenuItem item) {
		items.put(key, item);
	}
	
	/**
	 * Selects the item with the given key.
	 * 
	 * @param itemKey
	 */
	protected void select(String itemKey) {
		if (this.items.get(this.currentItemKey) != null) {
			this.items.get(this.currentItemKey).deselect();
		}
		this.items.get(itemKey).select();
		this.currentItemKey = itemKey;
	}
	
	/**
	 * Selects the item next of the currently selected item.
	 */
	private void selectNext() {
		Iterator itemIterator = items.keySet().iterator();
		boolean itsTheNextOne = false;
		while (itemIterator.hasNext()) {
			Object itemKey = itemIterator.next();
			if (itsTheNextOne) {
				select((String) itemKey);
				return;
			} else if (itemKey == currentItemKey) {
				itsTheNextOne = true;
			}
		}
	}
	
	/**
	 * Selects the previous item to the currently selected item.
	 */
	private void selectPrevious() {
		Iterator itemIterator = items.keySet().iterator();
		String thePreviousKey = null;
		while (itemIterator.hasNext()) {
			Object itemKey = itemIterator.next();
			if (itemKey == currentItemKey && thePreviousKey != null) {
				select(thePreviousKey);
				return;
			} else {
				thePreviousKey = (String) itemKey;
			}
		}
	}
	
	/**
	 * Calls the itemAction method and gives the currently selected item key.
	 */
	private void activateItem() {
		this.itemAction(this.currentItemKey);
	}
	
	/**
	 * This method is called when a selected item is activated.
	 * Override this method to link actions to the items (this can be done with a switch for instance).
	 * 
	 * @param actionKey
	 */
	protected void itemAction(String actionKey) {
		
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		background.draw(0, 0);
		
		Iterator itemIterator = items.keySet().iterator();
		while (itemIterator.hasNext()) {
			Object itemKey = itemIterator.next();
			items.get(itemKey).render(arg0, arg1, arg2);
		}
	}

	public void update(GameContainer container, StateBasedGame game, int arg2) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_DOWN)) {
			selectNext();
		} else if (container.getInput().isKeyPressed(Input.KEY_UP)) {
			selectPrevious();
		}
		if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
			activateItem();
		}
	}
}
