package com.crystalclash.audio;

import java.util.Enumeration;
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
	
	public enum GAME_END_SFX {
		victory, defeat, draw
	}
	
	private static Hashtable<String, Sound> soundMap;
	private static float volume;

	private static MusicWrapper playing;

	public static void load() {
		volume = 1;
		playing = null;

		soundMap = new Hashtable<String, Sound>();
	}

	public static Music getMusic(String path) {
		Music m = FileUtil.getMusic(path);
		return m;
	}

	public static Sound getUnitSFX(String unitName, String file) {
		String path = String.format("%s/%s", unitName, file);
		return soundMap.get(path);
		
//		if (soundMap.containsKey(path)) {
//			return soundMap.get(path);
//		} else {
//			Sound s = FileUtil.getUnitSFX(unitName, file);
//			soundMap.put(path, s);
//			return s;
//		}
	}

	public static void playMusic(MUSIC music) {
		if (playing != null && playing.isPlaying())
			fadeOut(music.toString());
		else
			fadeIn(music.toString());

	}

	public static Sound playUnitSFX(String unitName, SOUND sound) {
		int count = GameController.getUnitSoundCount(unitName, sound);
		Random rand = new Random();

		Sound ret = null;
		if (count > 0) {
			int index = rand.nextInt(count);
			String file = String.format("%s_%s", sound.toString(), index);

			ret = getUnitSFX(unitName, file);
			ret.play(volume);
		}
		return ret;
	}
	
	public static void playEndSound(GAME_END_SFX sound) {
		soundMap.get(sound.toString()).play(volume);
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
			playing.setVolume(volume / 2);
	}

	public static float getVolume() {
		return volume;
	}

	public static float toogleVolume() {
		setVolume(volume == 0 ? 1 : 0);
		return volume;
	}

	private static void fadeOut(final String name) {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(playing, MusicAccessor.VOLUME, 1f).target(0))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if(type == TweenCallback.COMPLETE){
							playing.stop();
							fadeIn(name);
						}
					}
				}));
	}

	private static void fadeIn(String name) {
		playing = new MusicWrapper(getMusic(String.format("data/audio/music/%s.mp3", name)), 0);
		playing.play();

		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(playing, MusicAccessor.VOLUME, 0.5f).target(volume / 2)));
	}

	public static void loadFirstTurnSFX() {
		Enumeration<String> unit_names = GameController.getUnitNames();
		String unitName;
		String file;
		Sound s;
		while (unit_names.hasMoreElements()) {
			unitName = unit_names.nextElement();
			
			int count = GameController.getUnitSoundCount(unitName, SOUND.place);
			for (int i = 0; i < count; i++){
				file = String.format("%s_%s", SOUND.place, i);
				s = FileUtil.getUnitSFX(unitName, file);
				soundMap.put(String.format("%s/%s", unitName, file), s);
			}
		}
	}

	public static void loadTurnAnimationSFX() {
		Sound s = FileUtil.getSound(GAME_END_SFX.victory.toString());
		soundMap.put(GAME_END_SFX.victory.toString(), s);
		
		s = FileUtil.getSound(GAME_END_SFX.draw.toString());
		soundMap.put(GAME_END_SFX.draw.toString(), s);
		
		s = FileUtil.getSound(GAME_END_SFX.defeat.toString());
		soundMap.put(GAME_END_SFX.defeat.toString(), s);
		
		Enumeration<String> unit_names = GameController.getUnitNames();
		String unitName;
		String file = SOUND.attack.toString() + "_0";
		while (unit_names.hasMoreElements()) {
			unitName = unit_names.nextElement();
			s = FileUtil.getUnitSFX(unitName, file);
			soundMap.put(String.format("%s/%s", unitName, file), s);
		}
	}

	// TODO:Falta controlar que solo cargue los de las unidades que tenes en el campo.
	public static void loadNormalGameSFX() {
		Enumeration<String> unit_names = GameController.getUnitNames();
		String unitName;
		String file;
		Sound s;
		while (unit_names.hasMoreElements()) {
			unitName = unit_names.nextElement();
			
			int count = GameController.getUnitSoundCount(unitName, SOUND.select);
			for (int i = 0; i < count; i++){
				file = String.format("%s_%s", SOUND.select, i);
				s = FileUtil.getUnitSFX(unitName, file);
				soundMap.put(String.format("%s/%s", unitName, file), s);
			}
			
			count = GameController.getUnitSoundCount(unitName, SOUND.chose_attack);
			for (int i = 0; i < count; i++){
				file = String.format("%s_%s", SOUND.chose_attack, i);
				s = FileUtil.getUnitSFX(unitName, file);
				soundMap.put(String.format("%s/%s", unitName, file), s);
			}
			
			count = GameController.getUnitSoundCount(unitName, SOUND.chose_defend);
			for (int i = 0; i < count; i++){
				file = String.format("%s_%s", SOUND.chose_defend, i);
				s = FileUtil.getUnitSFX(unitName, file);
				soundMap.put(String.format("%s/%s", unitName, file), s);
			}
			
			count = GameController.getUnitSoundCount(unitName, SOUND.chose_move);
			for (int i = 0; i < count; i++){
				file = String.format("%s_%s", SOUND.chose_move, i);
				s = FileUtil.getUnitSFX(unitName, file);
				soundMap.put(String.format("%s/%s", unitName, file), s);
			}
		}
	}

}
