package nl.jasperbok.zombies.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import LightTest.ConvexHull;
import LightTest.FrameBufferObject;
import LightTest.Light;
import LightTest.Vec2;

import nl.timcommandeur.zombies.light.FlashLight;
import nl.timcommandeur.zombies.light.LightSource;
import nl.timcommandeur.zombies.light.ShadowHull;
import nl.timcommandeur.zombies.screen.Camera;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.environment.Environment;
import nl.jasperbok.zombies.level.environment.MapLoader;
import nl.jasperbok.zombies.level.environment.TileEnvironment;

public class Level {
	private static int ID;
	
	// The environment in which the level takes place.
	public Environment env;
	// The player character.
	public Player player;
	// The map file name.
	public String mapFileName;
	// The camera.
	public Camera camera;
	
	private int totalDelta = 0;
	private int controlInterval = 50;
	private boolean showBounds = false;
	
	/* All the usable objects in the level. */
	protected List<Usable> usableObjects;
	
	// Lighting
    public static List<LightSource> lights;
    protected float intensity = 1.0f;
    protected FrameBufferObject fboLight;
    protected FrameBufferObject fboLevel;
    protected boolean addLight=true;
	protected List<ShadowHull> cHulls;
	
	protected FlashLight fl;
	protected int rot = 0;
	
	/**
	 * Level constructor.
	 * 
	 * @param mapFileName The name of the map file (without the .tmx extension).
	 * @throws SlickException
	 */
	public Level(String mapFileName) throws SlickException {
		lights = new ArrayList<LightSource>();
        cHulls = new ArrayList<ShadowHull>();
		
		this.mapFileName = mapFileName;
		player = new Player(100, 0, 200f, 300f, 50f, 4f);
		usableObjects = new ArrayList<Usable>();
		camera = new Camera();
		
		fboLight = new FrameBufferObject(new Point(1280, 720));
		fboLevel = new FrameBufferObject(new Point(1280, 720));
		
		fl = new FlashLight(lights, cHulls, new Vec2(200, 200));
		lights.add(new LightSource(new Vec2(200, 200), 200, 0, new Color(150, 0, 0)));
		
		restart();
	}
	
	/**
	 * Restarts the level.
	 * 
	 * @throws SlickException
	 */
	private void restart() throws SlickException {
		MapLoader loader = new MapLoader(mapFileName);
		TileEnvironment env = loader.load();
		env.setImageSize(32, 32);
		env.init();
		env.addEntity(player);
		this.env = env;
	}
	
	/**
	 * Returns an Entity implementing the Usable interface that's located
	 * within the given Rectangle.
	 * 
	 * @param	rect	a Rectangle.
	 * @return			The Entity within the given rect, or null if no
	 * 					Entity was found.
	 */
	public Usable findUsableObject(Rectangle rect) {
		for (Usable obj: usableObjects) {
			if (obj.canBeUsed(rect)) {
				return obj;
			}
		}
		return null;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		totalDelta += delta;
		env.update(delta);
		player.update(container, delta);
		Hud.getInstance().update(delta);
		fl.setPos(new Vec2(player.getX() + 10 + camera.position.x, player.getY() + 10 - camera.position.y));
		//fl.point(new Vec2(container.getInput().getAbsoluteMouseX() + camera.position.x, container.getInput().getAbsoluteMouseY()));
		fl.pointToMouse(container);
		//System.out.println(container.getInput().getAbsoluteMouseX() + camera.position.x);

		camera.position.x = player.getX() - 600;
		camera.position.y = player.getY() - 600;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		camera.translate(g);
		
		renderScene(container, g);
		renderLevel(container, g);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
        
        fboLight.render(1.0f);
		
		g.resetTransform();
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_COLOR);
        
		Hud.getInstance().render(container, g);
	}
	
	public void renderLevel(GameContainer container, Graphics g) throws SlickException {
		fboLevel.enable();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
		
		//GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		env.render(g);
        if (showBounds) env.renderBounds(g);
        
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
}
