package com.crystalclash.views;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.MenuGames;
import com.crystalclash.renders.AnimatedGroup;
import com.crystalclash.renders.BlackScreen;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.GameListItem;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.renders.helpers.ui.MessageBoxCallback;
import com.crystalclash.renders.helpers.ui.SuperScrollPane;
import com.crystalclash.renders.helpers.ui.SuperScrollPaneRefreshCallback;

public class MenuGamesView extends InputView {
	private static MenuGamesView instance;

	private MenuGames controller;
	private SuperScrollPane superScroll;
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
	private TextButton btnMusic;

	private boolean isTutoInvVisible = false;
	private AnimatedGroup grpTutoInvitation;
	private TextButton btnPlayTutorial;
	private TextButton btnSkipTutorial;
	private Image imgFireArcher;
	private Image imgBalloon;
	private Label lblMessage;

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
		if (!GameController.isTutorialDone()) {
			loadTutorial();
		} else {
			loadGameList();
		}
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		AudioManager.playMusic("march for glory");
		Timeline aux = Timeline.createParallel();

		lblHeading.setText("Welcome " + GameController.getUser().getName());
		aux.push(Tween.to(lblHeading, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
				.target(50))
				.push(Tween.to(btnLogOut, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT - btnLogOut.getHeight() - 10))
				.push(Tween.to(btnMusic, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT - btnMusic.getHeight() - 10))
				.push(Tween.to(superScroll, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(0));
		return t.push(aux);
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		t.beginParallel()
				.push(Tween.to(lblHeading, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(-CrystalClash.WIDTH))
				.push(Tween.to(btnLogOut, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT))
				.push(Tween.to(btnMusic, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT))
				.push(Tween.to(superScroll, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.WIDTH));
		if (isTutoInvVisible) {
			BlackScreen.build().hide(t)
					.push(Tween.to(imgFireArcher, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(-imgFireArcher.getWidth()))
					.push(Tween.to(imgBalloon, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT + imgBalloon.getHeight()))
					.push(Tween.to(btnPlayTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(0 - btnPlayTutorial.getHeight()))
					.push(Tween.to(btnSkipTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(0 - btnPlayTutorial.getHeight() - btnSkipTutorial.getHeight()));
		}
		return t.end();
	}

	private void load() {
		initSkin();

		lblHeading = new Label(String.format("Welcome %s", GameController.getUser().getName()),
				new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblHeading.setPosition(-CrystalClash.WIDTH, CrystalClash.HEIGHT - 50);
		addActor(lblHeading);

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
		superScroll.setBounds(CrystalClash.WIDTH, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT - 80);
		superScroll.scrollPane.setScrollingDisabled(true, false);
		superScroll.scrollPane.setOverscroll(false, true);
		superScroll.scrollPane.setSmoothScrolling(true);
		superScroll.scrollPane.setupOverscroll(CrystalClash.HEIGHT, 4000, 5000);
		superScroll.scrollPane.setForceScroll(false, true);
		superScroll.scrollPane.invalidate();
		addActor(superScroll);
		gamesImage = new Image(ResourceHelper.getTexture("menu/current_games_header"));

		list.addActor(gamesImage);

		Image menuImage = new Image(ResourceHelper.getTexture("menu/new_games_header"));
		list.addActor(menuImage);

		Group inviteButtons = new Group();
		inviteButtons.setBounds(0, 0, list.getWidth(), 160);

		btnNewRandom = new TextButton("New random game", ResourceHelper.getOuterButtonStyle());
		btnNewRandom.setBounds(0, 0, inviteButtons.getWidth() / 2, 160);
		btnNewRandom.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((TextButton) event.getListenerActor()).setText("Loading...");
				controller.enableRandom();
			}
		});
		inviteButtons.addActor(btnNewRandom);

		btnNewInvite = new TextButton("Invite friend", ResourceHelper.getOuterButtonStyle());
		btnNewInvite.setBounds(inviteButtons.getWidth() / 2, 0, inviteButtons.getWidth() / 2, 160);
		inviteButtons.addActor(btnNewInvite);

		list.addActorAfter(menuImage, inviteButtons);

		superScroll.load();
	}

	private void loadTutorial() {
		grpTutoInvitation = new AnimatedGroup();
		grpTutoInvitation.setBounds(0, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		imgFireArcher = new Image(ResourceHelper.getTexture("tutorial/fire_archer"));
		imgFireArcher.setPosition(-imgFireArcher.getWidth(), -10);
		grpTutoInvitation.addActor(imgFireArcher);

		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(imgFireArcher, ActorAccessor.SCALE_Y, CrystalClash.REALLY_SLOW_ANIMATION_SPEED)
						.target(1.005f))
				.push(Tween.to(imgFireArcher, ActorAccessor.ROTATION, CrystalClash.REALLY_SLOW_ANIMATION_SPEED)
						.target(0.5f))
				.repeatYoyo(-1, 0));

		imgBalloon = new Image(ResourceHelper.getTexture("tutorial/message_balloon"));
		imgBalloon.setPosition(CrystalClash.WIDTH / 3, CrystalClash.HEIGHT + imgBalloon.getHeight());
		grpTutoInvitation.addActor(imgBalloon);

		lblMessage = new Label("Welcome " + GameController.getUser().getName() +
				"\n\nI can help you learn the\nbasics... Do you want me to?", new LabelStyle(ResourceHelper.getFont(), Color.BLACK));
		lblMessage.setPosition(imgBalloon.getX() + 145, imgBalloon.getTop() - 65);
		grpTutoInvitation.addActor(lblMessage);

		btnPlayTutorial = new TextButton("Lets Do It!", ResourceHelper.getOuterButtonStyle());
		btnPlayTutorial.setPosition(CrystalClash.WIDTH / 3 * 2 - btnPlayTutorial.getWidth() / 2 + 130, 0 - btnPlayTutorial.getHeight());
		btnPlayTutorial.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				lblMessage.setText("");
				controller.openTutorial();
			}
		});
		grpTutoInvitation.addActor(btnPlayTutorial);

