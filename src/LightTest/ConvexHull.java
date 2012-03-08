/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LightTest;

import java.util.List;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

/**
 *
 * @author Ciano
 */
public class ConvexHull {

    protected List<Vec2> points;
    protected Vec2 pos;
    
    private float depth;
    private Color color;
    private static boolean renderShadowFins=true;

    private Boolean lastNodeFrontfacing;

    public ConvexHull() {
    }

    public ConvexHull(Vec2 pos, List<Vec2> lPoints, float depth, Color c) {
        this.points = new LoopingList<Vec2>();
        this.pos = pos;
        this.depth = depth;
        this.color = c;
        for(Vec2 v: lPoints) {
            this.points.add(new Vec2(v.x + pos.x, v.y + pos.y));
        }
    }

    public void drawShadowGeometry(Light light) {
        //# Calculate all the front facing sides
        Integer first = null, last = null;
        lastNodeFrontfacing = null;
        for (int x=-1; x<points.size(); x++) {
//        for x in xrange(-1, len(self.points), 1) {
            Vec2 current_point = points.get(x);
            Vec2 prev_point = points.get(x-1);

            Vec2 nv = new Vec2(current_point.y - prev_point.y, current_point.x - prev_point.x);
            Vec2 lv = new Vec2(current_point.x - light.getPos().x, current_point.y - light.getPos().y);

            //# Check if the face is front-facing
            if ((nv.x * -1.0f * lv.x) + (nv.y * lv.y) > 0) {
                if (lastNodeFrontfacing!=null && !lastNodeFrontfacing) last = points.indexOf(prev_point);
                lastNodeFrontfacing = true;
            }
            else {
                if (lastNodeFrontfacing!=null && lastNodeFrontfacing) first = points.indexOf(prev_point);
                lastNodeFrontfacing = false;
            }
        }

        if (first == null || last == null) {
            //# The light source is inside the object
            return;
        }

        //# Create shadow fins
        Object[] startFs = create_shadowfins(light, first, 1);
        List<ShadowFin> startFins = (List<ShadowFin>) startFs[0];
        first = (Integer) startFs[1]; Vec2 first_vector = (Vec2) startFs[2];
        Object[] endFs = create_shadowfins(light, last, -1);
        List<ShadowFin> endFins = (List<ShadowFin>) endFs[0];
        last = (Integer) endFs[1]; Vec2 last_vector = (Vec2) endFs[2];

        //# Render shadow fins
        if(renderShadowFins) {
            for (ShadowFin fin: startFins) {
                fin.render();
            }
            for (ShadowFin fin: endFins) {
                fin.render();
            }
        }

        //# Get a list of all the back edges
        List<Vec2> backpoints = new LoopingList<Vec2>();
        for (int x=first; x<first+points.size(); x++) {
            backpoints.add(0, points.get((x)%(points.size())));
            if (x%(points.size()) == last) break;
        }

        //# Figure out the length of the back edges. We'll use this later for
        //# weighted average between the shadow fins to find our umbra vectors.
        List<Float> back_length = new LoopingList<Float>();
        back_length.add(0.0f);
        float sum_back_length = 0;

        for (int x=1; x<backpoints.size(); x++) {
            float l = from_points(backpoints.get(x - 1), backpoints.get(x)).length();
            back_length.add(0, Float.valueOf(l));
            sum_back_length += l;
        }

        //# Draw the shadow geometry using a triangle strip
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        float a = 0;

        for (int x=0; x<backpoints.size(); x++) {
            Vec2 point = backpoints.get(x);
            GL11.glVertex3f(point.x, point.y, depth);
            //# Draw our umbra using weighted average vectors
            if (x != backpoints.size() - 2) {
                GL11.glVertex3f(point.x + (first_vector.x * (a / sum_back_length)) + (last_vector.x * (1 - (a / sum_back_length))),
                        point.y + (first_vector.y * (a / sum_back_length)) + (last_vector.y * (1 - (a / sum_back_length))), depth);
            }
            else {
                GL11.glVertex3f(point.x + first_vector.x, point.y + first_vector.y, depth);
            }
            a += back_length.get(x);
        }
        GL11.glEnd();
    }


