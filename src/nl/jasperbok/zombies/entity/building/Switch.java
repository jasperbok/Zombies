package nl.jasperbok.zombies.entity.building;

import java.util.ArrayList;
import java.util.HashMap;

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

public class Switch extends Entity implements Usable, Observable, Observer {
	
	/**
	 * The state of the Switch. True = on, False = off.
	 */
	private boolean state;
	private boolean canBeUsed = true;
	private Rectangle useBox;
	private ArrayList<Observer> observers;
	
	private Image switchedOnImage;
	private Image switchedOffImage;
	
	public Switch onOffSwitch;
	
	public Switch(Level level, Vector2f position) throws SlickException {
		this(level, false, position, new HashMap<String, String>());
	}
	
	public Switch(Level level, boolean initialState, Vector2f position, Switch onOffSwitch, HashMap<String, String> settings) throws SlickException {
		this(level, initialState, position, settings);
		
		this.onOffSwitch = onOffSwitch;
		this.onOffSwitch.registerObserver(this);
		this.canBeUsed = false;
	}

	public Switch(Level level, boolean initialState, Vector2f position, HashMap<String, String> settings) throws SlickException {
		super.init(level);
		this.settings = settings;
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
		if (canBeUsed) {
			this.state = !this.state;
			if (this.settings.get("target") != "") {
				try {
					if (this.state == true) {
						((Observer)this.level.env.getEntityByName(this.settings.get("target"))).notify(this, "on");
					} else {
						((Observer)this.level.env.getEntityByName(this.settings.get("target"))).notify(this, "off");
					}
				}
				finally {
					
				}
			}
		}
		/*
		for (Observer observer: observers) {
			if (this.state == true) {
				observer.notify(this, "on");
			} else {
				observer.notify(this, "off");
>>>>>>> d97f605c40ec0b41e74d318437fc8b923c0f7a28
			}
		}
		*/
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

	public void notify(Observable observable, String message) {
		if (message == "on") {
			System.out.println(this.getClass().toString() + ".notify: canbeused");
			canBeUsed = true;
		} else if (message == "off") {
			canBeUsed = false;
		}
	}
}