		final MessageBoxCallback confirmation = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {
					closeTutorialInvitation();
					MessageBox.build().hide();
					isTutoInvVisible = false;
				} else {
					lblMessage.setText("");
					controller.openTutorial();
				}
			}
		};
		btnSkipTutorial = new TextButton("No, thx", ResourceHelper.getOuterSmallButtonStyle());
		btnSkipTutorial.setPosition(CrystalClash.WIDTH / 3 * 2 - btnSkipTutorial.getWidth() / 2 + 130,
				0 - btnPlayTutorial.getHeight() - btnSkipTutorial.getHeight());
		btnSkipTutorial.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageBox.build()
						.setMessage("It's not safe going to the battle field without training...\nAre you sure you don't want to try?")
						.twoButtonsLayout("Yes, i'm sure", "Lets do it!")
						.setCallback(confirmation)
						.setHideOnAction(false)
						.show();
			}
		});
		grpTutoInvitation.addActor(btnSkipTutorial);
	}

	private void closeTutorialInvitation() {
		GameController.setTutorialDone();
		GameEngine.start(BlackScreen.build().hide(Timeline.createParallel())
				.push(Tween.to(imgFireArcher, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(-imgFireArcher.getWidth()))
				.push(Tween.to(imgBalloon, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + imgBalloon.getHeight()))
				.push(Tween.to(btnPlayTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(0 - btnPlayTutorial.getHeight()))
				.push(Tween.to(btnSkipTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(0 - btnSkipTutorial.getHeight()))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						lblMessage.setPosition(imgBalloon.getX() + 50, imgBalloon.getTop() - 150);
						GameEngine.start(pushEnterAnimation(Timeline.createParallel()));
					}
				}));
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
			listingItem = new GameListItem(games[i][0], games[i][1], games[i][2], games[i][3], games[i][4],
					listItemSkin, surrenderListener,
					playListener);
			gamesList[i] = listingItem;
			list.addActorAfter(gamesImage, listingItem);
		}
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
						.setMessage("\"The wise warrior avoids the battle.\"\n- Sun Tzu")
						.twoButtonsLayout("Surrender", "Not yet!")
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

		listItemSkin = new Skin();
		listItemSkin.add("font", ResourceHelper.getFont());
		listItemSkin.add("play_up", ResourceHelper.getTexture("menu/games_list_item_green"));
		listItemSkin.add("play_down", ResourceHelper.getTexture("menu/games_list_item_green"));
		listItemSkin.add("wait_up", ResourceHelper.getTexture("menu/games_list_item_red"));
		listItemSkin.add("wait_down", ResourceHelper.getTexture("menu/games_list_item_red"));
		listItemSkin.add("surrender_up", ResourceHelper.getTexture("menu/button_surrender"));
		listItemSkin.add("surrender_down", ResourceHelper.getTexture("menu/button_surrender_pressed"));

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
		MessageBox.build()
				.setMessage(message)
				.oneButtonsLayout("OK...")
				.setCallback(null)
				.show();
	}

	public void enableRandomSuccess(String[] game) {
		if (game != null) {
			GameListItem listingItem = new GameListItem(game[0], game[1], game[2], game[3], game[4],
					listItemSkin, surrenderListener,
					playListener);
			list.addActorAfter(gamesImage, listingItem);
		}
		btnNewRandom.setText("New random game");
		GameEngine.hideLoading();
	}

	public void enableRandomError(String message) {
		MessageBox.build()
				.setMessage(message)
				.oneButtonsLayout("OK...")
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
			isTutoInvVisible = true;
			GameEngine.start(BlackScreen.build().show(grpTutoInvitation, Timeline.createParallel())
					.push(Tween.to(imgFireArcher, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED).target(0))
					.push(Tween.to(imgBalloon, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(CrystalClash.HEIGHT / 2))
					.push(Tween.to(btnPlayTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT / 2 - btnPlayTutorial.getHeight()))
					.push(Tween.to(btnSkipTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(100))
					.setCallbackTriggers(TweenCallback.COMPLETE)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							lblMessage.setPosition(imgBalloon.getX() + 145, imgBalloon.getTop() - 200);
						}
					}));
		}
	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}
}
