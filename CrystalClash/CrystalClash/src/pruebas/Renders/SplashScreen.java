package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.CrystalClash.CrystalClash;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SplashScreen extends MenuRender {
	private Texture backgroundTexture;
	private Texture crystalTexture;
	private Texture nameTexture;

	private Image background;
	private Image crystal;
	private Image name;

	public SplashScreen() {
		load();
	}

	private void load() {
		backgroundTexture = new Texture(
				Gdx.files.internal("data/Images/Splash/splash_background.jpg"));

		background = new Image(backgroundTexture);
		addActor(background);

		background.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);

		crystalTexture = new Texture(
				Gdx.files.internal("data/Images/Splash/splash_crystal.png"));
		nameTexture = new Texture(
				Gdx.files.internal("data/Images/Splash/splash_name.png"));

		crystal = new Image(crystalTexture);
		addActor(crystal);
		name = new Image(nameTexture);
		addActor(name);
		crystal.setPosition(CrystalClash.WIDTH / 2 - crystal.getWidth() / 2, CrystalClash.HEIGHT +
				CrystalClash.HEIGHT / 2 - crystal.getHeight() / 2);
		name.setPosition(CrystalClash.WIDTH / 2 - name.getWidth() / 2, -name.getHeight());
	}

	@Override
	public void dispose() {
		backgroundTexture.dispose();
		crystalTexture.dispose();
		nameTexture.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t.beginParallel()
				.push(Tween.set(background, ActorAccessor.ALPHA)
						.target(0))
				.push(Tween.set(crystal, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(crystal, ActorAccessor.Y).target(
						crystal.getHeight()))
				.push(Tween.set(name, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(name, ActorAccessor.Y)
						.target(-name.getHeight()))
				.push(Tween.to(background, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED)
						.target(1))
				.push(Tween.to(crystal, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED).target(1))
				.push(Tween.to(crystal, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(
						CrystalClash.HEIGHT / 2 - crystal.getHeight() / 2 + 40))
				.push(Tween.to(name, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED).target(1))
				.push(Tween.to(name, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(40))
				.end();
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t.beginParallel()
				.push(Tween.to(background, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED)
						.target(0))
				.push(Tween.to(crystal, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED).target(0))
				.push(Tween.to(name, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED).target(0))
				.end();
	}
}
