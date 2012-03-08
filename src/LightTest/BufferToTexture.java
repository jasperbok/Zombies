/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LightTest;

import java.awt.Point;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Ciano
 */
public class BufferToTexture {

    private Integer buffer, tex;
    private boolean do_clear_buffer;
    private Point size;

    public BufferToTexture(Integer buffer, boolean do_clear_buffer) {
        this.buffer = (buffer!=null ? buffer: GL11.GL_BACK);
        this.do_clear_buffer = do_clear_buffer;
        this.tex = null;
    }

    public int next_poweroftwo(int n) {
        return (int) Math.pow(2, Math.ceil(Math.log(n) / Math.log(2)));
    }

    public void createTexture() {
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        int x = viewport.get(0); int y =viewport.get(1); int w=viewport.get(2); int h=viewport.get(3);
        size = new Point(next_poweroftwo(w),next_poweroftwo(h));
        tex = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    }

    public void enable() {
        if(tex==null) createTexture();
    }

    public void disable() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        GL11.glReadBuffer(buffer);
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, 0, 0, size.x, size.y, 0);
        if (do_clear_buffer) GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void render(float alpha) {
        if(tex!=null) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex2f(0.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex2f(size.x, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex2f(size.x, size.y);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex2f(0.0f, size.y);
            GL11.glEnd();
        }
    }


}