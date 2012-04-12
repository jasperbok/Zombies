package nl.jasperbok.zombies.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import LightTest.ConvexHull;
import LightTest.FrameBufferObject;
import LightTest.Light;
import LightTest.Vec2;

import nl.timcommandeur.zombies.light.FlashLight;
import nl.timcommandeur.zombies.light.LightSource;
import nl.timcommandeur.zombies.light.ShadowHull;
import nl.timcommandeur.zombies.screen.Camera;

import nl.jasperbok.zombies.gui.Hud;

public class Level {
	public Vector2f gravity = new Vector2f(0.0f, 0.002f);
	public Camera camera;
	
	public TileEnvironment env;
	
	// Lighting
	private boolean doLighting = false;
    public static List<LightSource> lights;
    protected float intensity = 1.0f;
    protected FrameBufferObject fboLight;
    protected FrameBufferObject fboLevel;
    protected boolean addLight=true;
	protected List<ShadowHull> cHulls;
	public FlashLight fl;
	protected int rot = 0;
	
	public Level(String mapFileName) throws SlickException {
		env = new TileEnvironment(mapFileName, gravity, this);
		camera = Camera.getInstance();
		
		lights = new ArrayList<LightSource>();
        cHulls = new ArrayList<ShadowHull>();
        fboLight = new FrameBufferObject(new Point(1280, 720));
		fboLevel = new FrameBufferObject(new Point(1280, 720));
		fl = new FlashLight(lights, cHulls, new Vector2f(200, 200), camera);
		lights.add(new LightSource(new Vector2f(200, 200), 200, 0, new Color(150, 0, 0), camera));
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		Hud.getInstance().update(delta);
		//fl.setPos(new Vec2(player.position.x + 10 + camera.position.x, player.position.y + 10 - camera.position.y));
		fl.setPosition(env.getPlayer().position.x + 10, env.getPlayer().position.y + 10);
		fl.point(new Vec2(container.getInput().getAbsoluteMouseX(), container.getInput().getAbsoluteMouseY()));
		//fl.pointToMouse(container);
		//System.out.println(container.getInput().getAbsoluteMouseX() + camera.position.x);
		
		camera.position.x = env.getPlayer().position.x;
		camera.position.y = env.getPlayer().position.y;
		
		/*System.out.println("player.position.y: " + env.getPlayer().position.y);
		System.out.println("player.renderPosition.y: " + env.getPlayer().renderPosition.y);
		System.out.println("camera.position.y: " + camera.position.y);*/
		
		env.update(container, delta);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		//camera.translate(g);
		
		if (doLighting) renderScene(container, g);
        renderLevel(container, g);
        
        if (doLighting) {
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
	        
	        fboLight.render(1.0f);
			
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_COLOR);
        }
        
		Hud.getInstance().render(container, g);
		
		//g.resetTransform();
	}
	
	public void renderLevel(GameContainer container, Graphics g) throws SlickException {
		if (doLighting) {
			fboLevel.enable();
			
			GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glEnable(GL11.GL_BLEND);
			
			//GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		env.render(container, g);
		
		if (doLighting) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
	        GL11.glDisable(GL11.GL_BLEND);
		}
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
  
}
