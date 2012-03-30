package nl.jasperbok.zombies.level;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.mob.Zombie;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import LightTest.Vec2;

public class TestLevel extends Level {

	public TestLevel() throws SlickException {
		super("proto_lvl");
		env.setPlayer(new Player(100, 80, this));
		for (int i = 0; i < 2; i++) {
			env.addMob(new Zombie(110 + i, 80));
		}
		env.mobDirector.addAttractor(env.getPlayer(), 1);
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
}
