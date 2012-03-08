/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LightTest;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author Ciano
 */
public class ShadowFin {

    private PenumbraShader shader;
    private Vec2 rootPos, inner, outer;
    private float penumbraIntensity, umbraIntensity, depth;
    private Integer index;

    public ShadowFin(Vec2 rootPosition) {
        this.rootPos = rootPosition;
        this.outer = null;
        this.penumbraIntensity = 1.0f;
        this.inner = null;
        this.umbraIntensity = 0.0f;
        this.depth = 0.0f;
    }

    public float angle() {
        Vec2 uv = new Vec2(inner.x, inner.y);
        Vec2 pv = new Vec2(outer.x, outer.y);
        uv.normalize();
        pv.normalize();
        return (float) Math.acos(Vec2.dot(uv, pv));
    }

    public void render() {
        shader = PenumbraShader.getPenumbraShader();
        shader.enable();
        shader.setState(rootPos, angle(), inner, umbraIntensity, penumbraIntensity);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex3f(rootPos.x, rootPos.y, depth);
        GL11.glVertex3f(rootPos.x + outer.x, rootPos.y + outer.y, depth);
        GL11.glVertex3f(rootPos.x + inner.x, rootPos.y + inner.y, depth);
        GL11.glEnd();
        shader.disable();
    }

    public void setInner(Vec2 inner) { this.inner = inner; }
    public void setOuter(Vec2 outer) { this.outer = outer; }
    public void setPenumbraIntensity(float penumbraIntensity) { this.penumbraIntensity = penumbraIntensity; }
    public void setUmbraIntensity(float umbraIntensity) { this.umbraIntensity = umbraIntensity; }
    public void setIndex(int index) { this.index = index; }
    public int getIndex() { return index; }
    public Vec2 getInner() { return inner; }
    public Vec2 getOuter() { return outer; }


    
}
