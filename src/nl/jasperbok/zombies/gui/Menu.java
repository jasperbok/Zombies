package nl.jasperbok.zombies.gui;

import java.util.ArrayList;
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
	private ArrayList<MenuItem> items;
	private int currentItemIndex;
	
	private Image background;
	
	public Menu(String background) {
		this.items = new ArrayList<MenuItem>();
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
		items.add(item);
	}
	
	/**
	 * Selects the item with the given key.
	 * 
	 * @param itemKey
	 */
	protected void select(int item) {
		if (this.items.get(currentItemIndex) != null) {
			this.items.get(currentItemIndex).deselect();
		}
		((MenuItem) this.items.get(item)).select();
		this.currentItemIndex = item;
	}
	
	/**
	 * Selects the item next of the currently selected item.
	 */
	private void selectNext() {
		boolean itsTheNextOne = false;
		int index = 0;
		for (MenuItem item : items) {
			if (itsTheNextOne) {
				select(index);
				return;
			} else if (index == currentItemIndex) {
				itsTheNextOne = true;
			}
			index++;
		}
	}
	
	/**
	 * Selects the previous item to the currently selected item.
	 */
	private void selectPrevious() {
		int index = 0;
		Integer thePreviousIndex = null;
		for (MenuItem item : items) {
			if (index == currentItemIndex && thePreviousIndex != null) {
				select(thePreviousIndex.intValue());
				return;
			} else {
				thePreviousIndex = new Integer(index);
			}
			index++;
		}
	}
	
	/**
	 * Calls the itemAction method and gives the currently selected item key.
	 */
	private void activateItem() {
		this.itemAction(this.currentItemIndex);
	}
	
	/**
	 * This method is called when a selected item is activated.
	 * Override this method to link actions to the items (this can be done with a switch for instance).
	 * 
	 * @param actionKey
	 */
	protected void itemAction(int actionKey) {
		
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		background.draw(0, 0);
		
		for (MenuItem item : items) {
			item.render(arg0, arg1, arg2);
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
