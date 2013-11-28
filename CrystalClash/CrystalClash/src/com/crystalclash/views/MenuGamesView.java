package com.crystalclash.views;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.MUSIC;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.MenuGames;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.ParallaxRender;
import com.crystalclash.renders.TutorialInvitation;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.GameListItem;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.renders.helpers.ui.MessageBox.Buttons;
import com.crystalclash.renders.helpers.ui.MessageBoxCallback;
import com.crystalclash.renders.helpers.ui.SuperScrollPane;
import com.crystalclash.renders.helpers.ui.SuperScrollPaneRefreshCallback;

public class MenuGamesView extends InputView {
	private static MenuGamesView instance;

	private MenuGames controller;
	private SuperScrollPane superScroll;

	private VerticalGroup list;
	private GameListItem[] gamesList;

	private TextButton btnProfile;
	private TextButton btnLogOut;

	private InputListener surrenderListener;
	private InputListener playListener;
	private Skin skin;

	private TextButton btnNewRandom;
	// private TextButton btnNewInvite;
	private TextButton btnMusic;

	private TutorialInvitation tutoInv;

	public MenuGamesView(MenuGames menu) {
		this.controller = menu;

		load();
	}

	public static MenuGamesView getInstance(MenuGames menu) {
		if (instance == null)
			instance = new MenuGamesView(menu);

		return instance;
	}

