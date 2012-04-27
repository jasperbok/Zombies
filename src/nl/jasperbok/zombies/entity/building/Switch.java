package nl.jasperbok.zombies.entity.building;

import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.gui.PlayerSpeech;
import nl.jasperbok.zombies.level.Level;

public class Switch extends Entity {
	
	/**
	 * The state of the Switch. True = on, False = off.
	 */
	private boolean state;
	private Rectangle useBox;
	private boolean firstUpdate = true;
	private boolean active = true;
	
	public Switch onOffSwitch;

	public Switch(Level level, boolean initialState, HashMap<String, String> settings) throws SlickException {
		super.init(level);
		this.settings = settings;
		this.zIndex = -2;
		this.active = "true".equals(this.settings.get("active"));
		
		Animation onAnim = new Animation();
		Animation offAnim = new Animation();
		onAnim.addFrame(new Image("/data/sprites/entity/building/buildings.png").getSubImage(0, 121, 29, 84), 5000);
		offAnim.addFrame(new Image("/data/sprites/entity/building/buildings.png").getSubImage(29, 121, 29, 84), 5000);
		this.anims.put("on", onAnim);
		this.anims.put("off", offAnim);
		this.currentAnim = this.anims.get("off");
		
		this.state = initialState;
		this.useBox = new Rectangle(this.position.x, this.position.y, this.currentAnim.getWidth(), this.currentAnim.getHeight());
		if (this.state) {
			System.out.println("active");
		} else {
			System.out.println("not active");
		}
	}

	/**
	 * Switches the state of the Switch.
	 * 
	 * @param user The Entity that uses the Switch.
	 */
	public void use(Entity user) {
		if (this.settings.get("requires") == "" ||
				(user.inventory.contains(this.settings.get("requires")))
			) {
			if (this.settings.get("successMessage") != "") PlayerSpeech.getInstance().addMessage(this.settings.get("successMessage"));
			this.state = !this.state;
			if (this.state) this.currentAnim = this.anims.get("on");
			if (!this.state) this.currentAnim = this.anims.get("off");
			if (this.settings.get("target") != "") {
				try {
					if (this.state == true) {
						this.level.env.getEntityByName(this.settings.get("target")).call("on");
					} else {
						this.level.env.getEntityByName(this.settings.get("target")).call("off");
					}
				}
				finally {
					
				}
			}
		} else {
			if (this.settings.get("failureMessage") != "") PlayerSpeech.getInstance().addMessage(this.settings.get("failureMessage"));
		}
	}

	/**
	 * Checks whether the given Rectangle intersects with the useBox
	 * of this Switch.
	 * 
	 * @param rect The Rectangle to check.
	 */
	public boolean canBeUsed(Rectangle rect) {
		return rect.intersects(useBox) && this.active;
	}
	
	public void update(Input input, int delta) {
		if (firstUpdate) {
			this.firstUpdate = false;
			if (this.settings.get("target") != "") {
				try {
					if (this.state == true) {
						this.level.env.getEntityByName(this.settings.get("target")).call("on");
					} else {
						this.level.env.getEntityByName(this.settings.get("target")).call("off");
					}
				}
				finally {
					
				}
			}
		}
		super.update(input, delta);
		this.useBox.setBounds(this.position.x, this.position.y, this.currentAnim.getWidth(), this.currentAnim.getHeight());
	}
	
	public void call(String message) {
		switch (message) {
		case "on" :
			this.active = true;
			break;
		case "off" :
			this.active = false;
			break;
		}
	}
}
