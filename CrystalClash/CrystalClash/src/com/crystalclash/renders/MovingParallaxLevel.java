package com.crystalclash.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.crystalclash.CrystalClash;

public class MovingParallaxLevel extends ParallaxLevel {
	private Image image;
	private float side_speed;

	public MovingParallaxLevel(TextureRegion t, float y, float x, float s, float sideSpeed) {
		super(y, x, s);
		image = new Image(t);
		addActor(image);
		side_speed = sideSpeed;
	}

	@Override
	public void update(float dy) {
		setX(getX() + side_speed);
		if (getX() <= -CrystalClash.WIDTH) {
			setX(CrystalClash.WIDTH);
		}
		super.update(dy);
	}
}
