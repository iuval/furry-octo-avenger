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
import com.crystalclash.renders.helpers.ui.GamesLoadCallback;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.renders.helpers.ui.MessageBox.Buttons;
import com.crystalclash.renders.helpers.ui.MessageBoxCallback;

public class MenuGamesView extends InputView {
	private GamesLoadCallback loadCallback;

	private static MenuGamesView instance;

	private MenuGames controller;

	private VerticalGroup list;
	private GameListItem[] gamesList;

	private Group grpNewRandom;
	private Group grpProfile;

	private InputListener surrenderListener;
	private InputListener playListener;
	private Skin skin;

	private TextButton btnNewRandom;

	private TutorialInvitation tutoInv;
	private TextureRegion txtCcolumn;

	float last_touch_down_y = 0f;
	float y_move_speed = 0f;
	float y_move_accel = 0.95f;
	float max_list_y = 0f;
	float min_list_y = 0f;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		last_touch_down_y = screenY;
		return super.touchDown(screenX, screenY, pointer, button);
	}

	public boolean touchDragged(int x, int y, int pointer) {
		moveList(y);
		return false;
	}

	private void moveList(int touch_y) {
		y_move_speed += (last_touch_down_y - touch_y) * 10;
		last_touch_down_y = touch_y;
		if (y_move_speed > 2000)
			y_move_speed = 2000;
		if (y_move_speed < -2000)
			y_move_speed = -2000;
	}

	private boolean yOutOfLimit(float y) {
		return (y < min_list_y || y > max_list_y);
	}

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

	}

	public void loadList(GamesLoadCallback callback) {
		loadGameList();
		loadCallback = callback;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		AudioManager.playMusic(MUSIC.menu);
		return t;
		// .push(Tween.to(this, ActorAccessor.ALPHA,
		// CrystalClash.NORMAL_ANIMATION_SPEED).target(1));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t;
		// .push(Tween.to(this, ActorAccessor.ALPHA,
		// CrystalClash.NORMAL_ANIMATION_SPEED).target(0));
	}

	private void load() {
		list = new VerticalGroup();
		list.setBounds(0, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT);
		addActor(list);

		initSkin();

		if (!GameController.isTutorialDone()) {
			loadTutorial();
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
		list.clear();
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
			max_list_y += listingItem.getHeight();
		}
		list.addActor(grpNewRandom);
		list.addActor(new Image(txtCcolumn));

		GameEngine.hideLoading();
		loadCallback.onFinish();
	}

	private void initSkin() {
		txtCcolumn = ResourceHelper.getTexture("menu/games_list/column_stack");

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
		skin.add("background", ResourceHelper.getTexture("menu/games_list/item_stack"));

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
		Image imgNewRandom = new Image(ResourceHelper.getTexture("menu/games_list/new_battle_stack"));
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
		Image imgProfile = new Image(ResourceHelper.getTexture("menu/games_list/user_stats_stack"));
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
		updateList(delta);

		ParallaxRender.getInstance().updateY(list.getY());

		super.act(delta);
	}

	private void updateList(float delta) {
		if (Math.abs(y_move_speed) > 0.5f) {
			float new_y = list.getY() + y_move_speed * delta;
			updateListSpeed();
			if (!yOutOfLimit(new_y))
				list.setY(new_y);
		}
	}

	private void updateListSpeed() {
		y_move_speed *= y_move_accel;
		if (Math.abs(y_move_speed) <= 0.5)
			y_move_speed = 0;
	}
}
