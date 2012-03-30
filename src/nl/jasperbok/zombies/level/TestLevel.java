package nl.jasperbok.zombies.level;

import nl.jasperbok.zombies.entity.Player;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import LightTest.Vec2;

public class TestLevel extends Level {

	public TestLevel() throws SlickException {
		super("proto_lvl");
		env.setPlayer(new Player(100, 0, this));
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
}
