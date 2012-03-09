package test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.Level;

import LightTest.*;
import LightTest.ShaderObjects.*;

public class Zombies extends BasicGame {
	public Level level;
	
	private List<ConvexHull> cHulls;
    public static List<Light> lights;
    private boolean addLight=true;
    private float intensity = 1.0f;
    private FrameBufferObject fboLight;
    private FrameBufferObject fboLevel;
	
	public Zombies() throws SlickException {
		super("Zombies");
		
		lights = new ArrayList<Light>();
        cHulls = new ArrayList<ConvexHull>();
	}
	
	private void addRandomHull() {
        Random gen = new Random();
        Vec2 points[] = {new Vec2(0, 0), new Vec2(20, 0), new Vec2(20, 20), new Vec2(0, 20)};
        cHulls.add(new ConvexHull(new Vec2(gen.nextInt(1024), gen.nextInt(768)), Arrays.asList(points), 0.1f, Color.white));
    }

    private void addRandomLight() {
        Random gen = new Random();
        Color c = new Color(gen.nextFloat(), gen.nextFloat(), gen.nextFloat());
        lights.add(new Light(new Vec2(gen.nextInt(1024), gen.nextInt(768)), 200.0f, 0.0f, c));
    }
	
	public void init(GameContainer container) throws SlickException {
		level = new Level("zombies_level_1.tmx");
		
		fboLight = new FrameBufferObject(new Point(1280, 720));
		fboLevel = new FrameBufferObject(new Point(1280, 720));
		
        for(int i=0; i<30; i++) {
            //addRandomHull();
        }
        //Create lights
        for(int i=0; i<4; i++) {
            addRandomLight();
        }
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		level.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
        
        renderScene(container, g);
        renderLevel(container, g);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
        
        fboLight.render(1.0f);
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer app = new AppGameContainer(new Zombies());
		app.setDisplayMode(1280, 720, false);
		app.start();
	}
	
	public void renderLevel(GameContainer container, Graphics g) throws SlickException
	{
		fboLevel.enable();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
		
		//GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		level.render(container, g);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void renderScene(GameContainer container, Graphics g) throws SlickException
    {
        //# Clear the color buffer
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        //# Use less-than or equal depth testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        fboLight.enable();
        
        //# Clear the fbo, and z-buffer
        clearFbo();

        //# fill z-buffer
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glColorMask(false, false, false, false);
        for (ConvexHull hull: cHulls) {
            hull.render();
        }
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for (Light light:lights) {
            //# Clear the alpha channel of the framebuffer to 0.0
            clearFramebufferAlpha();

            //# Write new framebuffer alpha
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColorMask(false, false, false, true);
            light.render(intensity);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);
            //# Draw shadow geometry
            for (ConvexHull hull: cHulls) {
                hull.drawShadowGeometry(light);
            }
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            //# Draw geometry
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            
            GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
            GL11.glColorMask(true, true, true, false);
            if(addLight) {
                for (Light light1: lights) {
                    light1.render(intensity);
                }
            }
            
            //GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_ALPHA);
            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //level.render(container, g);
            
            for (ConvexHull hull: cHulls) {
                hull.render();
            }
        }

        fboLight.disable();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        //# Render the fbo on top of the color buffer
        fboLight.render(1.0f);

    }

    private void clearFbo() {
        GL11.glClearDepth(1.1);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    private void clearFramebufferAlpha() {
        GL11.glColorMask(false, false, false, true);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }
}
