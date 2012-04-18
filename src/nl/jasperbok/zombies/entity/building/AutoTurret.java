package nl.jasperbok.zombies.entity.building;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Observer;
import nl.jasperbok.zombies.entity.Observable;
import nl.jasperbok.zombies.level.Level;

public class AutoTurret extends Entity implements Observer {

	/**
	 * A reference to the Switch that controls this AutoTurret.
	 */
	private Switch onOffSwitch;
	/**
	 * Indicates whether the AutoTurret is facing left or right.
	 */
	private boolean facingLeft;
	/**
	 * Indicates whether the AutoTurret is firing.
	 */
	private boolean firing;
	/**
	 * Delta time between successive shots.
	 */
	private int fireRate = 50;
	/**
	 * Delta counter that counts the time since the last shot.
	 */
	private int lastShotFired = 0;
	/**
	 * The Image representing the AutoTurret's on state.
	 */
	private Image turretOnImage;
	/**
	 * The Image representing the AutoTurret's off state.
	 */
	private Image turretOffImage;
	
	/**
	 * AutoTurret constructor.
	 * 
	 * @param level The Level this AutoTurret is part of.
	 * @param switch The Switch that controls this AutoTurret.
	 */
	public AutoTurret(Level level, boolean facingLeft, Switch onOffSwitch, Vector2f position) throws SlickException {
		super.init(level);
		this.gravityAffected = false;
		this.position = position;
		this.facingLeft = facingLeft;
		this.onOffSwitch = onOffSwitch;
		this.firing = false;
		this.turretOnImage = new Image("data/sprites/entity/building/buildings.png").getSubImage(135, 0, 135, 121);
		this.turretOffImage = new Image("data/sprites/entity/building/buildings.png").getSubImage(0, 0, 135, 121);
		
		this.onOffSwitch.registerObserver(this);
	}
	
	/**
	 * Switches the AutoTurret on or off (inverses its state).
	 */
	public void switchOnOrOff() {
		this.firing = !firing;
		this.lastShotFired = fireRate;
	}
	
	public void update(Input input, int delta) {
		lastShotFired += delta;
		
		if (firing) {
			if (lastShotFired >= fireRate) {
				lastShotFired = 0;
				System.out.println("turret firing!");
			}
		}
	}
	
	public void notify(Observable observable, String message) {
		if (message == "on") {
			this.firing = true;
		} else if (message == "off") {
			this.firing = false;
		}
		System.out.println(message);
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (firing) {
			if (facingLeft) {
				turretOnImage.draw((int)renderPosition.getX(), (int)renderPosition.getY());
			} else {
				turretOnImage.getFlippedCopy(true, false).draw((int)renderPosition.getX(), (int)renderPosition.getY());
			}
		} else {
			if (facingLeft) {
				turretOffImage.draw((int)renderPosition.getX(), (int)renderPosition.getY());
			} else {
				turretOffImage.getFlippedCopy(true, false).draw((int)renderPosition.getX(), (int)renderPosition.getY());
			}
		}
	}
}
