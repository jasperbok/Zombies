package nl.jasperbok.zombies.gui;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Notifications {
	
	public class Note {
		public String message;
		public int life;
		
		public Note(String message, int life) {
			this.message = message;
			this.life = life;
		}
		
		public void update(int delta) {
			if (life > 0) {
				life -= 1 * delta;
			} else {
				Notifications.getInstance().notes.remove(this);
			}
		}
	}
	
	private static Notifications instance = null;
	private List<Note> notes = new CopyOnWriteArrayList<Note>();
	
	public void add(String message) {
		add(message, 1000);
	}
	
	public void add(String message, int life) {
		notes.add(new Note(message, life));
		System.out.println(notes.size());
	}
	
	public void update(int delta) {
		for (Note n: notes) {
			n.update(delta);
		}
	}
	
	public void render(GameContainer container, Graphics g) {
		Iterator<Note> it = notes.iterator();
		int i = 0;
		while (it.hasNext()) {
			Note note = (Note) it.next();
			g.drawString(note.message, 10, 50 + i * 30);
			i += 1;
		}
	}
	
	private Notifications() {
	}
	
	public static synchronized Notifications getInstance() {
		if (instance == null) {
			instance = new Notifications();
		}
		
		return instance;
	}
}
