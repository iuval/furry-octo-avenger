package com.crystalclash.views;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.renders.helpers.ResourceHelper;

public class SplashView extends BaseView {
	private TextureRegion crystalTexture;
	private TextureRegion nameTexture;

	private Image crystal;
	private Image name;

	public SplashView() {
		load();
	}

	private void load() {
		crystalTexture = ResourceHelper.getTexture("splash/splash_crystal", false);
		nameTexture = ResourceHelper.getTexture("splash/splash_name", false);

		crystal = new Image(crystalTexture);
		addActor(crystal);
		name = new Image(nameTexture);
		addActor(name);
		crystal.setPosition(CrystalClash.WIDTH / 2 - crystal.getWidth() / 2, CrystalClash.HEIGHT +
				CrystalClash.HEIGHT / 2 - crystal.getHeight() / 3);
		name.setPosition(CrystalClash.WIDTH / 2 - name.getWidth() / 2, crystal.getY() - name.getHeight());
	}

	@Override
	public void dispose() {
		crystalTexture = null;
		nameTexture = null;
	}

	@Override
	public void init() {
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		// return t.beginParallel()
		// .push(Tween.set(crystal, ActorAccessor.ALPHA).target(0))
		// .push(Tween.set(crystal, ActorAccessor.Y).target(
		// crystal.getHeight()))
		// .push(Tween.set(name, ActorAccessor.ALPHA).target(0))
		// .push(Tween.set(name, ActorAccessor.Y)
		// .target(-name.getHeight()))
		// .push(Tween.to(crystal, ActorAccessor.ALPHA,
		// CrystalClash.SLOW_ANIMATION_SPEED).target(1))
		// .push(Tween.to(crystal, ActorAccessor.Y,
		// CrystalClash.SLOW_ANIMATION_SPEED).target(
		// CrystalClash.HEIGHT / 2 - crystal.getHeight() / 2 + 40))
		// .push(Tween.to(name, ActorAccessor.ALPHA,
		// CrystalClash.SLOW_ANIMATION_SPEED).target(1))
		// .push(Tween.to(name, ActorAccessor.Y,
		// CrystalClash.SLOW_ANIMATION_SPEED).target(40))
		// .end();
		return t;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t.beginParallel()
				.push(Tween.to(crystal, ActorAccessor.ALPHA,
						CrystalClash.SLOW_ANIMATION_SPEED).target(0))
				.push(Tween.to(name, ActorAccessor.ALPHA,
						CrystalClash.SLOW_ANIMATION_SPEED).target(0))
				.end();
	}

	@Override
	public void shown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}
}
