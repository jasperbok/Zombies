package nl.jasperbok.zombies.level;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import LightTest.Vec2;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.building.BreakableFloor;
import nl.jasperbok.zombies.entity.building.Door;
import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.building.Switch;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.Crate;
import nl.jasperbok.zombies.math.Vector2;
import nl.timcommandeur.zombies.light.FlashLight;

public class Level2 extends Level {
	
	private Music bgMusic;

	public Level2() throws SlickException {
		super("level2");
		this.ID = 3;
		
		camera.setTarget(env.getPlayer());
		
		bgMusic = new Music("data/sound/music/stil.ogg");
		bgMusic.loop();
		
		env.addMob(new Zombie(this, 320, 1200));
		
		env.addEntity(new BreakableFloor(this, 2480, 720));
		
		Switch powerSwitch = new Switch(this, false, new Vector2f(320, 1190), new HashMap<String, String>());
		Switch firstDoorSwitch = new Switch(this, false, new Vector2f(2100, 1190), new HashMap<String, String>());
		Switch unpoweredSwitch = new Switch(this, false, new Vector2f(2990, 1190), powerSwitch, new HashMap<String, String>());
		
		env.addEntity(powerSwitch);
		env.addEntity(firstDoorSwitch);
		env.addEntity(unpoweredSwitch);

		Door firstDoor = new Door(this, firstDoorSwitch, new Vector2f(1920, 1200), Door.SIDE_RIGHT);
		Door finalDoor = new Door(this, unpoweredSwitch, new Vector2f(3050, 1200), Door.SIDE_LEFT);
		
		env.addEntity(firstDoor);
		env.addEntity(finalDoor);
		
		env.sounds.loadSFX("zombie_groan1");
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		super.update(container, game, delta);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		super.render(container, game, g);
	}
}
