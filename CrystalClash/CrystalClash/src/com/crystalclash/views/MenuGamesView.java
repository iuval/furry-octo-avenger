package com.crystalclash.views;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.MUSIC;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.MenuGames;
import com.crystalclash.entities.User;
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

	private Group grpScroll;
	private Group grpColumns;
	private VerticalGroup list;
	private GameListItem[] gamesList;

	private final TextureRegion txtColumn;

	private Group grpNewRandom;
	private Group grpProfile;

	private InputListener surrenderListener;
	private InputListener playListener;
	private Skin skin;

	private TextButton btnNewRandom;

	private TutorialInvitation tutoInv;

	public MenuGamesView(MenuGames menu) {
		this.controller = menu;

		txtColumn = ResourceHelper.getTexture("menu/column2");
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
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		AudioManager.playMusic(MUSIC.menu);
		return t;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		superScroll.scrollPane.setScrollY(0);
		return t;
	}

	private void load() {
		grpScroll = new Group();
		grpScroll.setBounds(0, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		grpColumns = new Group();
		grpColumns.setPosition(970, CrystalClash.HEIGHT);
		grpColumns.addActor(new Image(ResourceHelper.getTexture("menu/column1")));
		grpScroll.addActor(grpColumns);

		list = new VerticalGroup();
		list.setWidth(1047);
		list.setPosition(0, CrystalClash.HEIGHT);
		grpScroll.addActor(list);

		// put the table inside a scrollpane
		superScroll = new SuperScrollPane(grpScroll, new SuperScrollPaneRefreshCallback() {
			@Override
			public void refresh() {
				loadGameList();
			}
		});
		superScroll.setBounds(0, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT);
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
		list.addActor(grpProfile);

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
		list.addActor(grpNewRandom);

		float colH = -CrystalClash.HEIGHT;
		Image col;
		while (colH > -superScroll.scrollPane.getHeight()) {
			col = new Image(txtColumn);
			col.setY(colH);
			grpColumns.addActor(col);
			colH -= CrystalClash.HEIGHT;
		}
		col = new Image(txtColumn);
		col.setY(colH);
		grpColumns.addActor(col);

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
		skin.add("sound_off_up", ResourceHelper.getTexture("menu/games_list/sound_off"));
		skin.add("sound_off_down", ResourceHelper.getTexture("menu/games_list/sound_off_pressed"));
		skin.add("sound_on_up", ResourceHelper.getTexture("menu/games_list/sound_on"));
		skin.add("logout_up", ResourceHelper.getTexture("menu/games_list/logout"));
		skin.add("logout_down", ResourceHelper.getTexture("menu/games_list/logout_pressed"));
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

		grpNewRandom = new Group();
		Image imgNewRandom = new Image(ResourceHelper.getTexture("menu/games_list/new_battle"));
		grpNewRandom.addActor(imgNewRandom);
		grpNewRandom.setSize(imgNewRandom.getWidth(), imgNewRandom.getHeight());

		TextButtonStyle newRandomStyle = new TextButtonStyle();
		newRandomStyle.font = skin.getFont("font");

		btnNewRandom = new TextButton("New random game", newRandomStyle);
		btnNewRandom.setPosition(400, 180);
		btnNewRandom.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((TextButton) event.getListenerActor()).setText("Loading...");
				controller.enableRandom();
			}
		});
		grpNewRandom.addActor(btnNewRandom);

		grpProfile = new Group();
		Image imgProfile = new Image(ResourceHelper.getTexture("menu/games_list/player_info"));
		grpProfile.addActor(imgProfile);
		grpProfile.setSize(imgProfile.getWidth(), imgProfile.getHeight());

		ButtonStyle soundStyle = new ButtonStyle();
		soundStyle.up = skin.getDrawable("sound_off_up");
		soundStyle.down = skin.getDrawable("sound_off_down");
		soundStyle.checked = skin.getDrawable("sound_on_up");
		final Button btnSound = new Button(soundStyle);
		btnSound.setPosition(840, 170);
		btnSound.setChecked(AudioManager.getVolume() == 0);
		btnSound.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AudioManager.toogleVolume();
				btnSound.setChecked(AudioManager.getVolume() == 0);
			}
		});
		grpProfile.addActor(btnSound);

		ButtonStyle logoutStyle = new ButtonStyle();
		logoutStyle.up = skin.getDrawable("logout_up");
		logoutStyle.down = skin.getDrawable("logout_down");
		final Button btnLogout = new Button(logoutStyle);
		btnLogout.setPosition(840, 70);
		btnLogout.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("LogOut");
				controller.logOut();
			}
		});
		grpProfile.addActor(btnLogout);

		User u = GameController.getUser();

		Label lblUserName = new Label(u.getName(), skin, "font", Color.WHITE);
		lblUserName.setPosition(290, 200);
		lblUserName.setAlignment(Align.center);
		grpProfile.addActor(lblUserName);

		Label lblUserD = new Label("40", skin, "font", Color.WHITE);
		lblUserD.setPosition(370, 90);
		grpProfile.addActor(lblUserD);

		Label lblUserV = new Label("150", skin, "font", Color.WHITE);
		lblUserV.setPosition(500, 110);
		grpProfile.addActor(lblUserV);

		Label lblUserL = new Label("20", skin, "font", Color.WHITE);
		lblUserL.setPosition(630, 90);
		grpProfile.addActor(lblUserL);

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
		System.out.println("" + superScroll.getY());
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
