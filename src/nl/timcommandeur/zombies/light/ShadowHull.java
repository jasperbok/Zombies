package nl.timcommandeur.zombies.light;

import java.util.List;

import org.newdawn.slick.Color;

import LightTest.ConvexHull;
import LightTest.LoopingList;
import LightTest.Vec2;

public class ShadowHull extends ConvexHull {
	
	public ShadowHull(Vec2 vec2, List<Vec2> asList, float f, Color red) {
		super(vec2, asList, f, red);
	}

	public void rotate(int angle, int originX, int originY)
	{
		/*
			x = ((x - x_origin) * cos(angle)) - ((y_origin - y) * sin(angle))
			y = ((y_origin - y) * cos(angle)) - ((x - x_origin) * sin(angle))
		*/
		
		int x_origin = (int) originX;
		int y_origin = (int) originY;
		
		System.out.println("x: " + x_origin + " y: " + y_origin);
		
		List<Vec2> newPoints = new LoopingList<Vec2>();
		
		for (Vec2 point : points) {
			float oldPointX = point.x - pos.x;
			float oldPointY = point.y - pos.y;
			
			float newPointX = (float) (((oldPointX - x_origin) * Math.cos(angle)) - ((y_origin - oldPointY) * Math.sin(angle)));
			float newPointY = (float) -(((oldPointY) * Math.cos(angle)) + ((oldPointX - x_origin) * Math.sin(angle)));
			newPointX += pos.x;
			newPointY += pos.y;
			
			newPoints.add(new Vec2(newPointX, newPointY));
			
			System.out.println("x_origin: " + x_origin + " y_origin: " + y_origin);
			System.out.println("old");
			System.out.println("x: " + (oldPointX + pos.x) + " y: " + (oldPointY + pos.y));
			System.out.println("new");
			System.out.println("x: " + newPointX + " y: " + newPointY);
		}
		
		points = newPoints;
	}
}
