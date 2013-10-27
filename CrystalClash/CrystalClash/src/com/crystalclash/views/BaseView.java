package com.crystalclash.views;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.scenes.scene2d.Group;

public abstract class BaseView extends Group {

	public BaseView() {
	}

	public abstract void init();

	public abstract Timeline pushEnterAnimation(Timeline t);

	public abstract Timeline pushExitAnimation(Timeline t);

	public abstract void shown();

	public abstract void closed();

	public abstract void dispose();
}
