package nl.jasperbok.zombies;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import nl.jasperbok.zombies.gui.MainMenu;
import nl.jasperbok.zombies.level.*;

public class Zombies extends StateBasedGame {
	public Level level;
	
	public Zombies() throws SlickException {
		super("Zombies");
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer app = new AppGameContainer(new Zombies());
		app.setDisplayMode(1280, 720, false);
		app.start();
	}

	public void initStatesList(GameContainer arg0) throws SlickException {
		
		//addState(new MainMenu());
		//addState(new Level1());
		addState(new Level2());
	}
}
