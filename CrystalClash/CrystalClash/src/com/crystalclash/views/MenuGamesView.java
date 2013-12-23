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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.crystalclash.CrystalClash;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.MUSIC;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.MenuGames;
import com.crystalclash.entities.User;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.ParallaxRender;
import com.crystalclash.renders.TutorialInvitation;
import com.crystalclash.renders.helpers.EmblemHelper;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.BaseBox;
import com.crystalclash.renders.helpers.ui.BaseBox.BoxButtons;
import com.crystalclash.renders.helpers.ui.BoxCallback;
import com.crystalclash.renders.helpers.ui.EmblemList;
import com.crystalclash.renders.helpers.ui.GameListItem;
import com.crystalclash.renders.helpers.ui.GamesLoadCallback;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.util.I18n;

public class MenuGamesView extends InputView {
	private GamesLoadCallback loadCallback;

	private static MenuGamesView instance;

	private MenuGames controller;

	private VerticalGroup list;
	private GameListItem[] gamesList;

	private Label lblUserV;
	private Label lblUserL;
	private Label lblUserD;
	private Label lblUserName;
	private Image imgEmblem;

	private Group grpNewRandom;
	private Group grpProfile;
	private Group grpTutorial;
	private Group grpStory;

	private TextButton btnNewRandom;
	private TextButton btnPlayTutorial;
	private TextButton btnViewStory;

	private Label lblNewRandomText;

	private InputListener surrenderListener;
	private InputListener ackSurrenderListener;
	private InputListener playListener;
	private Skin skin;

	private TutorialInvitation tutoInv;
	private TextureRegion txtCcolumn;

	float last_touch_down_y = 0f;
	float y_move_speed = 0f;
	float y_move_accel = 0.95f;
	float max_list_y = 0f;
	float min_list_y = 0f;
	float min_list_over_y = -400f;
	boolean isPressing = false;
	boolean isOverflowing = false;

	private Group grpRefresh;
	private Image imgRefreshArrow;
	private Label lblRefreshMessage;
	private boolean isTryingToRefresh = false;
	private boolean showPullDown = false;
	private boolean showRelease = false;
	private float pullDistance = 0;
	private float releaseDistance = 0;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		isPressing = true;
		last_touch_down_y = screenY;
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		isPressing = false;
		return super.touchUp(screenX, screenY, pointer, button);
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
		return (y < min_list_over_y || y > max_list_y);
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
		lblUserV.setText(String.valueOf(GameController.getUser().getVictoryCount()));
		lblUserL.setText(String.valueOf(GameController.getUser().getLostCount()));
		lblUserD.setText(String.valueOf(GameController.getUser().getDrawCount()));
		lblUserName.setText(GameController.getUser().getName());
		imgEmblem.setDrawable(new TextureRegionDrawable(EmblemHelper.getEmblem(GameController.getUser().getEmblem())));

		list.clear();

