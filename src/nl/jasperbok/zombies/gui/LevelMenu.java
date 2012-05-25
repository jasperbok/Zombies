package nl.jasperbok.zombies.gui;

import nl.jasperbok.zombies.level.Level;

public class LevelMenu extends Menu {
	protected Level level;
	
	public LevelMenu(Level level) {
		super("data/sprites/gui/levelmenu/background.png");
		
		this.level = level;
		
		this.addItem("1_level1", new MenuItem("data/sprites/gui/levelmenu/level1Active.png", "data/sprites/gui/levelmenu/level1.png", 100, 220));
		this.addItem("2_level2", new MenuItem("data/sprites/gui/levelmenu/level2Active.png", "data/sprites/gui/levelmenu/level2.png", 100, 300));
		this.addItem("3_level3", new MenuItem("data/sprites/gui/levelmenu/level3Active.png", "data/sprites/gui/levelmenu/level3.png", 100, 380));
		//this.addItem("4_level4", new MenuItem("data/sprites/gui/levelmenu/level4Active.png", "data/sprites/gui/levelmenu/level4.png", 100, 460));
		select(0);
	}
	
	protected void itemAction(int actionKey) {
		System.out.println("" + actionKey);
		this.level.setState(Level.INGAME);
		switch (actionKey) {
		case 0:
			this.level.reInit("level1");
			break;
		case 1:
			this.level.reInit("level2");
			break;
		case 2:
			this.level.reInit("level3");
			break;
		case 3:
			this.level.reInit("level4");
			break;
		}
	}
}
