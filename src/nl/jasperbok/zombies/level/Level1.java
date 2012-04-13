package nl.jasperbok.zombies.level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.Crate;
import nl.jasperbok.zombies.entity.object.WoodenCrate;
import nl.jasperbok.zombies.math.Vector2;
import nl.timcommandeur.zombies.light.FlashLight;

public class Level1 extends Level {
	public Elevator elevator;
	public MagneticCrane crane;
	public FlashLight[] craneLights;
	public Zombie zombie;
	public Crate crate;
	
	private Music bgMusic;

	public Level1() throws SlickException {
		super("level1");
		//elevator = new Elevator(this);
		//elevator.position = new Vector2(500.0f, 336.0f);
		//elevator.minHeight = 336.0f;
		//entities.add(elevator);
		Player player = new Player(100, this);
		//player.setPosition(240, 320);
		player.setPosition(1800, 660);
		env.setPlayer(player);
		
		env.addMob(new Zombie((float)(2400), 660));
		
		camera.setTarget(env.getPlayer());
		MagneticCrane crane = new MagneticCrane(this, new Vector2f(2160, 560));
		env.addEntity(crane);
		env.addEntity(new Crate(this, new Vector2f(2160, 660), crane));
		//crate = new Crate(this, 600, 500);
		//zombie = new Zombie(110, 0);

		//env.addEntity(crate);
		//env.addMob(zombie);
		
		env.addEntity(new WoodenCrate(this, 2160, 660));
		
		for (int i = 0; i < 5; i++) {
			env.addMob(new Zombie((float)(700 + i * 40), 80f));
		}
		env.mobDirector.addAttractor(env.getPlayer(), 1, true);
		
		bgMusic = new Music("data/sound/music/stil.ogg");
		bgMusic.loop();
		
		/*
		craneLights = new FlashLight[2];
		craneLights[0] = new FlashLight(lights, cHulls, new Vec2(crane.armPos.x + 30, 130));
		craneLights[1] = new FlashLight(lights, cHulls, new Vec2(crane.armPos.x + 90, 130));
		craneLights[0].rotate(100);
		craneLights[1].rotate(80);
		craneLights[0].setColor(new Color(150, 100, 100));
		craneLights[1].setColor(new Color(150, 100, 100));
		*/
	}

	public void update(GameContainer container, int delta) throws SlickException {
		//elevator.update(container, delta);
//		craneLights[0].setPos(new Vec2(crane.armPos.x + 30 + camera.position.x, 130 - camera.position.y));
//		craneLights[1].setPos(new Vec2(crane.armPos.x + 90 + camera.position.x, 130 - camera.position.y));
		//craneLights[0].setPos(new Vec2(crane.armPos.x + 30, 130));
		//craneLights[1].setPos(new Vec2(crane.armPos.x + 90, 130));
		super.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
	}
}
