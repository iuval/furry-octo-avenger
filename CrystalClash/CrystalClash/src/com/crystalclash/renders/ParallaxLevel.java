package com.crystalclash.renders;

import com.badlogic.gdx.scenes.scene2d.Group;

public abstract class ParallaxLevel extends Group {
	protected float base_y = 0;
	protected float vspeed = 0;

	public ParallaxLevel(float y, float x, float vspeed) {
		setPosition(x, y);
		base_y = y;
		this.vspeed = vspeed;
	}

	public abstract void update(float d);
}
