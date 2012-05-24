package nl.jasperbok.zombies.gui;

import java.util.HashMap;
import java.util.Iterator;

import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Comic {
	protected Level level;
	
	private HashMap<String, Image> pages;
	private String currentPageKey;
	
	public Comic(Level level) {
		this.level = level;
		pages = new HashMap<String, Image>();
	}
	
	/**
	 * Adds a page to the comic.
	 * 
	 * @param key
	 * @param pageImage
	 */
	public void addPage(String key, Image pageImage) {
		pages.put(key, pageImage);
	}
	
	/**
	 * Selects the item with the given key.
	 * 
	 * @param itemKey
	 */
	protected void select(String pageKey) {
		this.currentPageKey = pageKey;
	}
	
	/**
	 * Selects the item next of the currently selected item.
	 */
	private void selectNext() {
		Iterator pageIterator = pages.keySet().iterator();
		boolean itsTheNextOne = false;
		while (pageIterator.hasNext()) {
			Object itemKey = pageIterator.next();
			if (itsTheNextOne) {
				select((String) itemKey);
				return;
			} else if (itemKey == currentPageKey) {
				itsTheNextOne = true;
			}
		}
		if (itsTheNextOne && !pageIterator.hasNext()) {
			quit();
		}
	}
	
	public void quit() {
		
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		this.pages.get(this.currentPageKey).draw(0, 0);
	}

	public void update(GameContainer container, StateBasedGame game, int arg2) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_Q)) {
			this.quit();
		} else if (container.getInput().isKeyPressed(Input.ANY_CONTROLLER)) {
			selectNext();
		}
	}
}
