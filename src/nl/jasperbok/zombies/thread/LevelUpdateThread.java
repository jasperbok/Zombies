package nl.jasperbok.zombies.thread;

import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.gui.PlayerSpeech;
import nl.jasperbok.zombies.level.Level;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class LevelUpdateThread extends Thread {
	protected Level level;
	
	public LevelUpdateThread(Level level) {
		this.level = level;
	}
	
	public void start(GameContainer container, StateBasedGame game, int delta) {
		try {
			run(container, game, delta);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (this.level.quit) {
			container.exit();
		}
		if (this.level.currentState == Level.INGAME) {
			level.camera.update(container, delta);
			Hud.getInstance().update(delta);
			PlayerSpeech.getInstance().update(delta);
			//fl.setPos(new Vec2(player.position.x + 10 + camera.position.x, player.position.y + 10 - camera.position.y));
			level.fl.setPosition(level.env.getEntityByName("player").position.x + level.env.getEntityByName("player").size.x / 2, level.env.getEntityByName("player").position.y + level.env.getEntityByName("player").size.x / 4 + 12, 65);
			level.fl.pointToMouse(container);
			//fl.pointToMouse(container);
			//System.out.println(container.getInput().getAbsoluteMouseX() + camera.position.x);
			
			level.playerLight.setPosition(level.env.getEntityByName("player").position.x + level.env.getEntityByName("player").size.x / 2, level.env.getEntityByName("player").position.y + level.env.getEntityByName("player").size.y / 2 - 20);
			
			/*System.out.println("player.position.y: " + env.getPlayer().position.y);
			System.out.println("player.renderPosition.y: " + env.getPlayer().renderPosition.y);
			System.out.println("camera.position.y: " + camera.position.y);*/
			
			level.env.update(container, delta);
		} else {
			this.level.menu.update(container, game, delta);
		}
	}
}
