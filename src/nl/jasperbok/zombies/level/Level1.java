package nl.jasperbok.zombies.level;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;

import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.Block;

public class Level1 extends Level {
	public Elevator elevator;

	public Level1() throws SlickException {
		super("zombies_level_1.tmx");
		elevator = new Elevator(this);
	}

	public void update(GameContainer container, int delta) throws SlickException {
		
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		
	}
}
