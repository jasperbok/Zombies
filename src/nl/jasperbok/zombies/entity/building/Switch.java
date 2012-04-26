package nl.jasperbok.zombies.entity.building;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

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
	
	private Image switchedOnImage;
	private Image switchedOffImage;
	
	public Switch onOffSwitch;
	
	public Switch(Level level, boolean initialState, Switch onOffSwitch, HashMap<String, String> settings) throws SlickException {
		this(level, initialState, settings);
		
		this.onOffSwitch = onOffSwitch;
		this.onOffSwitch.registerObserver(this);
		this.canBeUsed = false;
	}

	public Switch(Level level, boolean initialState, HashMap<String, String> settings) throws SlickException {
		super.init(level);
		this.settings = settings;
		this.switchedOnImage = new Image("/data/sprites/entity/building/buildings.png").getSubImage(0, 121, 29, 84);
		this.switchedOffImage = new Image("/data/sprites/entity/building/buildings.png").getSubImage(29, 121, 29, 84);
		this.state = initialState;
		this.useBox = new Rectangle(this.position.x, this.position.y, switchedOnImage.getWidth(), switchedOnImage.getHeight());
	}

	/**
	 * Switches the state of the Switch.
	 * 
	 * @param user The Entity that uses the Switch.
	 */
	public void use(Entity user) {
		if (canBeUsed) {
			if (
					this.settings.get("requires") == "" ||
					(user.inventory.contains(this.settings.get("requires")))
				) {
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
		}
	}
	
	public void update(Input input, int delta) {
		//System.out.println("Switch "+ this.name +" pos: " + this.position.y);
		//System.out.println("Switch "+ this.name +" vel: " + this.velocity.y);
		super.update(input, delta);
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
			canBeUsed = true;
		} else if (message == "off") {
			canBeUsed = false;
		}
	}

	@Override
	public void registerObserver(Observer observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterObserver(Observer observer) {
		// TODO Auto-generated method stub
		
	}
}
