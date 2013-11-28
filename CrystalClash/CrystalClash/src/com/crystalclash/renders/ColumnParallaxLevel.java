package com.crystalclash.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.crystalclash.CrystalClash;

public class ColumnParallaxLevel extends ParallaxLevel {
	private Image image_1;
	private Image image_2;
	protected float delta = 0;
	protected float last = 0;
	private float dy = CrystalClash.HEIGHT * 2;

	public ColumnParallaxLevel(TextureRegion t, float y, float x) {
		super(y, x);
		image_1 = new Image(t);
		addActor(image_1);
		image_1.setY(0);
		image_2 = new Image(t);
		addActor(image_2);
		image_2.setY(-CrystalClash.HEIGHT);
	}

	@Override
	public void update(float d) {
		delta = d - last;
		last = d;
		image_1.setY(image_1.getY() + delta);
		if (image_1.getY() < -CrystalClash.HEIGHT) {
			image_1.setY(image_1.getY() + dy);
		}
		if (image_1.getY() > CrystalClash.HEIGHT) {
			image_1.setY(image_1.getY() - dy);
		}

		image_2.setY(image_2.getY() + delta);
		if (image_2.getY() < -CrystalClash.HEIGHT) {
			image_2.setY(image_2.getY() + dy);
		}
		if (image_2.getY() > CrystalClash.HEIGHT) {
			image_2.setY(image_2.getY() - dy);
		}
	}
}
