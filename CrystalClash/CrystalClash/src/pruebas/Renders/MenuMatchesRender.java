package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.MenuMatches;
import pruebas.CrystalClash.CrystalClash;
import pruebasUtil.SuperAnimation;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class MenuMatchesRender extends MenuRender {

	private static MenuMatchesRender instance;
	private TweenManager tweenManager;

	private MenuMatches controller;

	private BitmapFont font;
	private Label lblHeading;

	public MenuMatchesRender(MenuMatches menu) {
		this.controller = menu;
		tweenManager = new TweenManager();

		loadStuff();
	}

	public static MenuMatchesRender getInstance(MenuMatches menu) {
		if (instance == null)
			instance = new MenuMatchesRender(menu);

		return instance;
	}

	@Override
	public void render(float dt, Stage stage) {
		stage.addActor(lblHeading);
		tweenManager.update(dt);
	}

	@Override
	public void enterAnimation() {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.set(lblHeading, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(lblHeading, ActorAccessor.ALPHA, speed)
						.target(1)).start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
	}

	@Override
	public void exitAnimation() {
	}

	private void loadStuff() {
		font = new BitmapFont(Gdx.files.internal("data/Fonts/font.fnt"), false);

		lblHeading = new Label("Welcome "
				+ GameController.getInstancia().getUser().getNick(),
				new LabelStyle(font, Color.WHITE));
		lblHeading.setPosition(50, CrystalClash.HEIGHT - 50);

		enterAnimation();
	}

	// INPUT PROCESSOR--------------------------------------------
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
