package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AnimatedActor extends Actor {
	private final Animation anim;
	private float stateTime = 0;

	public AnimatedActor(float frameDuration, TextureRegion... keyFrames) {
		this.anim = new Animation(frameDuration, keyFrames);
		setSize(keyFrames[0].getRegionWidth(), keyFrames[0].getRegionHeight());
	}

	@Override
	public void act(float delta) {
		stateTime += delta;
	}

	public void reset() {
		stateTime = 0;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(anim.getKeyFrame(stateTime), getX(), getY(), getWidth(),
				getHeight());
	}
}
