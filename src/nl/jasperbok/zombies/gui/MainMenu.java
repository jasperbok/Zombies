package nl.jasperbok.zombies.gui;

import nl.jasperbok.zombies.StateManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenu extends BasicGameState implements GameState {
	
	protected static int ID = 1;
	
	private Image background;
	private Image startSelected;
	private Image startDeselected;
	private Image quitSelected;
	private Image quitDeselected;
	
	private String selectedItem;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		this.background = new Image("data/sprites/gui/mainmenu/background.png");
		this.startSelected = new Image("data/sprites/gui/mainmenu/startButtRed.png");
		this.startDeselected = new Image("data/sprites/gui/mainmenu/startButtWhite.png");
		this.quitSelected = new Image("data/sprites/gui/mainmenu/quitButtRed.png");
		this.quitDeselected = new Image("data/sprites/gui/mainmenu/quitButtWhite.png");
		this.selectedItem = "start";
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		background.draw(0, 0);
		
		if (this.selectedItem == "start") {
			this.startSelected.draw(100, 300);
		} else {
			this.startDeselected.draw(100, 300);
		}
		
		if (this.selectedItem == "quit") {
			this.quitSelected.draw(100, 400);
		} else {
			this.quitDeselected.draw(100, 400);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int arg2)
			throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_DOWN) || container.getInput().isKeyPressed(Input.KEY_UP)) {
			if (this.selectedItem == "quit") {
				this.selectedItem = "start";
			} else {
				this.selectedItem = "quit";
			}
		}
		if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
			if (this.selectedItem == "start") {
				//game.enterState(2, new FadeOutTransition(), new FadeInTransition());
				((StateManager)game.getCurrentState()).setState(1);
			} else if (this.selectedItem == "quit") {
				container.exit();
			}
		}
	}

	@Override
	public int getID() {
		return ID;
	}

}