		if (!GameController.isTutorialDone()) {
			loadTutorial();
		}
	}

	public void loadGameList(GamesLoadCallback callback) {
		controller.getGamesList();
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
	}

	private void loadTutorial() {
		tutoInv = new TutorialInvitation();
		addActor(tutoInv);
	}

	// SERVER DRIVER CALLBACKS --------------------------------------------
	public void listGamesSuccess(String[][] games) {
		list.clear();
		max_list_y = 0;
		if (gamesList != null) {
			for (int i = 0; i < gamesList.length; i++) {
				gamesList[i].remove();
			}
		}
		list.addActor(grpProfile);

		gamesList = new GameListItem[games.length];

		GameListItem listingItem;
		GameListItem canPlayItem = null;
		for (int i = 0, len = games.length; i < len; i++) {
			listingItem = new GameListItem(games[i][0], games[i][1], games[i][2], games[i][3], Integer.parseInt(games[i][5]), games[i][4],
					games[i][6].equals("true"), skin, surrenderListener, ackSurrenderListener,
					playListener);
			gamesList[i] = listingItem;

			if (canPlayItem == null) {
				if (games[i][4].equals("play")) {
					canPlayItem = listingItem;
				}
				list.addActor(listingItem);
			} else {
				if (games[i][4].equals("play")) {
					list.addActorBefore(canPlayItem, listingItem);
				} else {
					list.addActorAfter(canPlayItem, listingItem);
				}
			}
			max_list_y += listingItem.getHeight();
		}
		list.addActor(grpNewRandom);
		list.addActor(grpTutorial);
		list.addActor(grpStory);

		max_list_y += grpNewRandom.getHeight();
		max_list_y += grpTutorial.getHeight();
		max_list_y += grpStory.getHeight();

		list.addActor(new Image(txtCcolumn));

		GameEngine.hideLoading();
		loadCallback.onFinish();
	}

	private void initSkin() {
		txtCcolumn = ResourceHelper.getTexture("menu/games_list/column_stack");

		final BoxCallback surrenderCallback = new BoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == BoxCallback.YES) {
					controller.surrenderGame(data.toString());
					loadGameList(null);
				}
			}
		};

		// Listeners
		surrenderListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageBox.build()
						.setUserData(((GameListItem) event.getListenerActor().getParent()).gameId)
						.setMessage("menu_games_surender", BoxButtons.Two)
						.setCallback(surrenderCallback)
						.show();
			}
		};

		ackSurrenderListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.surrenderGame(((GameListItem) event.getListenerActor().getParent()).gameId);
			}
		};

		playListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameListItem item = ((GameListItem) event.getListenerActor().getParent());
				GameController.setEnemyUser(new User("", "", item.playerName, item.emblem, 0, 0, 0));
				controller.getGameTurn(item.gameId);
			}
		};

		skin = new Skin();
		skin.add("small_font", ResourceHelper.getSmallFont());
		skin.add("big_font", ResourceHelper.getBigFont());
		skin.add("play_up", ResourceHelper.getTexture("menu/games_list/flag_green"));
		skin.add("wait_up", ResourceHelper.getTexture("menu/games_list/flag_red"));
		skin.add("surrended_up", ResourceHelper.getTexture("menu/games_list/flag_surrended"));
		skin.add("surrender_up", ResourceHelper.getTexture("menu/games_list/surrender"));
		skin.add("sound_off_up", ResourceHelper.getTexture("menu/games_list/sound_off"));
		skin.add("sound_off_down", ResourceHelper.getTexture("menu/games_list/sound_off_pressed"));
		skin.add("sound_on_up", ResourceHelper.getTexture("menu/games_list/sound_on"));
		skin.add("logout_up", ResourceHelper.getTexture("menu/games_list/logout"));
		skin.add("logout_down", ResourceHelper.getTexture("menu/games_list/logout_pressed"));
		skin.add("background", ResourceHelper.getTexture("menu/games_list/item_stack"));

		LabelStyle style = new LabelStyle();
		style.font = skin.getFont("big_font");
		style.fontColor = Color.BLACK;
		skin.add("lblStyle", style);

		ButtonStyle playStyle = new ButtonStyle();
		playStyle.up = skin.getDrawable("play_up");
		playStyle.down = skin.getDrawable("play_up");
		skin.add("playStyle", playStyle);

		ButtonStyle waitStyle = new ButtonStyle();
		waitStyle.up = skin.getDrawable("wait_up");
		waitStyle.down = skin.getDrawable("wait_up");
		skin.add("waitStyle", waitStyle);
		
		ButtonStyle surrendedStyle = new ButtonStyle();
		surrendedStyle.up = skin.getDrawable("surrended_up");
		surrendedStyle.down = skin.getDrawable("surrended_up");
		skin.add("surrendedStyle", surrendedStyle);

		ButtonStyle surrenderStyle = new ButtonStyle();
		surrenderStyle.up = skin.getDrawable("surrender_up");
		surrenderStyle.down = skin.getDrawable("surrender_up");
		skin.add("surrenderStyle", surrenderStyle);

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

		lblUserName = new Label(u.getName(), skin, "big_font", Color.BLACK);
		lblUserName.setPosition(290, 215);
		lblUserName.setSize(460, 60);
		lblUserName.setAlignment(Align.center);
		grpProfile.addActor(lblUserName);

		imgEmblem = new Image(EmblemHelper.getEmblem(u.getEmblem()));
		imgEmblem.setPosition(50, 95);
		imgEmblem.setSize(160, 160);
		grpProfile.addActor(imgEmblem);
		imgEmblem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				final EmblemList emblemList = new EmblemList();
				BaseBox box = new BaseBox(emblemList);
				box.twoButtonsLayout("Save", "Back");
				box.setCallback(new BoxCallback() {
					@Override
					public void onEvent(int type, Object data) {
						if (type == YES) {
							GameController.getUser().setEmblem(emblemList.getSelectedEmblem());
							GameController.getUser().update();
							imgEmblem.setDrawable(new TextureRegionDrawable(EmblemHelper.getEmblem(emblemList.getSelectedEmblem())));
						}
					}
				});
				box.show();
			}
		});

		lblUserV = new Label(u.getVictoryCount() + "", skin, "big_font", Color.BLACK);
		lblUserV.setPosition(365, 115);
		Label lblUserVTittle = new Label("Victories", skin, "small_font", Color.BLACK);
		lblUserVTittle.setPosition(330, 162);
		grpProfile.addActor(lblUserV);
		grpProfile.addActor(lblUserVTittle);

		lblUserL = new Label(u.getLostCount() + "", skin, "big_font", Color.BLACK);
		lblUserL.setPosition(540, 110);
		Label lblUserLTittle = new Label("Defeats", skin, "small_font", Color.BLACK);
		lblUserLTittle.setPosition(505, 158);
		grpProfile.addActor(lblUserL);
		grpProfile.addActor(lblUserLTittle);

		lblUserD = new Label(u.getDrawCount() + "", skin, "big_font", Color.BLACK);
		lblUserD.setPosition(675, 110);
		Label lblUserDTittle = new Label("Draws", skin, "small_font", Color.BLACK);
		lblUserDTittle.setPosition(655, 158);
		grpProfile.addActor(lblUserD);
		grpProfile.addActor(lblUserDTittle);

		TextButtonStyle btnStyle = new TextButtonStyle();
		btnStyle.font = skin.getFont("big_font");

		// New Random Group
		grpNewRandom = new Group();
		Image imgNewRandom = new Image(ResourceHelper.getTexture("menu/games_list/new_battle_stack"));
		grpNewRandom.addActor(imgNewRandom);
		grpNewRandom.setSize(imgNewRandom.getWidth(), imgNewRandom.getHeight());

		lblNewRandomText = new Label("New Game", skin, "lblStyle");
		lblNewRandomText.setPosition(510, 210);
		grpNewRandom.addActor(lblNewRandomText);

		btnNewRandom = new TextButton("", btnStyle);
		btnNewRandom.setPosition(253, 28);
		btnNewRandom.setSize(588, 302);
		btnNewRandom.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				lblNewRandomText.setText("   Loading...");
				controller.enableRandom();
			}
		});
		grpNewRandom.addActor(btnNewRandom);

		// Play Tutorial Group
		grpTutorial = new Group();
		Image imgTutorial = new Image(ResourceHelper.getTexture("menu/games_list/tutorial_stack"));
		grpTutorial.addActor(imgTutorial);
		grpTutorial.setSize(imgTutorial.getWidth(), imgTutorial.getHeight());

		Label tutorialText = new Label("Play\nTutorial", skin, "lblStyle");
		tutorialText.setPosition(360, 140);
		grpTutorial.addActor(tutorialText);

		btnPlayTutorial = new TextButton("", btnStyle);
		btnPlayTutorial.setPosition(253, 28);
		btnPlayTutorial.setSize(588, 302);
		btnPlayTutorial.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameEngine.getInstance().openTutorial();
			}
		});
		grpTutorial.addActor(btnPlayTutorial);

		// View Story Group
		grpStory = new Group();
		Image imgStory = new Image(ResourceHelper.getTexture("menu/games_list/story_stack"));
		grpStory.addActor(imgStory);
		grpStory.setSize(imgStory.getWidth(), imgStory.getHeight());

		Label storyText = new Label("Learn\nthe Story", skin, "lblStyle");
		storyText.setPosition(360, 140);
		grpStory.addActor(storyText);

		btnViewStory = new TextButton("", btnStyle);
		btnViewStory.setPosition(253, 28);
		btnViewStory.setSize(588, 302);
		btnViewStory.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

			}
		});
		grpStory.addActor(btnViewStory);

		grpRefresh = new Group();
		grpRefresh.setSize(CrystalClash.WIDTH, 200);
		grpRefresh.setVisible(false);
		
		imgRefreshArrow = new Image(ResourceHelper.getTexture("menu/refresh_list/arrow"));
		imgRefreshArrow.setOrigin(imgRefreshArrow.getWidth() / 2 , imgRefreshArrow.getHeight() / 2);
		imgRefreshArrow.setPosition(CrystalClash.WIDTH / 4 - imgRefreshArrow.getWidth() / 2, grpRefresh.getHeight() / 2 - imgRefreshArrow.getHeight() / 2);
		grpRefresh.addActor(imgRefreshArrow);
		lblRefreshMessage = new Label(I18n.t("menu_games_pull"), skin, "lblStyle");
		lblRefreshMessage.setPosition(imgRefreshArrow.getX() + imgRefreshArrow.getWidth() + 100, grpRefresh.getHeight() / 2 - lblRefreshMessage.getHeight() / 2);
		grpRefresh.addActor(lblRefreshMessage);
		
		addActor(grpRefresh);
		
		pullDistance = 100;
		releaseDistance = pullDistance * 2;
	}

	public void updateListGameSurrender(String id) {
		lblUserV.setText(String.valueOf(GameController.getUser().getVictoryCount()));
		lblUserL.setText(String.valueOf(GameController.getUser().getLostCount()));
		int itemIndex = -1;
		for (int i = 0; i < gamesList.length; i++) {
			if (gamesList[i].gameId.equals(id)) {
				itemIndex = i;
				break;
			}
		}
		if (itemIndex != -1) {
			gamesList[itemIndex].remove();
		}
	}

	public void listGamesError(String message) {
		MessageBox.build()
				.setText(message)
				.setCallback(null)
				.show();
	}

	public void enableRandomSuccess(String[] game) {
		if (game != null) {
			GameListItem listingItem = new GameListItem(game[0], game[1], game[2], game[3], Integer.parseInt(game[5]), game[4],
					false,
					skin, surrenderListener, ackSurrenderListener,
					playListener);
			list.addActor(listingItem);
		}
		lblNewRandomText.setText("New game");
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
		updateEmblems();
		if (!GameController.isTutorialDone()) {
			tutoInv.show();
		}
	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}

	private void updateEmblems() {
		for (int i = 0; i < gamesList.length; i++) {
			gamesList[i].loadEmblem();
		}
	}

	@Override
	public void act(float delta) {
		updateList(delta);

		updateRefresh();

		ParallaxRender.getInstance().updateY(list.getY());

		super.act(delta);
	}

	private void updateRefresh() {
		if (isPressing) {
			if (!isTryingToRefresh && list.getY() < -pullDistance) {
				showPullDown = true;
				showRelease = false;
				isTryingToRefresh = true;
			} else if (isTryingToRefresh && list.getY() < -releaseDistance) {
				showPullDown = false;
				showRelease = true;
			} else if (isTryingToRefresh && list.getY() > -releaseDistance) {
				if (isTryingToRefresh && list.getY() > -pullDistance) {
					isTryingToRefresh = false;
				} else {
					showPullDown = true;
					showRelease = false;
				}
			}
		} else {
			if (isTryingToRefresh) {
				if (showRelease) {
					loadGameList(new GamesLoadCallback() {

						@Override
						public void onFinish() {
							updateEmblems();
						}
					});
				}
				isTryingToRefresh = false;
			}
		}

		if (isTryingToRefresh) {
			if (showRelease) {
				lblRefreshMessage.setText(I18n.t("menu_games_release"));
				imgRefreshArrow.setRotation(180);
			} else if (showPullDown) {
				lblRefreshMessage.setText(I18n.t("menu_games_pull"));
				imgRefreshArrow.setRotation(0);
			}
			
			grpRefresh.setY(list.getTop());
			grpRefresh.setVisible(true);
		} else {
			grpRefresh.setVisible(false);
		}
	}

	private void updateList(float delta) {
		if (list.getY() < min_list_y) {
			if (!isPressing) {
				y_move_speed = (min_list_y - list.getY()) * 10;
			}
		}

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
