package nl.jasperbok.zombies.thread;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.gui.PlayerSpeech;
import nl.jasperbok.zombies.level.Level;

public class LevelRenderThread extends Thread {
	protected Level level;
	
	public LevelRenderThread(Level level) {
		this.level = level;
	}
	
	public void start(GameContainer container, StateBasedGame game, Graphics g) {
		try {
			run(container, game, g);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if (this.level.currentState == Level.INGAME) {
			//camera.translate(g);
			
			if (level.doLighting) level.renderScene(container, g);
			level.renderLevel(container, g);
	        
	        if (level.doLighting) {
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
		        
		        level.fboLight.render(1.0f);
				
		        //GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_COLOR);
	        }
	        
			Hud.getInstance().render(container, g);
			PlayerSpeech.getInstance().render(container, g);
			
			//g.resetTransform();
		} else {
			this.level.menu.render(container, game, g);
		}
	}
}
