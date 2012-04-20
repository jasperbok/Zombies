package nl.jasperbok.zombies.gui;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class PlayerSpeech {
	
	private static PlayerSpeech instance = null;
	
	private ArrayList<String> messageQueue;
	private ArrayList<Integer> queueMessageLengths;
	private int currentMessagePassedTime = 0;
	
	private PlayerSpeech() {
		this.messageQueue = new ArrayList<String>();
		this.queueMessageLengths = new ArrayList<Integer>();
	}
	
	public void addMessage(String message, int duration) {
		messageQueue.add(message);
		queueMessageLengths.add(duration);
	}
	
	public void update(int delta) {
		// If there is a message in the queue.
		if (messageQueue.size() > 0) {
			currentMessagePassedTime += delta;
			
			// If the current message's time has passed.
			if (currentMessagePassedTime >= queueMessageLengths.get(0)) {
				messageQueue.remove(0);
				queueMessageLengths.remove(0);
				currentMessagePassedTime = 0;
			}
		}
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (messageQueue.size() > 0) {
			g.drawString(messageQueue.get(0), 200, 680);
			System.out.println("messageQueue.get(0)");
		}
	}
	
	public static PlayerSpeech getInstance() {
		if (instance == null) {
			instance = new PlayerSpeech();
		}
		return instance;
	}
}