	@Override
	public void init() {
		// GameController.setTutorialNotDone();
		ParallaxRender.getInstance().setY(0);
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		AudioManager.playMusic(MUSIC.menu);
		Timeline aux = Timeline.createParallel();

		btnProfile.setText("Welcome " + GameController.getUser().getName());
		aux.push(Tween.to(btnLogOut, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
				.target(CrystalClash.HEIGHT - btnLogOut.getHeight() - 10))
				.push(Tween.to(btnMusic, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT - btnMusic.getHeight() - 10));
		return t.push(aux);
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		superScroll.scrollPane.setScrollY(0);
		t.beginParallel()
				.push(Tween.to(btnLogOut, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT))
				.push(Tween.to(btnMusic, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT));
		return t.end();
	}

	private void load() {
		btnLogOut = new TextButton("Log Out", ResourceHelper.getButtonStyle());
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

		btnMusic = new TextButton(String.format("Music %s", AudioManager.getVolume() == 0 ? "OFF" : "ON"),
				ResourceHelper.getButtonStyle());
		btnMusic.setPosition(CrystalClash.WIDTH - btnLogOut.getWidth() - 100 - btnMusic.getWidth(),
				CrystalClash.HEIGHT);
		btnMusic.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnMusic.setText(String.format("Music %s", (AudioManager.toogleVolume() == 0 ? "OFF" : "ON")));
			}
		});
		addActor(btnMusic);

		list = new VerticalGroup();
		list.setWidth(CrystalClash.WIDTH);

		// put the table inside a scrollpane
		superScroll = new SuperScrollPane(list, new SuperScrollPaneRefreshCallback() {
			@Override
			public void refresh() {
				loadGameList();
			}
		});
		superScroll.setBounds(-120, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT);
		superScroll.scrollPane.setScrollingDisabled(true, false);
		superScroll.scrollPane.setOverscroll(false, true);
		superScroll.scrollPane.setSmoothScrolling(true);
		superScroll.scrollPane.setupOverscroll(CrystalClash.HEIGHT, 4000, 5000);
		superScroll.scrollPane.setForceScroll(false, true);
		superScroll.scrollPane.invalidate();
		addActor(superScroll);

		initSkin();
		superScroll.load();

		if (!GameController.isTutorialDone()) {
			loadTutorial();
		} else {
			loadGameList();
		}
	}

	private void loadTutorial() {
		tutoInv = new TutorialInvitation();
		addActor(tutoInv);
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
		list.addActor(btnProfile);

		gamesList = new GameListItem[games.length];

		GameListItem listingItem;
		GameListItem canPlayItem = null;
		for (int i = 0, len = games.length; i < len; i++) {
			listingItem = new GameListItem(games[i][0], games[i][1], games[i][2], games[i][3], games[i][4],
					skin, surrenderListener,
					playListener);
			gamesList[i] = listingItem;

			if (canPlayItem == null) {
				if (games[i][4].equals("play")) {
					canPlayItem = listingItem;
				}
				list.addActor(listingItem);
			} else {
				if (games[i][4].equals("play")) {
					list.addActor(listingItem);
				} else {
					list.addActorAfter(canPlayItem, listingItem);
				}
			}
		}
		list.addActor(btnNewRandom);
		GameEngine.hideLoading();
	}

	private void initSkin() {
		final MessageBoxCallback surrenderCallback = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {
					controller.surrenderGame(data.toString());
					loadGameList();
				}
			}
		};
		// Listeners
		surrenderListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageBox.build()
						.setUserData(((GameListItem) event.getListenerActor().getParent()).gameId)
						.setMessage("menu_ganmes_surender", Buttons.Two)
						.setCallback(surrenderCallback)
						.show();
			}
		};

		playListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.getGameTurn(((GameListItem) event.getListenerActor().getParent()).gameId);
			}
		};

		skin = new Skin();
		skin.add("font", ResourceHelper.getBigFont());
		skin.add("play_up", ResourceHelper.getTexture("menu/games_list/flag_green"));
		skin.add("play_down", ResourceHelper.getTexture("menu/games_list/flag_green"));
		skin.add("wait_up", ResourceHelper.getTexture("menu/games_list/flag_red"));
		skin.add("wait_down", ResourceHelper.getTexture("menu/games_list/flag_red"));
		skin.add("surrender_up", ResourceHelper.getTexture("menu/games_list/surrender"));
		skin.add("surrender_down", ResourceHelper.getTexture("menu/games_list/surrender"));
		skin.add("new_battle_up", ResourceHelper.getTexture("menu/games_list/new_battle"));
		skin.add("new_battle_down", ResourceHelper.getTexture("menu/games_list/new_battle"));
		skin.add("profile_up", ResourceHelper.getTexture("menu/games_list/player_info"));
		skin.add("profile_down", ResourceHelper.getTexture("menu/games_list/player_info"));
		skin.add("background", ResourceHelper.getTexture("menu/games_list/item"));

		TextButtonStyle playStyle = new TextButtonStyle();
		playStyle.font = skin.getFont("font");
		playStyle.up = skin.getDrawable("play_up");
		playStyle.down = skin.getDrawable("play_down");
		skin.add("playStyle", playStyle);

		TextButtonStyle waitStyle = new TextButtonStyle();
		waitStyle.font = skin.getFont("font");
		waitStyle.up = skin.getDrawable("wait_up");
		waitStyle.down = skin.getDrawable("wait_down");
		skin.add("waitStyle", waitStyle);

		TextButtonStyle surrenderStyle = new TextButtonStyle();
		surrenderStyle.font = skin.getFont("font");
		surrenderStyle.up = skin.getDrawable("surrender_up");
		surrenderStyle.down = skin.getDrawable("surrender_down");
		skin.add("surrenderStyle", surrenderStyle);

		TextButtonStyle newRandomStyle = new TextButtonStyle();
		newRandomStyle.font = skin.getFont("font");
		newRandomStyle.up = skin.getDrawable("new_battle_up");
		newRandomStyle.down = skin.getDrawable("new_battle_down");

		btnNewRandom = new TextButton("New random game", newRandomStyle);
		btnNewRandom.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((TextButton) event.getListenerActor()).setText("Loading...");
				controller.enableRandom();
			}
		});

		TextButtonStyle profileStyle = new TextButtonStyle();
		profileStyle.font = skin.getFont("font");
		profileStyle.up = skin.getDrawable("profile_up");
		profileStyle.down = skin.getDrawable("profile_down");

		btnProfile = new TextButton("New random game", profileStyle);
	}

	public void listGamesError(String message) {
		MessageBox.build()
				.setText(message)
				.setCallback(null)
				.show();
	}

	public void enableRandomSuccess(String[] game) {
		if (game != null) {
			GameListItem listingItem = new GameListItem(game[0], game[1], game[2], game[3], game[4],
					skin, surrenderListener,
					playListener);
			// list.addActorAfter(gamesImage, listingItem);
			list.addActor(listingItem);
		}
		btnNewRandom.setText("New random game");
		GameEngine.hideLoading();
	}

	public void enableRandomError(String message) {
		MessageBox.build()
				.setText(message)
				.setCallback(null)
				.show();
	}

	// INPUT PROCESSOR--------------------------------------------
	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Keys.BACK) {
			controller.logOut();
		}
		return true;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shown() {
		if (!GameController.isTutorialDone()) {
			tutoInv.show();
		}
	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void act(float delta) {

		ParallaxRender.getInstance().updateY(superScroll.scrollPane.getScrollY());

		super.act(delta);
	}
}
