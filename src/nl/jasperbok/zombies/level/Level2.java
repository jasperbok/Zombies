package nl.jasperbok.zombies.level;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import LightTest.Vec2;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.Crate;
import nl.jasperbok.zombies.math.Vector2;
import nl.timcommandeur.zombies.light.FlashLight;

public class Level2 extends Level {
	public Elevator elevator;
	public MagneticCrane crane;
	public FlashLight[] craneLights;
	public Zombie zombie;
	public Crate crate;
	
	private Music bgMusic;

	public Level2() throws SlickException {
		super("level2");
		env.setPlayer(new Player(100, 0, this));
		bgMusic = new Music("data/sound/music/zombiesinspace.ogg");
		bgMusic.loop();
	}

	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
}
