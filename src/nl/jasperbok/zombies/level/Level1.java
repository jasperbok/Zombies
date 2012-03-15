package nl.jasperbok.zombies.level;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import LightTest.Vec2;

import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.Block;
import nl.jasperbok.zombies.math.Vector2;
import nl.timcommandeur.zombies.light.FlashLight;

public class Level1 extends Level {
	public Elevator elevator;
	public MagneticCrane crane;
	public FlashLight[] craneLights;
	
	private Music bgMusic;

	public Level1() throws SlickException {
		super("zombies_level_1");
		//elevator = new Elevator(this);
		//elevator.position = new Vector2(500.0f, 336.0f);
		//elevator.minHeight = 336.0f;
		//entities.add(elevator);
		crane = new MagneticCrane(this, new Vector2(700.0f, 64.0f));
		
		//usableObjects.add(crane);
		
		craneLights = new FlashLight[2];
		craneLights[0] = new FlashLight(lights, cHulls, new Vec2(crane.armPos.x + 30, 130));
		craneLights[1] = new FlashLight(lights, cHulls, new Vec2(crane.armPos.x + 90, 130));
		craneLights[0].rotate(100);
		craneLights[1].rotate(80);
		
		craneLights[0].setColor(new Color(150, 100, 100));
		craneLights[1].setColor(new Color(150, 100, 100));
		
		bgMusic = new Music("data/sound/music/zombiesinspace.ogg");
		bgMusic.loop();
	}

	public void update(GameContainer container, int delta) throws SlickException {
		//elevator.update(container, delta);
		craneLights[0].setPos(new Vec2(crane.armPos.x + 30 + camera.position.x, 130 - camera.position.y));
		craneLights[1].setPos(new Vec2(crane.armPos.x + 90 + camera.position.x, 130 - camera.position.y));
		super.update(container, delta);
		//crane.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
}
