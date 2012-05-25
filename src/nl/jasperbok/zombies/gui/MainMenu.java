package nl.jasperbok.zombies.gui;

import nl.jasperbok.zombies.level.Level;

public class MainMenu extends Menu {
	protected Level level;

	public MainMenu(Level level) {
		super("data/sprites/gui/mainmenu/background.png");
		this.level = level;
		
		this.addItem("start", new MenuItem("data/sprites/gui/mainmenu/startButtRed.png", "data/sprites/gui/mainmenu/startButtWhite.png", 100, 220));
		this.addItem("quit", new MenuItem("data/sprites/gui/mainmenu/quitButtRed.png", "data/sprites/gui/mainmenu/quitButtWhite.png", 100, 300));
		select(0);
	}
	
	protected void itemAction(int actionKey) {
		switch (actionKey) {
		case 0:
			this.level.reInit();
			this.level.currentState = Level.INGAME;
			System.out.println(this.getClass().toString() + ".itemAction: STARTE DAS VERDAMMTEN SPIEL!!!");
			break;
		case 1:
			this.level.quitGame();
			break;
		}
	}
}
