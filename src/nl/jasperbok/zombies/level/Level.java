package nl.jasperbok.zombies.level;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.Block;

public class Level {
	public Player player;
	public TiledMap map;
	
	// Animations and SpriteSheets.
	public SpriteSheet playerSprites;
	public Animation playerWalk;
	public Animation playerIdle;
	public Animation playerFall;
	public Animation playerHit;
	public Animation playerCrouch;
	
	public Level(String mapFileName) throws SlickException {
		init(mapFileName);
	}
	
	public void init(String mapFileName) throws SlickException {
		this.map = new TiledMap("/data/maps/" + mapFileName);
		this.player = new Player(100, 0, map);
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		player.update(container, delta);
		Hud.getInstance().update(delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		map.render(0, 0);
		player.render(container, g);
		Hud.getInstance().render(container, g);
	}
}
