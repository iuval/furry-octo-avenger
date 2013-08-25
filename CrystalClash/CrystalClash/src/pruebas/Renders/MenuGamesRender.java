package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.MenuGames;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Renders.helpers.UIHelper;
import pruebas.Renders.helpers.ui.GameListItem;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuGamesRender extends MenuRender {
	private static MenuGamesRender instance;
	private TweenManager tweenManager;

	private MenuGames controller;
	private ScrollPane scrollPane;
	private Image gamesImage;

	private VerticalGroup list;
	private GameListItem[] gamesList;

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
	public void act(float delta) {
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
		tweenManager.update(delta);
		super.act(delta);
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		lblHeading.setText("Welcome " + GameController.getInstancia().getUser().getNick());
		return t.beginParallel()
				.push(Tween.to(lblHeading, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(50))
				.push(Tween.to(btnLogOut, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(CrystalClash.HEIGHT - btnLogOut.getHeight() - 10))
				.push(Tween.to(scrollPane, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(0))
				.end();
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t.beginParallel()
				.push(Tween.to(lblHeading, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(-CrystalClash.WIDTH))
				.push(Tween.to(btnLogOut, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(CrystalClash.HEIGHT))
				.push(Tween.to(scrollPane, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(CrystalClash.WIDTH))
				.end();
	}

	private void load() {
		initSkin();

		lblHeading = new Label("Welcome "
				+ GameController.getInstancia().getUser().getNick(),
				new LabelStyle(UIHelper.getFont(), Color.WHITE));
		lblHeading.setPosition(-CrystalClash.WIDTH, CrystalClash.HEIGHT - 50);
		addActor(lblHeading);

		btnLogOut = new TextButton("Log Out", UIHelper.getButtonStyle());
		btnLogOut.setPosition(CrystalClash.WIDTH - btnLogOut.getWidth() - 50,
				CrystalClash.HEIGHT);
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
				.setBounds(CrystalClash.WIDTH, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT - 80);
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

		Group inviteButtons = new Group();
		inviteButtons.setBounds(0, 0, list.getWidth(), 160);

		btnNewRandom = new TextButton("New random game", UIHelper.getOuterButtonStyle());
		btnNewRandom.setBounds(0, 0, inviteButtons.getWidth() / 2, 160);
		btnNewRandom.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((TextButton) event.getListenerActor()).setText("Loading...");
				controller.enableRandom();
			}
		});
		inviteButtons.addActor(btnNewRandom);

		btnNewInvite = new TextButton("Invite friend", UIHelper.getOuterButtonStyle());
		btnNewInvite.setBounds(inviteButtons.getWidth() / 2, 0, inviteButtons.getWidth() / 2, 160);
		inviteButtons.addActor(btnNewInvite);

		list.addActorAfter(menuImage, inviteButtons);

		refreshMessagePull = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/RefreshList/refresh_message_pull.png")));
		refreshMessagePull.setVisible(false);
		addActor(refreshMessagePull);

		refreshMessageRelease = new Image(new Texture(
				Gdx.files.internal("data/Images/Menu/RefreshList/refresh_message_release.png")));
		refreshMessageRelease.setVisible(false);
		addActor(refreshMessageRelease);
	}

	private void loadGameList() {
		controller.getGamesList();
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
					games[i][2], games[i][3], games[i][4],
					listItemSkin, surrenderListener,
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
				controller.getGameTurn(((GameListItem) event.getListenerActor()
						.getParent()).gameId, ((GameListItem) event.getListenerActor()
						.getParent()).turn);
			}
		};

		listItemSkin = new Skin();
		listItemSkin.add("font", UIHelper.getFont());
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

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
