package nl.jasperbok.zombies.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

public class MenuItem {
	private Image selectedImage;
	private Image deselectedImage;
	
	private boolean selected = false;
	private Vector2f position;
	
	public MenuItem(String selectedImageLink, String deselectedImageLink) {
		this(selectedImageLink, deselectedImageLink, 0, 0);
	}
	
	public MenuItem(String selectedImageLink, String deselectedImageLink, float x, float y) {
		try {
			this.selectedImage = new Image(selectedImageLink);
			this.deselectedImage = new Image(deselectedImageLink);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		this.position = new Vector2f(x, y);
	}
	
	/**
	 * Selects the item.
	 */
	public void select() {
		this.selected = true;
	}
	
	/**
	 * Deselects the item.
	 */
	public void deselect() {
		this.selected = false;
	}
	
	/**
	 * Returns if the item is selected or not.
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Renders an item.
	 * The image depends on wether the item is selected or not.
	 * 
	 * @param container
	 * @param game
	 * @param g
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		if (selected) {
			g.drawImage(selectedImage, position.x, position.y);
		} else {
			g.drawImage(deselectedImage, position.x, position.y);
		}
	}
}
