package nl.jasperbok.zombies;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import nl.jasperbok.zombies.gui.MainMenu;
import nl.jasperbok.zombies.level.*;

public class Zombies extends StateBasedGame {
	public Level level;
	MainMenu mainMenu;
	
	public Zombies() throws SlickException {
		super("Zombies");
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer app = new AppGameContainer(new Zombies());
		app.setDisplayMode(1280, 720, false);
		app.start();
	}

	public void initStatesList(GameContainer container) throws SlickException {
		//addState(new StateManager(container, this));
		addState(new Level1());
		//addState(new Level2());
	}
}
