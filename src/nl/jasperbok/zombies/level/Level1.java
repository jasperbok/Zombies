package nl.jasperbok.zombies.level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.Block;
import nl.jasperbok.zombies.math.Vector2;

public class Level1 extends Level {
	public Elevator elevator;

	public Level1() throws SlickException {
		super("zombies_level_1.tmx");
		elevator = new Elevator(this);
		elevator.position = new Vector2(500.0f, 336.0f);
		elevator.minHeight = 336.0f;
		entities.add(elevator);
		usableObjects.add(elevator);
	}

	public void update(GameContainer container, int delta) throws SlickException {
		elevator.update(container, delta);
		super.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
}
