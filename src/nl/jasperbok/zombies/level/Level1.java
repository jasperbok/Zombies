package nl.jasperbok.zombies.level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import nl.jasperbok.zombies.StateManager;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.building.AutoTurret;
import nl.jasperbok.zombies.entity.building.Door;
import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.Crate;

public class Level1 extends Level {
	/**
	 * The game holding this state.
	 */
	private StateBasedGame game;
	public Elevator elevator;
	public MagneticCrane crane;
	public Zombie zombie;
	public Crate crate;
	
	public Zombie crateZombie;
	
	private Music bgMusic;

	public Level1() throws SlickException {
		super("level1");
		this.ID = 2;
		
		camera.setTarget(env.getPlayer());
		MagneticCrane crane = new MagneticCrane(this, new Vector2f(2160, 560));
		env.addEntity(crane);
		crate = new Crate(this, new Vector2f(2160, 660), crane);
		env.addEntity(crate);
		env.mobDirector.addAttractor(env.getEntityByName("player"), 50, true);
		
		env.sounds.loadSFX("flatsh");
		bgMusic = new Music("data/sound/music/stil.ogg");
		bgMusic.loop();
		
		//craneLights[0] = new FlashLight(lights, cHulls, new Vector2f(crane.armPos.x + 30, 730), 200, camera);
		//craneLights[1] = new FlashLight(lights, cHulls, new Vector2f(crane.armPos.x + 90, 730), 200, camera);
		//craneLights[0].setPosition(new Vector2f(crane.armPos.getX() + 30 + camera.position.getX(), 730 - camera.position.getY()));
		//craneLights[1].setPosition(new Vector2f(crane.armPos.getX() + 90 + camera.position.getX(), 730 - camera.position.getY()));
		//craneLights[0].rotate(100);
		//craneLights[1].rotate(80);
		//craneLights[0].setColor(new Color(100, 100, 100));
		//craneLights[1].setColor(new Color(100, 100, 100));
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		//craneLights[0].setPosition(new Vector2f(crane.armPos.x + 30 + camera.position.x, 130 - camera.position.y));
		//System.out.println(this.getClass().toString() + ".update: player_x" + this.env.getPlayer().position.x);
		//System.out.println(this.getClass().toString() + ".update: player_y" + this.env.getPlayer().position.y);
		
		if (this.env.getEntityByName("crate_zombie").touches(crate) && crate.velocity.y > 0) {
			env.sounds.playSFX("flatsh");
			crateZombie.kill();
		}
		super.update(container, game, delta);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		super.render(container, game, g);
	}
}