    public Object[] create_shadowfins(Light light, int origin, int step) {
        List<ShadowFin> shadowfins = new LoopingList<ShadowFin>();

        //# Go backwards to see if we need any shadow fins
        int i = origin;
        while(true) {
            Vec2 p1 = points.get(i);

            //# Make sure we wrap around
            i -= step;
            if (i < 0) i = points.size() - 1;
            else if (i == points.size()) i = 0;

            Vec2 p0 = points.get(i);

            Vec2 edge = from_points(p1, p0);
            edge.normalize();

            ShadowFin shadowfin = new ShadowFin(p0);
            shadowfin.setIndex(i);

            float angle = vecAngle(edge) - vecAngle(light.outerVector(p0, step));

            if (step == 1) {
                if (angle < 0 || angle > Math.PI * 0.5f) break;
            }
            else if (step == -1) {
            //# Make sure the angle is within the right quadrant
                if (angle > Math.PI) angle -= Math.PI * 2.0f;
                if (angle > 0 || angle < -Math.PI * 0.5f) break;
            }

            shadowfin.setOuter(light.outerVector(p0, step));
            shadowfin.setInner(edge.mul(light.innerVector(p0, step).length()));

            shadowfins.add(shadowfin);
            //#break
        }

        //# Go forwards and see if we need any shadow fins
        i = origin;
        while(true) {
            ShadowFin shadowfin = new ShadowFin(points.get(i));
            shadowfin.setIndex(i);

            shadowfin.setOuter(light.outerVector(points.get(i), step));
            shadowfin.setInner(light.innerVector(points.get(i), step));

            if(shadowfins.size() > 0) shadowfin.setOuter(shadowfins.get(0).getInner());

            Vec2 p0 = points.get(i);

            //# Make sure we wrap around
            i += step;
            if (i < 0) i = points.size() - 1;
            else if (i == points.size()) i = 0;

            Vec2 p1 = points.get(i);

            Vec2 edge = from_points(p1, p0);
            edge.normalize();

            boolean done = true;
            Vec2 penumbra = new Vec2(shadowfin.getOuter().x, shadowfin.getOuter().y);
            penumbra.normalize();
            Vec2 umbra = new Vec2(shadowfin.getInner().x, shadowfin.getInner().y);
            umbra.normalize();
            if (Math.acos(Vec2.dot(edge, penumbra)) < Math.acos(Vec2.dot(umbra, penumbra))) {
                shadowfin.setInner(edge.mul(light.outerVector(p0, step).length()));
                done = false;
            }
            shadowfins.add(0, shadowfin);

            if (done) break;
        }

        //# Get the total angle
        float sum_angles = 0;
        for(int x=0; x<shadowfins.size(); x++) {
            sum_angles += shadowfins.get(x).angle();
        }

        //# Calculate the inner and outer intensity of the shadowfins
        float angle = 0;
        for (int x=0; x<shadowfins.size(); x++) {
            shadowfins.get(x).setUmbraIntensity(angle / sum_angles);
            angle += shadowfins.get(x).angle();
            shadowfins.get(x).setPenumbraIntensity(angle / sum_angles);
        }

        //# We'll use these for our umbra generation
        if(shadowfins.size()>0)
        return new Object[] {shadowfins, shadowfins.get(0).getIndex(), shadowfins.get(0).getInner()};
        else return new Object[] {shadowfins, 1, new Vec2()};
    }

    public void render() {
        GL11.glColor4f(color.r, color.g, color.b, 1.0f);
        GL11.glBegin(GL11.GL_POLYGON);
        for(Vec2 point: points) {
            GL11.glVertex3f(point.x, point.y, depth);
        }
        GL11.glEnd();
    }

    public Vec2 from_points(Vec2 fromPt, Vec2 toPt) {
        return new Vec2(fromPt.x - toPt.x, fromPt.y - toPt.y);
    }

    public float vecAngle(Vec2 v) {
        return (float) Math.atan2(v.y, v.x);
    }

    public static void toggleRenderShadowFins() {
        ConvexHull.renderShadowFins = !ConvexHull.renderShadowFins;
    }


}
