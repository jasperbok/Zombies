/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LightTest;

import LightTest.ShaderObjects.Program;

/**
 *
 * @author Ciano
 */
public class PenumbraShader {

    private static PenumbraShader penumbraShader;
    public static PenumbraShader getPenumbraShader() {
        if(penumbraShader==null) penumbraShader = new PenumbraShader();
        return penumbraShader;
    }
    private static String vertexShader =
            "varying vec2 pos; \n" +
            "void main() \n" +
            "{ \n" +
                "pos = gl_Vertex.xy; \n" +
                "gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_Vertex.xy, 0.0, 1.0); \n" +
            "}";
    private static String fragmentShader = 
            "uniform vec2 origin; \n" +
            "uniform vec2 inner; \n" +
            "uniform float angle; \n" +
            "uniform float inner_intensity; \n" +
            "uniform float outer_intensity; \n" +
            "varying vec2 pos; \n" +
            "void main() \n" +
            "{ \n" +
                "float a = acos(dot(normalize(pos - origin), normalize(inner))) / angle; \n" +
                "a = (outer_intensity - inner_intensity) * a + inner_intensity; \n" +
                "a = 1.0 / (1.0 + exp(-(a*12.0 - 6.0))); \n" +
                "gl_FragColor = vec4(a, a, a, a); \n" +
            "} \n";

    private Program program;

    private PenumbraShader() {
    }

    public void createProgram() {
        program = new Program(vertexShader, fragmentShader);
    }

    public void enable() {
        if(program==null) createProgram();
        program.enable();
    }

    public void disable() {
        program.disable();
    }

    public void setState(Vec2 origin, float angle, Vec2 innerVec, float innerIntensity, float outerIntensity) {
        program.setUniformf("origin", new float[] {origin.x, origin.y});
        program.setUniform1f("angle", angle);
        program.setUniformf("inner", new float[] {innerVec.x, innerVec.y});
        program.setUniform1f("inner_intensity", innerIntensity);
        program.setUniform1f("outer_intensity", outerIntensity);
    }

}
