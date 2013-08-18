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

	private VerticalGroup list;
	private GameListItem[] gamesList;

	private BitmapFont font;
	private Label lblHeading;
	private TextButton btnLogOut;

	private InputListener surrenderListener;
	private InputListener playListener;
	private Skin listItemSkin;
	private TextButton btnNewRandom;
	private TextButton btnNewInvite;

	private Image refreshMessagePull;
	private Image refreshMessageRelease;
	private boolean isTryingToRefresh = false;
	private boolean showPullDown = false;
	private boolean showRelease = false;

	public MenuGamesRender(MenuGames menu) {
		this.controller = menu;
		tweenManager = new TweenManager();

		load();
	}

	public static MenuGamesRender getInstance(MenuGames menu) {
		if (instance == null)
			instance = new MenuGamesRender(menu);

		return instance;
	}

	@Override
	public void update(float dt) {
		if (scrollPane.isPanning()) {
			if (!isTryingToRefresh && scrollPane.getScrollY() < -100) {
				showPullDown = true;
				showRelease = false;
				isTryingToRefresh = true;
			} else if (isTryingToRefresh && scrollPane.getScrollY() < -200) {
				showPullDown = false;
				showRelease = true;
			} else if (isTryingToRefresh && scrollPane.getScrollY() > -200) {
				if (isTryingToRefresh && scrollPane.getScrollY() > -100) {
					isTryingToRefresh = false;
				} else {
					showPullDown = true;
					showRelease = false;
				}
			}
		} else {
			if (isTryingToRefresh) {
				if (showRelease) {
					loadGameList();
				}
				isTryingToRefresh = false;
			}
		}

		if (isTryingToRefresh) {
			if (showRelease) {
				refreshMessageRelease.setY(list.getTop());
				refreshMessageRelease.setVisible(true);
				refreshMessagePull.setVisible(false);
			} else if (showPullDown) {
				refreshMessagePull.setY(list.getTop());
				refreshMessagePull.setVisible(true);
				refreshMessageRelease.setVisible(false);
			}
		} else {
			refreshMessagePull.setVisible(false);
			refreshMessageRelease.setVisible(false);
		}

		// stage.addActor(lblHeading);
		// stage.addActor(btnLogOut);
		// stage.addActor(scrollPane);
		// stage.addActor(this);
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

	private void load() {
		initSkin();

		font = new BitmapFont(Gdx.files.internal("data/Fonts/font.fnt"), false);

		lblHeading = new Label("Welcome "
				+ GameController.getInstancia().getUser().getNick(),
				new LabelStyle(font, Color.WHITE));
		lblHeading.setPosition(50, CrystalClash.HEIGHT - 50);
		addActor(lblHeading);

		btnLogOut = new TextButton("Log Out", listItemSkin.get("innerButtonStyle",
				TextButtonStyle.class));
		btnLogOut.setPosition(CrystalClash.WIDTH - btnLogOut.getWidth() - 50,
				CrystalClash.HEIGHT - btnLogOut.getHeight() - 10);
		btnLogOut.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("LogOut");
				controller.logOut();
			}
		});
		addActor(btnLogOut);
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
		scrollPane.setupOverscroll(CrystalClash.HEIGHT, 4000, 5000);
		addActor(scrollPane);
		gamesImage = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/current_games_header.png")));

		list.addActor(gamesImage);

		Image menuImage = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/new_games_header.png")));
		list.addActor(menuImage);

		btnNewRandom = new TextButton("New random game", listItemSkin.get(
				"outterButtonStyle", TextButtonStyle.class));
		btnNewRandom.setBounds(0, 0, list.getWidth(), 160);
		btnNewRandom.align(Align.center);
		btnNewRandom.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((TextButton) event.getListenerActor()).setText("Loading...");
				controller.enableRandom();
			}
		});
		list.addActorAfter(menuImage, btnNewRandom);

		btnNewInvite = new TextButton("Invite friend",
				listItemSkin.get("outterButtonStyle", TextButtonStyle.class));
		btnNewInvite.setBounds(0, 0, list.getWidth(), 160);
		btnNewInvite.align(Align.center);
		list.addActorAfter(menuImage, btnNewInvite);

		loadGameList();

		refreshMessagePull = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/RefreshList/refresh_message_pull.png")));
		refreshMessagePull.setVisible(false);
		addActor(refreshMessagePull);

		refreshMessageRelease = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/RefreshList/refresh_message_release.png")));
		refreshMessageRelease.setVisible(false);
		addActor(refreshMessageRelease);

		enterAnimation();
	}

	private void loadGameList() {
		ServerDriver.getListGames(GameController.getInstancia().getUser()
				.getId());
	}

	// SERVER DRIVER CALLBACKS --------------------------------------------
	public void listGamesSuccess(String[][] games) {
		if (gamesList != null) {
			for (int i = 0; i < gamesList.length; i++) {
				gamesList[i].dispose();
				gamesList[i].remove();
			}
		}
		gamesList = new GameListItem[games.length];

		GameListItem listingItem;
		for (int i = 0, len = games.length; i < len; i++) {
			listingItem = new GameListItem(games[i][0], games[i][1],
					games[i][2], games[i][3], listItemSkin, surrenderListener,
					playListener);
			gamesList[i] = listingItem;
			list.addActorAfter(gamesImage, listingItem);
		}
		GameEngine.hideLoading();
	}

	private void initSkin() {
		// Listeners
		surrenderListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// GameEngine.showLoading();
				System.out
						.println("surrender: "
								+ ((GameListItem) event.getListenerActor()
										.getParent()).gameId);
			}
		};

		playListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameEngine.showLoading();
				controller.getGameTurn(((GameListItem) event.getListenerActor()
						.getParent()).gameId, ((GameListItem) event.getListenerActor()
								.getParent()).turn);
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
				.add("play_up",
						new Texture(
								Gdx.files
										.internal("data/Images/Menu/games_list_item_green.png")));
		listItemSkin.add(
				"play_down",
				new Texture(Gdx.files
						.internal("data/Images/Menu/games_list_item_green.png")));
		listItemSkin.add(
				"wait_up",
				new Texture(Gdx.files
						.internal("data/Images/Menu/games_list_item_red.png")));
		listItemSkin.add(
				"wait_down",
				new Texture(Gdx.files
						.internal("data/Images/Menu/games_list_item_red.png")));
		listItemSkin.add(
				"surrender_up",
				new Texture(Gdx.files
						.internal("data/Images/Menu/button_surrender.png")));
		listItemSkin
				.add("surrender_down",
						new Texture(
								Gdx.files
										.internal("data/Images/Menu/button_surrender_pressed.png")));

		TextButtonStyle playStyle = new TextButtonStyle();
		playStyle.font = listItemSkin.getFont("font");
		playStyle.up = listItemSkin.getDrawable("play_up");
		playStyle.down = listItemSkin.getDrawable("play_down");
		listItemSkin.add("playStyle", playStyle);

		TextButtonStyle waitStyle = new TextButtonStyle();
		waitStyle.font = listItemSkin.getFont("font");
		waitStyle.up = listItemSkin.getDrawable("wait_up");
		waitStyle.down = listItemSkin.getDrawable("wait_down");
		listItemSkin.add("waitStyle", waitStyle);

		TextButtonStyle surrenderStyle = new TextButtonStyle();
		surrenderStyle.font = listItemSkin.getFont("font");
		surrenderStyle.up = listItemSkin.getDrawable("surrender_up");
		surrenderStyle.down = listItemSkin.getDrawable("surrender_down");
		listItemSkin.add("surrenderStyle", surrenderStyle);

		TextButtonStyle innerStyle = new TextButtonStyle();
		innerStyle.font = listItemSkin.getFont("font");
		innerStyle.up = listItemSkin.getDrawable("button_orange");
		innerStyle.down = listItemSkin.getDrawable("button_orange_pressed");
		listItemSkin.add("innerButtonStyle", innerStyle);

		TextButtonStyle outterStyle = new TextButtonStyle();
		outterStyle.font = listItemSkin.getFont("font");
		outterStyle.up = listItemSkin.getDrawable("outer_button_orange");
		outterStyle.down = listItemSkin.getDrawable("outer_button_orange_pressed");
		listItemSkin.add("outterButtonStyle", outterStyle);
	}

	public void listGamesError(String message) {
		System.out.println(message);
		GameEngine.hideLoading();
	}

	public void enableRandomSuccess() {
		btnNewRandom.setText("New random game");
		GameEngine.hideLoading();
	}

	public void enableRandomError(String message) {
		System.out.println(message);
		GameEngine.hideLoading();
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
