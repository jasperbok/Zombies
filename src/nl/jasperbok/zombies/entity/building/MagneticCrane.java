package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.gui.Notifications;
import nl.jasperbok.zombies.level.Level;
import nl.jasperbok.zombies.math.Vector2;

public class MagneticCrane extends Entity implements Usable {
	
	public MagneticCrane(Level level) {
		super.init(level);
	}

	public void use(Entity user) {
		
	}
	
	public boolean canBeUsed(Rectangle rect) {
		return false;
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
		
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		
	}
}
