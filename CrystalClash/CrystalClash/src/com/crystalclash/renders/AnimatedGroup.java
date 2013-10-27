package com.crystalclash.renders;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.scenes.scene2d.Group;

public class AnimatedGroup extends Group implements Animated {

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t;
	}

}
