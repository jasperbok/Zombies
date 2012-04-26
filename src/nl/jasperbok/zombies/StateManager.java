package nl.jasperbok.zombies;

import nl.jasperbok.zombies.gui.MainMenu;
import nl.jasperbok.zombies.level.Level1;
import nl.jasperbok.zombies.level.Level2;
import nl.timcommandeur.zombies.screen.Camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public class StateManager extends BasicGameState implements GameState {
	private GameState currentState;
	
	GameContainer container;
	StateBasedGame game;
	
	public static StateManager instance;
	
	public StateManager(GameContainer container, StateBasedGame game) {
		this.container = container;
		this.game = game;
		this.instance = this;

		setState(1);
	}
	
	public void setState(int levelID) {
		currentState = null;
		try {
			switch(levelID) {
			case 0:
				currentState = new MainMenu();
				break;
			case 1:
				currentState = new Level1();
				break;
			case 2:
				currentState = new Level2();
				break;
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
		try {
			currentState.init(container, game);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void update(GameContainer container, StateBasedGame game, int arg2) throws SlickException {
		currentState.update(container, game, arg2);
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		currentState.render(arg0, arg1, arg2);
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static synchronized StateManager getInstance() {
		return instance;
	}
}
