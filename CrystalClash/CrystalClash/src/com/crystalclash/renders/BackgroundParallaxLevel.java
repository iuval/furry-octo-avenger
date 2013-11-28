package com.crystalclash.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BackgroundParallaxLevel extends ParallaxLevel {
	public BackgroundParallaxLevel(TextureRegion t, float y, float x) {
		super(y, x);
		addActor(new Image(t));
	}

	@Override
	public void update(float d) {
		// TODO Auto-generated method stub

	}
}
