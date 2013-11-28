package com.crystalclash.renders;

import com.badlogic.gdx.scenes.scene2d.Group;

public abstract class ParallaxLevel extends Group {
	public ParallaxLevel(float y, float x) {
		setPosition(x, y);
	}

	public abstract void update(float d);
}
