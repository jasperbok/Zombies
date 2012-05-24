package nl.jasperbok.zombies.gui;

import nl.jasperbok.zombies.StateManager;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenu extends Menu {
	protected Level level;

	private Image startSelected;
	private Image startDeselected;
	private Image quitSelected;
	private Image quitDeselected;
	
	private String selectedItem;

	public MainMenu(Level level) {
		super("data/sprites/gui/mainmenu/background.png");
		this.level = level;
		
		this.addItem("start", new MenuItem("data/sprites/gui/mainmenu/startButtRed.png", "data/sprites/gui/mainmenu/startButtWhite.png", 100, 220));
		this.addItem("quit", new MenuItem("data/sprites/gui/mainmenu/quitButtRed.png", "data/sprites/gui/mainmenu/quitButtWhite.png", 100, 300));
		select("start");
	}
	
	protected void itemAction(String actionKey) {
		switch (actionKey) {
		case "start":
			this.level.unPause();
			break;
		case "quit":
			this.level.quitGame();
			break;
		}
	}
}
