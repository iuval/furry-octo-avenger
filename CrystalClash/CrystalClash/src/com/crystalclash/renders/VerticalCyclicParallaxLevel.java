package com.crystalclash.renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.crystalclash.CrystalClash;

public class VerticalCyclicParallaxLevel extends ColumnParallaxLevel {
	private Image image_1;
	private Image image_2;

	public VerticalCyclicParallaxLevel(TextureRegion t, float y, float x, float s) {
		super(t,y, x);
		image_1 = new Image(t);
		addActor(image_1);
		image_1.setY(0);
		image_2 = new Image(t);
		addActor(image_2);
		image_2.setY(CrystalClash.HEIGHT);
	}

	@Override
	public void update(float d) {
		// TODO Auto-generated method stub
		
	}
}
