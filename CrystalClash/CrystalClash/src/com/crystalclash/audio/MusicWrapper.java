package com.crystalclash.audio;

import com.badlogic.gdx.audio.Music;

public class MusicWrapper {

	private float volume;
	private Music music;

	public MusicWrapper(Music music, float volume) {
		this.music = music;
		this.volume = volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
		music.setVolume(volume);
	}
	
	public float getVolume() {
		return volume;
	}

	public void play(){
		music.setLooping(true);
		music.play();
	}
	
	public void stop(){
		music.stop();
		music.dispose();
	}
	
	public boolean isPlaying(){
		return music.isPlaying();
	}
}
