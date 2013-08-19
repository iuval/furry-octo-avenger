package pruebas.CrystalClash;

import pruebas.Renders.SplashScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;

public class CrystalClash extends Game {

	public static final String TITLE = "Crystal Clash";
	public static final String VERSION = "0.1";
	public static final boolean DEBUG = false;

	public static float ANIMATION_SPEED = 0.75f;
	public static float WIDTH = 1280;
	public static float HEIGHT = 853;

	@Override
	public void create() {
		Texture.setEnforcePotImages(false);
		setScreen(new SplashScreen());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
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
