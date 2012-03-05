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

public class Zombies extends BasicGame {
	public SpriteSheet playerSprites;
	public Animation playerWalk;
	public Player player;
	
	public Zombies() throws SlickException {
		super("Zombies");
	}
	
	public void init(GameContainer container) throws SlickException {
		this.playerSprites = new SpriteSheet("data/sprites/entity/walk.png", 63, 82);
		this.playerWalk = new Animation();
		for (int i = 0; i < 3; i++) {
			playerWalk.addFrame(playerSprites.getSprite(i, 0), 200);
		}
		player = new Player(playerWalk, 100, 0);
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		player.update(delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		player.render(container, g);
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer app = new AppGameContainer(new Zombies());
		app.setDisplayMode(1280, 720, false);
		app.start();
	}
}
