/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LightTest;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Ciano
 */
public class LightTest extends BasicGame {

    private List<ConvexHull> cHulls;
    public static List<Light> lights;
    private boolean addLight=true;
    private float intensity = 1.0f;
    private FrameBufferObject fbo;

    public LightTest(String name) {
        super(name);
        lights = new ArrayList<Light>();
        cHulls = new ArrayList<ConvexHull>();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AppGameContainer contain = new AppGameContainer(new LightTest("Game"));
            contain.setDisplayMode(1024, 768, false);
            contain.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        fbo = new FrameBufferObject(new Point(1024, 768));
        for(int i=0; i<30; i++) {
            addRandomHull();
        }
        //Create lights
        for(int i=0; i<4; i++) {
            addRandomLight();
        }
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

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        Input input = container.getInput();
        
        //Randomize light intensity (light flickers)
        intensity+=(new Random().nextFloat()-0.5f)/4.0f;
        intensity=Math.min(2.0f, Math.max(-1.0f, intensity));

        //Move last light in list around with mouse
        if(lights.size()>0) lights.get(lights.size()-1).setPos(new Vec2(input.getMouseX(), input.getMouseY()));

        //Key Commands
        if(input.isKeyPressed(Input.KEY_EQUALS)) intensity+=0.2f;
        if(input.isKeyPressed(Input.KEY_MINUS)) intensity-=0.2f;
        if(input.isKeyPressed(Input.KEY_F)) ConvexHull.toggleRenderShadowFins();
        if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) addRandomLight();
        if(input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) addRandomHull();
        
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
        renderScene(g);
        System.out.println(Integer.toString(container.getFPS()));
    }

    public void renderScene(Graphics g)
    {
        //# Clear the color buffer
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        //# Use less-than or equal depth testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        fbo.enable();
        
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
            for (ConvexHull hull: cHulls) {
                hull.render();
            }
        }

        fbo.disable();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        //# Render the fbo on top of the color buffer
        fbo.render(1.0f);

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
