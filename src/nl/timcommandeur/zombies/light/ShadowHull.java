package nl.timcommandeur.zombies.light;

import java.util.List;

import org.newdawn.slick.Color;

import LightTest.ConvexHull;
import LightTest.LoopingList;
import LightTest.Vec2;

public class ShadowHull extends ConvexHull {
	
	public List<Vec2> shapePoints;
	
	protected int screenHeight = 720;
	
	public ShadowHull(Vec2 vec2, List<Vec2> asList, float f, Color red) {
		super(vec2, asList, f, red);
		
		shapePoints = new LoopingList<Vec2>();
		
		for(Vec2 v: asList) {
            shapePoints.add(new Vec2(v.x, v.y));
        }
		
		setPos(vec2);
	}
	
	public void setPos(Vec2 newPos) {
		pos = new Vec2(newPos.x, (newPos.y * -1) + screenHeight);
		
		List<Vec2> newPoints = new LoopingList<Vec2>();
		
		for (Vec2 point : shapePoints) {
			
			float newPointX = point.x + pos.x;
			float newPointY = point.y + pos.y;
			
			newPoints.add(new Vec2(Math.round(newPointX), Math.round(newPointY)));
		}
		
		points = newPoints;
		
		//System.out.println(pos.x + " | " + pos.y);
	}

	public void rotate(float angle, int originX, int originY)
	{
		/*
			x = ((x - x_origin) * cos(angle)) - ((y_origin - y) * sin(angle))
			y = ((y_origin - y) * cos(angle)) - ((x - x_origin) * sin(angle))
		*/
		
		int x_origin = (int) originX;
		int y_origin = (int) originY;
		angle = (float) (angle * Math.PI) / 180;
		
		//System.out.println("x: " + x_origin + " y: " + y_origin);
		
		List<Vec2> newPoints = new LoopingList<Vec2>();
		
		for (Vec2 point : shapePoints) {
			
			float newPointX = (float) (((point.x - x_origin) * Math.cos(angle)) - ((y_origin - point.y) * Math.sin(angle)));
			float newPointY = (float) -(((y_origin - point.y) * Math.cos(angle)) + ((point.x - x_origin) * Math.sin(angle)));
			newPointX = newPointX + pos.x;
			newPointY = newPointY + pos.y;
			
			newPoints.add(new Vec2(Math.round(newPointX), Math.round(newPointY)));
			
			//System.out.println(newPointX + " " + newPointY);
		}
		
		points = newPoints;
	}
}
