package nl.jasperbok.zombies.level;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import LightTest.Vec2;

import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.Crate;
import nl.jasperbok.zombies.math.Vector2;
import nl.timcommandeur.zombies.light.FlashLight;
import nl.timcommandeur.zombies.light.LightSource;

public class Level1 extends Level {
	public Elevator elevator;
	public MagneticCrane crane;
	public FlashLight[] craneLights;
	public Zombie zombie;
	public Crate crate;
	public LightSource playerLight;
	
	private Music bgMusic;

	public Level1() throws SlickException {
		super("zombies_level_1.tmx");
		//elevator = new Elevator(this);
		//elevator.position = new Vector2(500.0f, 336.0f);
		//elevator.minHeight = 336.0f;
		//entities.add(elevator);
		crate = new Crate(this, 600, 500);
		zombie = new Zombie(750, 500, crate);
		crane = new MagneticCrane(this, new Vector2(700.0f, 64.0f), crate);
		
		playerLight = new LightSource(new Vec2(0, 0), 40, 0, new Color(10, 10, 10));
		lights.add(playerLight);
		
		craneLights = new FlashLight[2];
		craneLights[0] = new FlashLight(lights, cHulls, new Vec2(crane.armPos.x + 30, 145));
		craneLights[1] = new FlashLight(lights, cHulls, new Vec2(crane.armPos.x + 90, 145));
		craneLights[0].rotate(100);
		craneLights[1].rotate(80);
		//craneLights[0].setColor(new Color(150, 100, 100));
		//craneLights[1].setColor(new Color(150, 100, 100));

		entities.add(crate);
		entities.add(crane);
		entities.add(zombie);
		
		usableObjects.add(crane);
		
		//bgMusic = new Music("data/sound/music/zombiesinspace.ogg");
		//bgMusic.loop();
	}

	public void update(GameContainer container, int delta) throws SlickException {
		//elevator.update(container, delta);
//		craneLights[0].setPos(new Vec2(crane.armPos.x + 30 + camera.position.x, 130 - camera.position.y));
//		craneLights[1].setPos(new Vec2(crane.armPos.x + 90 + camera.position.x, 130 - camera.position.y));
		craneLights[0].setPos(new Vec2(crane.armPos.x + 30, 145));
		craneLights[1].setPos(new Vec2(crane.armPos.x + 90, 145));
		super.update(container, delta);
		crane.update(container, delta);
		crate.update(container, delta);
		zombie.update(container, delta);
		playerLight.setPos(new Vec2(player.position.x + 16.5f, player.position.y + 35));
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
	
	public void remove() {
    	System.out.println("Remove called!");
    	entities.remove(zombie);
    }
}
