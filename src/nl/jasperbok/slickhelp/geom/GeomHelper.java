package nl.jasperbok.slickhelp.geom;

import org.newdawn.slick.geom.Rectangle;

public class GeomHelper {
	/**
	 * Returns the smallest rectangle containing two rectangles.
	 * 
	 * @param rect1 A rectangle.
	 * @param rect2 Another rectangle.
	 * @return Rectangle The smalles rectangle containing the two given
	 * rectangles.
	 */
	public static Rectangle combineToRectangle(Rectangle rect1, Rectangle rect2) {
		float newWidth = 0;
		float newHeight = 0;
		
		if (rect1.getMinX() < rect2.getMinX()) {
			newWidth = rect1.getMinX() + rect2.getMaxX();
		} else if (rect1.getMinX() > rect2.getMinX()) {
			newWidth = rect2.getMinX() + rect1.getMaxX();
		} else {
			newWidth = Math.max(rect2.getWidth(), rect1.getWidth());
		}
		
		if (rect1.getMinY() < rect2.getMinY()) {
			newHeight = rect2.getMaxY() - rect1.getMinY(); 
		} else if (rect1.getMinY() > rect2.getMinY()) {
			newHeight = rect1.getMaxY() - rect2.getMinY();
		} else {
			newHeight = Math.max(rect1.getHeight(), rect2.getHeight());
		}
		
		return new Rectangle(
				Math.min(rect1.getMinX(), rect2.getMinX()),
				Math.min(rect1.getMinY(), rect2.getMinY()),
				newWidth,
				newHeight);
	}
}
