package nl.jasperbok.zombies;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.*;

public class Zombies extends BasicGame {
	public Level level;
	
	public Zombies() throws SlickException {
		super("Zombies");
	}
	
	public void init(GameContainer container) throws SlickException {
		level = new Level1();
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		level.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		level.render(container, g);
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer app = new AppGameContainer(new Zombies());
		app.setDisplayMode(1280, 720, false);
		app.start();
	}
}
