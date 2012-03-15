/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LightTest;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

/**
 *
 * @author Ciano
 */
public class Light {

    private Vec2 pos;
    private float radius, size, depth;
    private Color color;

    public Light(Vec2 pos, float radius, float depth, Color color) {
        this.pos = pos;
        this.radius = radius;
        this.depth = depth;
        this.color = color;
        this.size=10.0f;
    }

    public Vec2 outerVector(Vec2 edge, int step) {
        Vec2 cv = new Vec2(pos.x - edge.x, pos.y - edge.y);
        boolean useNegative = false;
        if(pos.x<edge.x) useNegative=true;

        Vec2 perpVec = new Vec2(pos.x - edge.x, pos.y - edge.y);
        perpVec.normalize();
        
        if (step == 1) {
            if(useNegative) {
                perpVec = perpVec.mul(-size);
                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
            }
            else {
                perpVec = perpVec.mul(size);
                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
            }
        }
        else {
            if(useNegative) {
                perpVec = perpVec.mul(-size);
                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
            }
            else {
                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
                perpVec = perpVec.mul(size);
            }
        }
        cv = new Vec2((pos.x + perpVec.x) - edge.x, (pos.y + perpVec.y) - edge.y);
        cv = cv.mul(-1.0f);

        cv.normalize();
        return cv.mul(radius*10.0f);
    }

    public Vec2 innerVector(Vec2 edge, int step) {
        Vec2 cv = new Vec2(pos.x - edge.x, pos.y - edge.y);
        boolean useNegative = false;
        if(pos.x<edge.x) useNegative=true;

        Vec2 perpVec = new Vec2(pos.x - edge.x, pos.y - edge.y);
        perpVec.normalize();

        if (step == 1) {
            if(useNegative) {
                perpVec = perpVec.mul(-size);
                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
            }
            else {
                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
                perpVec = perpVec.mul(size);
            }
        }
        else {
            if(useNegative) {
                perpVec = perpVec.mul(-size);
                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
            }
            else {
                perpVec = perpVec.mul(size);
                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
            }
        }
        cv = new Vec2((pos.x + perpVec.x) - edge.x, (pos.y + perpVec.y) - edge.y);
        cv = cv.mul(-1.0f);

        cv.normalize();
        return cv.mul(radius*10.0f);
    }

    public void renderSource() {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        //# Color
        GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
        GL11.glVertex3f(pos.x, pos.y, depth);

        float angle = 0;
        while (angle <= Math.PI * 2) {
            GL11.glVertex3f((float)(size * Math.cos(angle) + pos.x), (float) (size * Math.sin(angle) + pos.y), depth);
            angle += Math.PI * 2.0f / 12.0f;
        }
        GL11.glVertex3f(pos.x + size, pos.y, depth);

        GL11.glEnd();
    }

    public void render(float intensity) {
        //Begin Drawing
        LightShader shader = LightShader.getLightShader();
        shader.enable();
        shader.setState(color.brighter(intensity));

        GL11.glPushMatrix();
        GL11.glTranslatef(pos.x, pos.y, 0);
        GL11.glScalef(radius, radius, 0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(-1.0f, -1.0f, depth);
        GL11.glVertex3f(1.0f, -1.0f, depth);
        GL11.glVertex3f(1.0f, 1.0f, depth);
        GL11.glVertex3f(-1.0f, 1.0f, depth);
        GL11.glEnd();
        GL11.glPopMatrix();

        shader.disable();
        //# Remove this!
//        renderSource();
    }

    private Vec2 rotateVec(Vec2 vec, float rad) {
        //"""Rotate vector with radians."""
        float length = vec.length();
        if (vec.y != 0) rad += Math.asin(vec.y/length);
        else if (vec.x != 0) rad += Math.acos(vec.x/length);
        else {
            //# Null Vector
            return new Vec2();
        }
        return new Vec2((float)(length * Math.cos(rad)),(float)(length * Math.sin(rad)));
    }

    public Vec2 getPos() {
        return pos;
    }

    public void setPos(Vec2 pos) {
        this.pos = pos;
    }
    
    public void setColor(Color c) {
    	this.color = c;
    }

    public float getRadius() {
        return radius;
    }
}
