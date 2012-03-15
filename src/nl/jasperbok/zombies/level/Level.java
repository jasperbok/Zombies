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
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import LightTest.ConvexHull;
import LightTest.FrameBufferObject;
import LightTest.Light;
import LightTest.Vec2;

import nl.timcommandeur.zombies.light.FlashLight;
import nl.timcommandeur.zombies.light.LightSource;
import nl.timcommandeur.zombies.light.ShadowHull;
import nl.timcommandeur.zombies.screen.Camera;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.gui.Hud;
import nl.jasperbok.zombies.level.Block;
import nl.jasperbok.zombies.math.Vector2;

public class Level {
	public Player player;
	public TiledMap map;
	
	public Camera camera;
	
	protected List<Entity> entities;
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
	
	public Level(String mapFileName) throws SlickException {
		lights = new ArrayList<LightSource>();
        cHulls = new ArrayList<ShadowHull>();
        
		init(mapFileName);
		
		map = new TiledMap("/data/maps/" + mapFileName);
		player = new Player(100, 0, map, this);
		entities = new ArrayList<Entity>();
		usableObjects = new ArrayList<Usable>();
		entities.add(player);
		
		for (int i = 0; i < map.getHeight(); i++) {
			for (int j = 0; j < map.getWidth(); j++) {
				entities.add(new Block(i, j, map.getTileId(j, i, 0), map.getTileHeight(), map));
			}
		}
	}
	
	public void init(String mapFileName) throws SlickException {
		camera = new Camera();
		
		fboLight = new FrameBufferObject(new Point(1280, 720));
		fboLevel = new FrameBufferObject(new Point(1280, 720));
		
		fl = new FlashLight(lights, cHulls, new Vec2(200, 200));
		lights.add(new LightSource(new Vec2(200, 200), 200, 0, new Color(150, 0, 0)));
	}
	
	public String movingStatus(Entity ent) {
		String status = "falling"; // Falling by default.
		Rectangle box = ent.boundingBox;
		Vector2 vel = ent.velocity;
		
		// Always create a bottom Rectangle to see if ground moves underneath.
		Rectangle bottomSide = new Rectangle(box.getMinX(), box.getMaxY() - 2, box.getWidth(), 2);
		Rectangle below = new Rectangle(box.getMinX(), box.getMaxY(), box.getWidth(), 1);
		
		if (vel.y < 0) {
			Rectangle topSide = new Rectangle(box.getMinX(), box.getMinY(), box.getWidth(), 2);
		}
		
		if (vel.x < 0) {
			Rectangle leftSide = new Rectangle(box.getMinX(), box.getMinY(), 2, box.getHeight());
		} else if (vel.x > 0) {
			Rectangle rightSide = new Rectangle(box.getMaxX() - 2, box.getMinY(), 2, box.getHeight());
		}
		
		// Check whether the Entity is standing on something solid.
		for (Entity currEnt: entities) {
			if (currEnt.isBlocking) {
				if (currEnt.boundingBox.intersects(below)) {
					status = "standing";
					break;
				}
			}
		}
		
		return status;
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
	
	/**
	 * Checks for collisions between one Entity and others in the level.
	 * 
	 * @param ent	The Entity to check hits for.
	 * @return		An ArrayList with Entities that intersect with ent.
	 */
	public ArrayList<Entity> touchingSolidObject(Entity ent) {
		ArrayList<Entity> hits = new ArrayList<Entity>();
		for (Entity ent2: entities) {
			if (ent == ent2) continue;
			if (ent.boundingBox.intersects(ent2.boundingBox)) {
				hits.add(ent2);
			}
		}
		return hits;
	}
	
	/**
	 * 
	 * @param ent1 The entity to check the collisions for.
	 * @param ent2 The entity to check the collisions against.
	 * @return A boolean list, the order is top, right, down, left. True
	 * tells there is an intersect.
	 */
	public boolean[] findIntersects(Entity ent1, Entity ent2) {
		boolean[] sides = new boolean[3];
		for (int i = 0; i < 4; i++) {
			sides[i] = false;
		}
		if (ent2.boundingBox.contains(ent1.boundingBox.getCenterX(), ent1.boundingBox.getMinY())) sides[0] = true;
		if (ent2.boundingBox.contains(ent1.boundingBox.getMaxX(), ent1.boundingBox.getCenterY())) sides[0] = true;
		if (ent2.boundingBox.contains(ent1.boundingBox.getCenterX(), ent1.boundingBox.getMaxY())) sides[0] = true;
		if (ent2.boundingBox.contains(ent1.boundingBox.getMinX(), ent1.boundingBox.getCenterY())) sides[0] = true;
		return sides;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		player.update(container, delta);
		Hud.getInstance().update(delta);
		fl.setPos(new Vec2(player.position.x + 10 + camera.position.x, player.position.y + 10 - camera.position.y));
		//fl.point(new Vec2(container.getInput().getAbsoluteMouseX() + camera.position.x, container.getInput().getAbsoluteMouseY()));
		fl.pointToMouse(container);
		//System.out.println(container.getInput().getAbsoluteMouseX() + camera.position.x);

		camera.position.x = player.position.x - 600;
		camera.position.y = player.position.y - 600;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		camera.translate(g);
		
		renderScene(container, g);
        renderLevel(container, g);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
        
        fboLight.render(1.0f);
		
		Hud.getInstance().render(container, g);
		
		g.resetTransform();
	}
	
	public void renderLevel(GameContainer container, Graphics g) throws SlickException {
		fboLevel.enable();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
		
		//GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_COLOR);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		map.render(0, 0);
		for (Entity ent: entities) {
			ent.render(container, g);
		}
		
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
