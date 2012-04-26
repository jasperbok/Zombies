package nl.jasperbok.zombies.entity.building;

import java.util.HashMap;

import org.newdawn.slick.Animation;
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
		this.zIndex = -2;
		Animation onAnim = new Animation();
		Animation offAnim = new Animation();
		onAnim.addFrame(new Image("/data/sprites/entity/building/buildings.png").getSubImage(0, 121, 29, 84), 5000);
		offAnim.addFrame(new Image("/data/sprites/entity/building/buildings.png").getSubImage(29, 121, 29, 84), 5000);
		this.anims.put("on", onAnim);
		this.anims.put("off", offAnim);
		this.currentAnim = this.anims.get("off");
		this.state = initialState;
		this.useBox = new Rectangle(this.position.x, this.position.y, this.currentAnim.getWidth(), this.currentAnim.getHeight());
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
				if (this.state) this.currentAnim = this.anims.get("on");
				if (!this.state) this.currentAnim = this.anims.get("off");
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
