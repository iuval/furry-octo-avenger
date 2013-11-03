package com.crystalclash.audio;

import java.util.Hashtable;
import java.util.Random;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.crystalclash.accessors.MusicAccessor;
import com.crystalclash.controllers.GameController;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.util.FileUtil;

public class AudioManager {

	public enum MUSIC {
		menu, tutorial, in_game, select_units, animations
	}
	
	public enum SOUND {
		chose_attack, chose_move, chose_defend, place, select, attack
	}
	
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

	public static Sound getSound(String unitName, String file) {
		String path = String.format("%s/%s", unitName, file);
		if (soundMap.contains(path)) {
			return soundMap.get(path);
		} else {
			Sound s = FileUtil.getSound(unitName, file);
			soundMap.put(path, s);
			return s;
		}
	}

	public static void playMusic(MUSIC music) {
		if (playing != null && playing.isPlaying())
			fadeOut(music.toString());
		else
			fadeIn(music.toString());

	}

	public static void playSound(String unitName, SOUND sound) {
		int count = GameController.getUnitSoundCount(unitName, sound);
		Random rand = new Random();
		int index = rand.nextInt(count);
		String file = String.format("%s_%s", sound.toString(), index);
		
		getSound(unitName, file).play(volume);
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
				.push(Tween.to(playing, MusicAccessor.VOLUME, 1f).target(volume / 3)));
	}
}
