package com.crystalclash.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class StaticParallaxLevel extends ParallaxLevel {
	public StaticParallaxLevel(TextureRegion t, float y, float x, float s) {
		super(y, x, s);
		addActor(new Image(t));
	}
}
