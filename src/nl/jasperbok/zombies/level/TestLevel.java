package nl.jasperbok.zombies.level;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.timcommandeur.zombies.light.LightSource;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import LightTest.Vec2;

public class TestLevel extends Level {

	public TestLevel() throws SlickException {
		super("proto_lvl");
		env.setPlayer(new Player(100, 200, this));
		//env.addEntity(new Elevator(this, 200, 620));
		for (int i = 0; i < 5; i++) {
			env.addMob(new Zombie((float)(100 + i * 40), 80f));
		}
		env.mobDirector.addAttractor(env.getPlayer(), 1, true);
		
		lights.add(new LightSource(new Vec2(300, 300), 600, 1.0f, new Color(255, 255, 255)));
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
}
