package nl.jasperbok.zombies.sound;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class SoundManager {
	private HashMap<String, Sound> sounds;
	
	public SoundManager() {
		sounds = new HashMap<String, Sound>();
	}
	
	public void loadSFX(String file) {
		try {
			sounds.put(file, new Sound("data/sound/sfx/" + file + ".ogg"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void playSFX(String soundKey) {
		playSFX(soundKey, 0, 0);
	}
	
	public void playSFX(String soundKey, float x, float y) {
		if (sounds.containsKey(soundKey) == false) {
			try {
				throw new Exception("The sound: \"" + soundKey + "\" was not loaded.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			sounds.get(soundKey).playAt(x, y, 0);
		}
	}
}
