package com.crystalclash.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.crystalclash.CrystalClash;

public class CloudParallaxLevel extends ParallaxLevel {
	private Image image_1;
	private Image image_2;
	private float side_speed;

	public CloudParallaxLevel(TextureRegion t, float y, float x, float sideSpeed, float vspeed) {
		super(y, x, vspeed);
		image_1 = new Image(t);
		addActor(image_1);
		image_1.setX(0);
		image_2 = new Image(t);
		addActor(image_2);
		image_2.setX(CrystalClash.WIDTH);
		side_speed = sideSpeed;
	}

	@Override
	public void update(float dy) {
		image_1.setX(image_1.getX() + side_speed);
		if (image_1.getX() <= -CrystalClash.WIDTH) {
			image_1.setX(CrystalClash.WIDTH);
		}
		image_2.setX(image_2.getX() + side_speed);
		if (image_2.getX() <= -CrystalClash.WIDTH) {
			image_2.setX(CrystalClash.WIDTH);
		}
		setY(base_y + dy * vspeed);
	}
}
