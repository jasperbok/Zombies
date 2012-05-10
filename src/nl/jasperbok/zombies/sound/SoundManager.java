package nl.jasperbok.zombies.sound;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class SoundManager {
	private HashMap<String, Sound> sounds;
	
	public SoundManager() {
		sounds = new HashMap<String, Sound>();
	}
	
	public boolean loadSFX(String file) {
		try {
			if (!sounds.containsKey(file)) {
				sounds.put(file, new Sound("data/sound/sfx/" + file + ".ogg"));
				return true;
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void playSFX(String soundKey) {
		playSFX(soundKey, new Vector2f(0, 0), new Vector2f(0, 0));
	}
	
	public void playSFX(String soundKey, Vector2f dispatcherPosition, Vector2f listenerPosition) {
		Vector2f playPosition = dispatcherPosition.sub(listenerPosition);
		float distance = playPosition.length();
		float volume;
		if (distance != 0) {
			if (distance < 1200) {
				volume = 1 / ((distance * distance / 100) / 500);
			} else {
				volume = 0;
			}
		} else {
			volume = 1;
		}
		
		if (sounds.containsKey(soundKey) == false && !this.loadSFX(soundKey)) {
			try {
				throw new Exception("The sound: \"" + soundKey + "\" was not loaded.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//System.out.println(this.getClass().toString() + ".playSFX: playing at volume " + volume);
			sounds.get(soundKey).playAt(1, volume, 0, 0, 0);
		}
	}
	
	public void stopSFX(String key) {
		if (!isSFXPlaying(key)) {
			sounds.get(key).stop();
		}
	}
	
	public boolean isSFXPlaying(String key) {
		return sounds.get(key).playing();
	}
}
