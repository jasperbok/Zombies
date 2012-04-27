package nl.jasperbok.zombies.level;

import nl.jasperbok.zombies.entity.building.Elevator;
import nl.jasperbok.zombies.entity.building.MagneticCrane;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.Crate;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

public class MergedLevel extends Level {
	/**
	 * The game holding this state.
	 */
	private StateBasedGame game;
	public Elevator elevator;
	public MagneticCrane crane;
	public Zombie zombie;
	public Crate crate;
	
	private Music bgMusic;
	
	public MergedLevel(String mapFileName) throws SlickException {
		super(mapFileName);
		init(mapFileName);
	}
	
	public void init(String levelName) {
		super.init(levelName);
		this.ID = 2;
		
		this.currentLevel = levelName;
		
		switch (levelName) {
		case "level1":
			try {
				String u = env.getPlayer().getClass().toString();//camera.setTarget(env.getPlayer());
				MagneticCrane crane = new MagneticCrane(this, new Vector2f(2160, 560));
				env.addEntity(crane);
				crate = new Crate(this, new Vector2f(2160, 660), crane);
				env.addEntity(crate);
				//env.mobDirector.addAttractor(env.getEntityByName("player"), 50, true);
				
				env.sounds.loadSFX("flatsh");
				bgMusic = new Music("data/sound/music/stil.ogg");
				bgMusic.loop();
			} catch (SlickException e) {
				e.printStackTrace();
			}
			break;
		case "level2":
			try {
				bgMusic = new Music("data/sound/music/stil.ogg");
				bgMusic.loop();
			} catch (SlickException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	public void reInit() {
		super.reInit(this.currentLevel);
		init(this.currentLevel);
	}
	
	public void reInit(String levelName) {
		super.reInit(levelName);
		init(levelName);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		super.update(container, game, delta);
		
		//craneLights[0].setPosition(new Vector2f(crane.armPos.x + 30 + camera.position.x, 130 - camera.position.y));
		//System.out.println(this.getClass().toString() + ".update: player_x" + this.env.getPlayer().position.x);
		//System.out.println(this.getClass().toString() + ".update: player_y" + this.env.getPlayer().position.y);
		
		switch (this.currentLevel) {
		case "level1":
			if (this.env.getEntityByName("crate_zombie").touches(crate) && crate.velocity.y > 0) {
				env.sounds.playSFX("flatsh");
				this.env.getEntityByName("crate_zombie").kill();
			}
			break;
		}
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		super.render(container, game, g);
	}
}
