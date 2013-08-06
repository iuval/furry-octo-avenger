package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.MenuGames;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Networking.ServerDriver;
import pruebas.Renders.helpers.ui.GameListItem;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuGamesRender extends MenuRender {

	private static MenuGamesRender instance;
	private TweenManager tweenManager;

	private MenuGames controller;
	private ScrollPane scrollPane;
	private Image gamesImage;

	VerticalGroup list;

	private BitmapFont font;
	private Label lblHeading;
	private TextButton btnLogOut;

	private InputListener surrenderListener;
	private InputListener playListener;
	private Skin listItemSkin;

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
		stage.addActor(btnLogOut);
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
		initSkin();

		font = new BitmapFont(Gdx.files.internal("data/Fonts/font.fnt"), false);

		lblHeading = new Label("Welcome "
				+ GameController.getInstancia().getUser().getNick(),
				new LabelStyle(font, Color.WHITE));
		lblHeading.setPosition(50, CrystalClash.HEIGHT - 50);

		btnLogOut = new TextButton("Log Out", listItemSkin.get(
				"buttonLogOutStyle", TextButtonStyle.class));
		btnLogOut.setPosition(CrystalClash.WIDTH - btnLogOut.getWidth() - 50,
				CrystalClash.HEIGHT - btnLogOut.getHeight() - 10);
		btnLogOut.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("LogOut");
				controller.logOut();
			}
		});

		list = new VerticalGroup();
		list.setWidth(CrystalClash.WIDTH);

		// put the table inside a scrollpane
		scrollPane = new ScrollPane(list);
		scrollPane
				.setBounds(0, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT - 80);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, true);
		scrollPane.setSmoothScrolling(true);
		scrollPane.invalidate();

		gamesImage = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/games_header.png")));

		list.addActor(gamesImage);

		Image menuImage = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/new_games_header.png")));
		list.addActor(menuImage);

		TextButton buttonNewRandom = new TextButton("New random game",
				listItemSkin.get("buttonStyle", TextButtonStyle.class));
		buttonNewRandom.setBounds(0, 0, 250, list.getWidth());
		buttonNewRandom.align(Align.center);
		list.addActorAfter(menuImage, buttonNewRandom);
		buttonNewRandom.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((TextButton) event.getListenerActor()).setText("Buscando...");
				controller.enableRandom();
				return true;
			}
		});

		TextButton buttonNewInvite = new TextButton("Invite friend",
				listItemSkin.get("buttonStyle", TextButtonStyle.class));
		buttonNewInvite.setBounds(0, 0, 250, list.getWidth());
		buttonNewInvite.align(Align.center);
		list.addActorAfter(menuImage, buttonNewInvite);

		ServerDriver.getListGames(GameController.getInstancia().getUser()
				.getId());
		enterAnimation();
	}

	// SERVER DRIVER CALLBACKS --------------------------------------------
	public void listGamesSuccess(String[][] games) {
		GameListItem listingItem;
		for (int i = 0, len = games.length; i < len; i++) {
			listingItem = new GameListItem(games[i][0], games[i][1],
					games[i][2], games[i][3], listItemSkin, surrenderListener,
					playListener);
			list.addActorAfter(gamesImage, listingItem);
		}
	}

	private void initSkin() {
		// Listeners
		surrenderListener = new InputListener() {
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

		playListener = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				controller.getGameTurn(((GameListItem) event.getListenerActor()
						.getParent()).gameId);
				return true;
			}
		};

		TextureAtlas listItemButtonAtlas = new TextureAtlas(
				"data/Images/Buttons/buttons.pack");
		listItemSkin = new Skin(listItemButtonAtlas);
		listItemSkin
				.add("font",
						new BitmapFont(Gdx.files
								.internal("data/Fonts/font.fnt"), false));
		listItemSkin
				.add("background",
						new Texture(
								Gdx.files
										.internal("data/Images/Menu/games_item_background.png")));

		TextButtonStyle outerStyle = new TextButtonStyle();
		outerStyle.font = listItemSkin.getFont("font");
		outerStyle.up = listItemSkin.getDrawable("outer_button_orange");
		outerStyle.down = listItemSkin
				.getDrawable("outer_button_orange_pressed");
		listItemSkin.add("buttonStyle", outerStyle);

		TextButtonStyle innerStyle = new TextButtonStyle();
		innerStyle.font = listItemSkin.getFont("font");
		innerStyle.up = listItemSkin.getDrawable("button_orange");
		innerStyle.down = listItemSkin.getDrawable("button_orange_pressed");
		listItemSkin.add("buttonLogOutStyle", innerStyle);
	}

	public void listGamesError(String message) {
		System.out.println(message);
	}

	public void enableRandomSuccess() {

	}

	public void enableRandomError(String message) {
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
