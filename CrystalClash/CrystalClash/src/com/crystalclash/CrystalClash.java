package com.crystalclash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.crystalclash.renders.GameEngine;

public class CrystalClash extends Game {
	// constant useful for logging
	public static final String LOG = CrystalClash.class.getSimpleName();
	private FPSLogger fpsLogger;

	public static final String TITLE = "Crystal Clash";
	public static final String VERSION = "0.2";
	public static final boolean DEBUG = false;

	public static float REALLY_SLOW_ANIMATION_SPEED = 2.0f;
	public static float SLOW_ANIMATION_SPEED = 0.75f;
	public static float NORMAL_ANIMATION_SPEED = 0.4f;
	public static float FAST_ANIMATION_SPEED = 0.1f;
	public static float FIGTH_ANIMATION_SPEED = 1.5f;
	public static float ENTRANCE_ANIMATION_SPEED = 2.5f;
	public static float WALK_ANIMATION_SPEED = 1;
	public static float WIDTH = 1280;
	public static float HEIGHT = 853;

	@Override
	public void create() {
		Texture.setEnforcePotImages(false);
		Gdx.input.setOnscreenKeyboardVisible(false);
		fpsLogger = new FPSLogger();
		setScreen(GameEngine.getInstance());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render()
	{
		super.render();

		// output the current FPS
		// fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
