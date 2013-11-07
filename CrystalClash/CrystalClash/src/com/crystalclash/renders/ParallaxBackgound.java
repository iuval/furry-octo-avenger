package com.crystalclash.renders;

import java.util.Random;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.renders.helpers.ResourceHelper;

public class ParallaxBackgound extends Group {
	private static ParallaxBackgound instance;
	private Array<ParallaxLevel> levels;
	private Random rand;

	private boolean logInLoaded = false;
	private boolean gmesListLoaded = false;
	private float bg_y;

	public static ParallaxBackgound getInstance() {
		if (instance == null)
			instance = new ParallaxBackgound();
		return instance;
	}

	private ParallaxBackgound() {
		rand = new Random();
		levels = new Array<ParallaxLevel>();
		TextureRegion t = ResourceHelper.getTexture("menu/background");
		setBounds(0, -854, 1280, 1708);
		addLevel(new StaticParallaxLevel(t, 0, 0, 0.01f));
		setColor(getColor().r, getColor().g, getColor().b, 0);
	}

	public void loadLogIn() {
		if (!logInLoaded) {
			addLevel(new MovingParallaxLevel(ResourceHelper.getTexture("menu/nimbus1"), 1400, -(CrystalClash.WIDTH + rand.nextInt(500)), 0.1f, -0.6f));
			addLevel(new MovingParallaxLevel(ResourceHelper.getTexture("menu/nimbus2"), 1200, -(CrystalClash.WIDTH + rand.nextInt(500)), 0.05f, -1f));
			addLevel(new MovingParallaxLevel(ResourceHelper.getTexture("menu/nimbus3"), 1000, -(CrystalClash.WIDTH + rand.nextInt(500)), 0.08f, -2f));
			logInLoaded = true;
		}
	}

	public void loadGamesList() {
		if (!gmesListLoaded) {
			addLevel(new MovingContinuousParallaxLevel(ResourceHelper.getTexture("menu/level1_clouds"), 0, 0, 0.03f, -0.1f));
			addLevel(new StaticParallaxLevel(ResourceHelper.getTexture("menu/level1_columns"), 0, 0, 0.03f));

			addLevel(new MovingContinuousParallaxLevel(ResourceHelper.getTexture("menu/level2_clouds"), -427, 0, 0.05f, -0.5f));
			addLevel(new StaticParallaxLevel(ResourceHelper.getTexture("menu/level2_shadows"), -427, 0, 0.05f));
			addLevel(new StaticParallaxLevel(ResourceHelper.getTexture("menu/level2_columns"), -427, 0, 0.05f));

			addLevel(new MovingContinuousParallaxLevel(ResourceHelper.getTexture("menu/level3_clouds"), -427, 0, 0.1f, -1));
			addLevel(new StaticParallaxLevel(ResourceHelper.getTexture("menu/level3_shadows"), -427, 0, 0.1f));
			addLevel(new StaticParallaxLevel(ResourceHelper.getTexture("menu/level3_columns"), -427, 0, 0.1f));

			addLevel(new MovingParallaxLevel(ResourceHelper.getTexture("menu/nimbus4"), 700, -(CrystalClash.WIDTH + rand.nextInt(500)), 0.1f, -0.4f));
			addLevel(new MovingParallaxLevel(ResourceHelper.getTexture("menu/nimbus5"), 600, -(CrystalClash.WIDTH + rand.nextInt(500)), 0.05f, -0.6f));
			addLevel(new MovingParallaxLevel(ResourceHelper.getTexture("menu/nimbus6"), 400, -(CrystalClash.WIDTH + rand.nextInt(500)), 0.08f, -1f));

			gmesListLoaded = true;
		}
	}

	public Timeline pushMoveToLogin(Timeline t) {
		return t.push(Tween.to(this, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
				.target(-854));
	}

	public Timeline pushMoveToGamesList(Timeline t) {
		return t.push(Tween.to(this, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
				.target(0));
	}

	private void addLevel(ParallaxLevel level) {
		addActor(level);
		levels.add(level);
	}

	public Timeline pushShow(Timeline t) {
		return t.push(Tween.to(this, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED).target(1));
	}

	public Timeline pushHide(Timeline t) {
		return t.push(Tween.to(this, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED).target(0));
	}

	public void updateY(float dy) {
		bg_y = dy;
	}

	@Override
	public void act(float delta) {
		for (int i = 0; i < levels.size; i++) {
			levels.get(i).update(bg_y);
		}
		super.act(delta);
	}
}
