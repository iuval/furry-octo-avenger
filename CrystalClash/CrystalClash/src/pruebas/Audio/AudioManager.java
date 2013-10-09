package pruebas.Audio;

import java.util.Hashtable;

import pruebas.Accessors.MusicAccessor;
import pruebas.Renders.GameEngine;
import pruebas.Util.FileUtil;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

	private static Hashtable<String, Music> musicMap;
	private static Hashtable<String, Sound> soundMap;
	private static float volume;

	private static MusicWrapper playing;

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
		if (playing != null && playing.isPlaying())
			fadeOut(name);
		else
			fadeIn(name);

	}

	public static void playSound(String name) {
		playing.setVolume(volume / 2);
		getSound(String.format("data/audio/sfx/%s.mp3", name)).play(volume);
		playing.setVolume(volume);
	}

	public static void volumeUp() {
		volume += 0.05;
		if (volume > 1)
			volume = 1;

		if (playing != null)
			playing.setVolume(volume);
	}

	public static void volumeDown() {
		volume -= 0.05;
		if (volume < 0)
			volume = 0;

		if (playing != null)
			playing.setVolume(volume);
	}

	public static void setVolume(float vol) {
		volume = vol;

		if (playing != null)
			playing.setVolume(volume);
	}

	public static float getVolume() {
		return volume;
	}

	public static float toogleVolume() {
		setVolume(volume == 0 ? 0.5f : 0);
		return volume;
	}

	private static void fadeOut(final String name) {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(playing, MusicAccessor.VOLUME, 1f).target(0))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						playing.stop();
						fadeIn(name);
					}
				}));
	}

	private static void fadeIn(String name) {
		playing = new MusicWrapper(getMusic(String.format("data/audio/music/%s.mp3", name)), 0);
		playing.play();

		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(playing, MusicAccessor.VOLUME, 1f).target(volume)));
	}
}
