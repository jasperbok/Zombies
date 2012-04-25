package nl.jasperbok.zombies.level;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import LightTest.Vec2;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.Crate;
import nl.jasperbok.zombies.math.Vector2;
import nl.timcommandeur.zombies.light.FlashLight;

public class Level2 extends Level {
	
	private Music bgMusic;

	public Level2() throws SlickException {
		super("level2");
		Player player = new Player(100, this);
		player.setPosition(0, 0);
		env.setPlayer(player);
		
		bgMusic = new Music("data/sound/music/stil.ogg");
		bgMusic.loop();
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		super.update(container, game, delta);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		super.render(container, game, g);
	}
}
