package com.crystalclash.renders;

import com.badlogic.gdx.scenes.scene2d.Group;

public abstract class ParallaxLevel extends Group {
	protected float smoothness;
	protected float originY;

	public ParallaxLevel(float y, float x, float s) {
		originY = y;
		setPosition(x, y);
		smoothness = s;
	}

	public void update(float dy) {
		setY(originY + (dy * smoothness));
	}
}
