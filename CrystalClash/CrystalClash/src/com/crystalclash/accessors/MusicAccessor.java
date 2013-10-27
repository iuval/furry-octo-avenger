package com.crystalclash.accessors;

import aurelienribon.tweenengine.TweenAccessor;

import com.crystalclash.audio.MusicWrapper;

public class MusicAccessor implements TweenAccessor<MusicWrapper> {

	public static final int VOLUME = 0;

	@Override
	public int getValues(MusicWrapper target, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case VOLUME:
			returnValues[0] = target.getVolume();
			return 1;
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(MusicWrapper target, int tweenType, float[] newValues) {
		switch (tweenType) {
		case VOLUME:
			target.setVolume(newValues[0]);
			break;
		}
	}

}
