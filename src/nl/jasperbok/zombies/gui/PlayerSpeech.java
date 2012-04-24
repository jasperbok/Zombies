package nl.jasperbok.zombies.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class PlayerSpeech {
	
	private static PlayerSpeech instance = null;
	
	private ArrayList<String> messageQueue;
	private ArrayList<Integer> queueMessageLengths;
	private int currentMessagePassedTime = 0;
	/**
	 * The HashMap with messages per level.
	 */
	private HashMap<String, HashMap<String, String>> messages = new HashMap<String, HashMap<String, String>>();
	
	private PlayerSpeech() {
		initMessages();
		this.messageQueue = new ArrayList<String>();
		this.queueMessageLengths = new ArrayList<Integer>();
	}
	
	/**
	 * Initializes the messages per level.
	 */
	public void initMessages() {
		// Init the level hashmaps
		messages.put("Level1", new HashMap<String, String>());
		
		// Init the messages for Level1
		messages.get("Level1").put("missingkey", "It seems I need a key for this.");
	}
	
	public void addMessage(String message) {
		this.addMessage(message, 5000);
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
		}
	}
	
	/**
	 * Displays the message for the given level key and message key.
	 * 
	 * @param levelKey
	 * @param messageKey
	 */
	public void displayMessage(String levelKey, String messageKey) {
		displayMessage(levelKey, messageKey, 4000);
	}
	
	/**
	 * Displays the message for the given level key and message key.
	 * 
	 * @param levelKey
	 * @param messageKey
	 * @param duration
	 */
	public void displayMessage(String levelKey, String messageKey, int duration) {
		levelKey = levelKey.replaceAll("^.+\\.", "");
		String message = messages.get(levelKey).get(messageKey);
		if (message == null) {
			message = "";
		}
		this.addMessage(message, duration);
	}
	
	public static PlayerSpeech getInstance() {
		if (instance == null) {
			instance = new PlayerSpeech();
		}
		return instance;
	}
}
