package nl.jasperbok.zombies.sound;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class SoundManager {
	private HashMap<String, Audio> sounds;
	
	public SoundManager() {
		sounds = new HashMap<String, Audio>();
	}
	
	public void loadSFX(String file) throws IOException {
		sounds.put(file, AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("data/sound/sfx/" + file + ".wav")));
	}
	
	public void playSFX(String soundKey) throws Exception {
		if (!sounds.containsKey(soundKey)) {
			throw new Exception("The sound: \"" + soundKey + "\" was not loaded.");
		} else {
			sounds.get(soundKey).playAsSoundEffect(1.0f, 1.0f, false);
		}
	}
}
