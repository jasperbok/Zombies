package nl.jasperbok.zombies.entity.building;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Observable;
import nl.jasperbok.zombies.entity.Observer;
import nl.jasperbok.zombies.entity.Usable;
import nl.jasperbok.zombies.level.Level;

public class Switch extends Entity implements Usable, Observable {
	
	/**
	 * The state of the Switch. True = on, False = off.
	 */
	private boolean state;
	private Rectangle useBox;
	private ArrayList<Observer> observers;
	
	private Image switchedOnImage;
	private Image switchedOffImage;
	
	public Switch(Level level, Vector2f position) throws SlickException {
		this(level, false, position);
	}

	public Switch(Level level, boolean initialState, Vector2f position) throws SlickException {
		super.init(level);
		this.gravityAffected = false;
		this.position = position;
		this.switchedOnImage = new Image("/data/sprites/entity/building/buildings.png").getSubImage(0, 121, 29, 84);
		this.switchedOffImage = new Image("/data/sprites/entity/building/buildings.png").getSubImage(29, 121, 29, 84);
		this.state = initialState;
		this.observers = new ArrayList<Observer>();
		this.useBox = new Rectangle(position.getX(), position.getY(), switchedOnImage.getWidth(), switchedOnImage.getHeight());
	}
	
	/**
	 * Register an object that implements the Observer interface as an
	 * observer at this Switch.
	 * 
	 * @param observer The Observer object.
	 */
	public void registerObserver(Observer observer) {
		this.observers.add(observer);
		if (this.state == true) {
			observer.notify(this, "on");
		} else {
			observer.notify(this, "off");
		}
	}
	
	/**
	 * Unregister an Observer.
	 * 
	 * @param observer The Observer.
	 */
	public void unregisterObserver(Observer observer) {
		this.observers.remove(observer);
	}

	/**
	 * Switches the state of the Switch.
	 * 
	 * @param user The Entity that uses the Switch.
	 */
	public void use(Entity user) {
		this.state = !this.state;
		for (Observer observer: observers) {
			if (this.state == true) {
				observer.notify(this, "on");
			} else {
				observer.notify(this, "off");
			}
		}
	}

	/**
	 * Checks whether the given Rectangle intersects with the useBox
	 * of this Switch.
	 * 
	 * @param rect The Rectangle to check.
	 */
	public boolean canBeUsed(Rectangle rect) {
		return rect.intersects(useBox);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (state) {
			switchedOnImage.draw((int)renderPosition.getX(), (int)renderPosition.getY());
		} else {
			switchedOffImage.draw((int)renderPosition.getX(), (int)renderPosition.getY());
		}
	}
}
