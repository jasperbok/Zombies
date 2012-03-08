package nl.jasperbok.zombies.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;

import LightTest.ConvexHull;
import LightTest.FrameBufferObject;
import LightTest.Light;
import LightTest.Vec2;

import nl.timcommandeur.zombies.light.LightSource;
import nl.timcommandeur.zombies.light.ShadowHull;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.Block;

public class Level {
	public Player player;
	public TiledMap map;
	
	// Animations and SpriteSheets.
	public SpriteSheet playerSprites;
	public Animation playerWalk;
	public Animation playerIdle;
	public Animation playerFall;
	public Animation playerHit;
	public Animation playerCrouch;
	
	// Lighting
    public static List<LightSource> lights;
    private float intensity = 1.0f;
    private FrameBufferObject fboLight;
    private FrameBufferObject fboLevel;
    private boolean addLight=true;
	private List<ShadowHull> cHulls;
	
	public Level(String mapFileName) throws SlickException {
		lights = new ArrayList<LightSource>();
        cHulls = new ArrayList<ShadowHull>();
        
		init(mapFileName);
	}
	
	public void init(String mapFileName) throws SlickException {
		this.map = new TiledMap("/data/maps/" + mapFileName);
		this.player = new Player(100, 0, map);
		
		fboLight = new FrameBufferObject(new Point(1280, 720));
		fboLevel = new FrameBufferObject(new Point(1280, 720));
		
		LightSource l = new LightSource(new Vec2(400, 400), 900, 0, new Color(170, 170, 170));
		lights.add(l);
		//l.setFlicker(0, 1);
		
		addHull();
		
        for(int i=0; i<30; i++) {
            //addRandomHull();
        }
        //Create lights
        for(int i=0; i<4; i++) {
            //addRandomLight();
        }
	}
	
	public void addHull() {
		Random gen = new Random();
        Vec2 points[] = {new Vec2(0, 0), new Vec2(20, 0), new Vec2(20, 20), new Vec2(0, 20)};
        ShadowHull hull = new ShadowHull(new Vec2(650, 300), Arrays.asList(points), 0.1f, Color.red);
        //hull.rotate(1, 10, 10);
        
        cHulls.add(hull);
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		player.update(container, delta);
		Hud.getInstance().update(delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		renderScene(container, g);
        renderLevel(container, g);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
        
        fboLight.render(1.0f);
		
		Hud.getInstance().render(container, g);
	}
	
	public void renderLevel(GameContainer container, Graphics g) throws SlickException {
		fboLevel.enable();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
		
		//GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		map.render(0, 0);
		player.render(container, g);
		
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
                for (LightSource light1: lights) {
                    light1.render();
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
    
    private void addRandomHull() {
        Random gen = new Random();
        Vec2 points[] = {new Vec2(0, 0), new Vec2(20, 0), new Vec2(20, 20), new Vec2(0, 20)};
        cHulls.add(new ShadowHull(new Vec2(gen.nextInt(1024), gen.nextInt(768)), Arrays.asList(points), 0.1f, Color.white));
    }

    private void addRandomLight() {
        Random gen = new Random();
        Color c = new Color(gen.nextFloat(), gen.nextFloat(), gen.nextFloat());
        lights.add(new LightSource(new Vec2(gen.nextInt(1024), gen.nextInt(768)), 200.0f, 0.0f, c));
    }
}
