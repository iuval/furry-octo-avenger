package pruebas.Audio;

import java.util.Hashtable;

import pruebas.Util.FileUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
	
	private static Hashtable<String, Music> musicMap;
	private static Hashtable<String, Sound> soundMap;
	private static float volume;
	
	private static Music playing;
	
	public static void load() {
		volume = 0.5f;
		playing = null;
		
		musicMap = new Hashtable<String, Music>();
		soundMap = new Hashtable<String, Sound>();
	}
	
	public static Music getMusic(String path) {
		if (musicMap.contains(path)) {
			return musicMap.get(path);
		} else {
			Music m = FileUtil.getMusic(path);
			musicMap.put(path, m);
			return m;
		}
	}
	
	public static Sound getSound(String path) {
		if (soundMap.contains(path)) {
			return soundMap.get(path);
		} else {
			Sound s = FileUtil.getSound(path);
			soundMap.put(path, s);
			return s;
		}
	}
	
	public static void playMusic(String name) {
		if (playing != null)
			playing.stop();
		
		playing = getMusic("data/Audio/" + name + ".mp3");
		playing.play();
	}
	
	public static void playSound(String name) {
		getSound("data/SFX/" + name + ".mp3").play();
	}
	
	public static void VolumeUp() {
		volume += 0.05;
		if (volume > 1)
			volume = 1;

	}

	public static void VolumeDown() {
		volume -= 0.05;
		if (volume < 0)
			volume = 0;
	}
}
