package nl.jasperbok.zombies.entity.component;

import nl.jasperbok.zombies.entity.Entity;
import nl.jasperbok.zombies.entity.Usable;

import org.newdawn.slick.Input;

public class DraggableComponent extends Component {
	public DraggableComponent(Entity owner) {
		this.id = Component.DRAGGABLE;
		this.owner = owner;
	}
	
	public void update(Input input, int delta) {
		if (owner.user != null) {
			owner.velocity = owner.user.velocity.copy();
			
			// Check if the user wants to stop dragging.
			if (input.isKeyPressed(Input.KEY_E)) {
				owner.user = null;
			}
		}
	}
}
