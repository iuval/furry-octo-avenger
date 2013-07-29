package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.MenuGames;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Networking.ServerDriver;
import pruebas.Renders.shared.GameListItem;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class MenuGamesRender extends MenuRender {

	private static MenuGamesRender instance;
	private TweenManager tweenManager;

	private MenuGames controller;
	private ScrollPane scrollPane;
	private Table table;

	private BitmapFont font;
	private Label lblHeading;

	public MenuGamesRender(MenuGames menu) {
		this.controller = menu;
		tweenManager = new TweenManager();

		loadStuff();
	}

	public static MenuGamesRender getInstance(MenuGames menu) {
		if (instance == null)
			instance = new MenuGamesRender(menu);

		return instance;
	}

	@Override
	public void render(float dt, Stage stage) {
		stage.addActor(lblHeading);
		stage.addActor(scrollPane);
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
		lblHeading = new Label("Welcome", new LabelStyle(font, Color.WHITE));
		lblHeading.setPosition(50, CrystalClash.HEIGHT - 50);

		// item list table
		table = new Table();
		table.setFillParent(true);
		table.align(Align.bottom).padBottom(20).padTop(20);
		table.defaults().space(10);

		// put the table inside a scrollpane
		scrollPane = new ScrollPane(table);
		scrollPane.setBounds(20, 0, Gdx.graphics.getWidth() - 20,
				Gdx.graphics.getHeight() - 20);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, false);
		scrollPane.invalidate();

		enterAnimation();
		ServerDriver.ListGames(GameController.getInstancia().getUser().getId());
	}

	public void listGamesSuccess(String[][] games) {

		// Listeners
		InputListener surrenderListener = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				System.out
						.println("surrender: "
								+ ((GameListItem) event.getListenerActor()
										.getParent()).gameId);
				return super.touchDown(event, x, y, pointer, button);
			}
		};

		InputListener playListener = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				System.out
						.println("play: "
								+ ((GameListItem) event.getListenerActor()
										.getParent()).gameId);
				return super.touchDown(event, x, y, pointer, button);
			}
		};

		TextureAtlas listItemButtonAtlas = new TextureAtlas(
				"data/Buttons/buttons.pack");
		Skin listItemSkin = new Skin(listItemButtonAtlas);
		listItemSkin
				.add("font",
						new BitmapFont(Gdx.files
								.internal("data/Fonts/font.fnt"), false));

		TextButtonStyle outerStyle = new TextButtonStyle();
		outerStyle.font = listItemSkin.getFont("font");
		outerStyle.up = listItemSkin.getDrawable("outer_button_orange");
		outerStyle.down = listItemSkin
				.getDrawable("outer_button_orange_pressed");
		listItemSkin.add("buttonStyle", outerStyle);

		GameListItem listingItem;
		for (int i = 0, len = games.length; i < len; i++) {
			listingItem = new GameListItem(games[i][0], games[i][1],
					games[i][2], games[i][3], listItemSkin, surrenderListener,
					playListener);
			table.row();
			table.add(listingItem);
		}

	}

	public void listGamesError(String message) {
		System.out.println(message);
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
