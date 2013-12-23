package com.crystalclash.views;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.SOUND;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;
import com.crystalclash.entities.User;
import com.crystalclash.entities.helpers.UnitAction.UnitActionType;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.helpers.CellHelper;
import com.crystalclash.renders.helpers.EmblemHelper;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.UnitHelper;
import com.crystalclash.renders.helpers.ui.BaseBox.BoxButtons;
import com.crystalclash.renders.helpers.ui.BoxCallback;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.renders.helpers.ui.SuperAnimatedActor;
import com.crystalclash.renders.helpers.ui.UnitStatsPopup;
import com.crystalclash.util.I18n;

public class WorldView extends InputView {
	public static CellHelper cellHelper;

	private TextureRegion txrTerrain;
	private Image imgTerrain;

	private Group grpSend;
	private Image imgBtnSendBackground;
	private Button btnSend;

	private Group grpOptions;
	private Image imgBtnOptionsBackground;
	private Button btnOptions;

	private Group grpPlayer1Details;
	private Image imgDetails1Bar;
	private Image imgPlayer1Emblem;
	private Label lblPlayer1Name;
	
	private Group grpPlayer2Details;
	private Image imgDetails2Bar;
	private Image imgPlayer2Emblem;
	private Label lblPlayer2Name;
	
	private SuperAnimatedActor banner;
	
	private Group grpPopupMenu;
	private Image imgPopupBackground;
	private Image txrBlackScreen;
	private TextButton btnSound;
	private TextButton btnClear;
	private TextButton btnSurrender;
	private TextButton btnBackToGame;
	private TextButton btnBackToMenu;
	private boolean popupMenuVisible;

	private Group grpActionHud;
	private Image actionsHud;
	private Button btnAttack;
	private Button btnMove;
	private Button btnDefense;
	private Button btnUndo;

	private Image arrow;
	private Image pointingHand;
	private float arrowX;
	private float arrowY;
	private float handX;
	private float handY;

	private UnitStatsPopup statsPopup;

	private WorldController world;
	GameView gameRender;

	private boolean readInput = true;

	private BoxCallback backCallback;

	public WorldView(WorldController world) {
		this.world = world;

		cellHelper = new CellHelper();
		cellHelper.load();

		arrowX = 0;
		arrowY = CrystalClash.HEIGHT + 20;
		handX = arrowX;
		handY = arrowY;

		UnitHelper.init();
	}

	public void initFirstTurn() {
		gameRender = new SelectUnitsView(world);
		addActor(gameRender);
		finishLoad(false);
		showGameMenuButtons();
		if (world.player == 1) {
			statsPopup.setX(CrystalClash.WIDTH * 0.25f - statsPopup.getWidth() / 2);
		} else {
			statsPopup.setX(CrystalClash.WIDTH * 0.75f - statsPopup.getWidth() / 2);
		}
	}

	public void initNormalTurn() {
		gameRender = new NormalGameView(world);
		addActor(gameRender);
		showGameMenuButtons();
	}

	public void initTurnAnimations() {
		gameRender = new TurnAnimationsView(world);
		addActor(gameRender);
		finishLoad(true);
	}

	public void initTutorial() {
		gameRender = new TutorialView(world);
		addActor(gameRender);
		finishLoad(false);
		showGameMenuButtons();
	}

	public void render(float dt, SpriteBatch batch) {
		imgTerrain.draw(batch, 1);

		for (int i = 0; i < world.gridW; i++) {
			for (int j = world.gridH - 1; j >= 0; j--) {
				world.cellGrid[i][j].getRender().draw(dt, batch);
			}
		}

		gameRender.renderInTheBack(dt, batch);

		for (int j = world.gridH - 1; j >= 0; j--) {
			for (int i = 0; i < world.gridW; i += 2) {
				world.cellGrid[i][j].getRender().drawUnits(dt, batch);
			}
			for (int i = 1; i < world.gridW; i += 2) {
				world.cellGrid[i][j].getRender().drawUnits(dt, batch);
			}
		}

		gameRender.renderInTheFront(dt, batch);
	}

	public void load() {
		TextureAtlas atlas = ResourceHelper.getTextureAtlas("in_game/options_bar.pack");
		Skin skin = new Skin(atlas);
		
		// Terrain
		txrTerrain = ResourceHelper.getTexture("in_game/terrain");
		imgTerrain = new Image(txrTerrain);
		imgTerrain.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);

		// Grp PopupMenu
		popupMenuVisible = false;
		grpPopupMenu = new Group();
		imgPopupBackground = new Image(ResourceHelper.getTexture("in_game/normal_game_popup"));
		imgPopupBackground.setPosition(0, 0);
		grpPopupMenu.addActor(imgPopupBackground);

		txrBlackScreen = new Image(ResourceHelper.getTexture("menu/loading/background"));
		txrBlackScreen.setPosition(0, -txrBlackScreen.getHeight());
		txrBlackScreen.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
			}
		});

		TextButtonStyle style = ResourceHelper.getButtonStyle();
		
		btnSound = new TextButton(AudioManager.getVolume() == 0 ? I18n.t("world_sound_off") : I18n.t("world_sound_on"), style);
		btnSound.setPosition(imgPopupBackground.getWidth() / 2 - btnSound.getWidth() / 2, imgPopupBackground.getTop() - btnSound.getHeight() - 100);
		btnSound.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AudioManager.toogleVolume();
				btnSound.setText(AudioManager.getVolume() == 0 ? I18n.t("world_sound_off") : I18n.t("world_sound_on"));
			}
		});
		grpPopupMenu.addActor(btnSound);
		
		btnClear = new TextButton(I18n.t("world_clear_moves"), style);
		btnClear.setPosition(imgPopupBackground.getWidth() / 2 - btnClear.getWidth() / 2, btnSound.getY() - 100);
		btnClear.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.clearAllChanges();
				hideOptions();
			}
		});
		grpPopupMenu.addActor(btnClear);
		
		btnSurrender = new TextButton(I18n.t("world_surrender_btn"), style);
		btnSurrender.setPosition(imgPopupBackground.getWidth() / 2 - btnSurrender.getWidth() / 2, btnClear.getY() - 100);
		final BoxCallback leaveCallback = new BoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == BoxCallback.YES) {
					GameEngine.showLoading();
					world.surrenderCurrentGame();
				} else {
					MessageBox.build().hide();
					resume();
				}
			}
		};
		btnSurrender.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				pause();
				setReadInput(false);
				MessageBox.build()
						.setMessage("world_surrender", BoxButtons.Two)
						.setHideOnAction(false)
						.setCallback(leaveCallback)
						.show();
			}
		});
		grpPopupMenu.addActor(btnSurrender);

		btnBackToGame = new TextButton(I18n.t("world_back_to_game"), style);
		btnBackToGame.setPosition(imgPopupBackground.getWidth() / 2 - btnBackToGame.getWidth() / 2, btnSurrender.getY() - 190);
		btnBackToGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				hideOptions();
			}
		});
		grpPopupMenu.addActor(btnBackToGame);
		
		btnBackToMenu = new TextButton(I18n.t("world_back_to_menu_btn"), style);
		btnBackToMenu.setPosition(imgPopupBackground.getWidth() / 2 - btnBackToMenu.getWidth() / 2, btnBackToGame.getY() - 100);
		backCallback = new BoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == BoxCallback.YES) {
					GameEngine.showLoading();
					world.leaveGame();
				} else {
					MessageBox.build().hide();
					resume();
				}
			}
		};
		btnBackToMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setReadInput(false);
				back();
			}
		});
		grpPopupMenu.addActor(btnBackToMenu);
		
		grpPopupMenu.setSize(imgPopupBackground.getWidth(), imgPopupBackground.getHeight());
		grpPopupMenu.setPosition(CrystalClash.WIDTH / 2 - grpPopupMenu.getWidth() / 2, -grpPopupMenu.getHeight());
		
		// Grp Send
		grpSend = new Group();
		imgBtnSendBackground = new Image(skin.getRegion("option_send_bar"));
		imgBtnSendBackground.setPosition(0, 0);
		grpSend.addActor(imgBtnSendBackground);

		ButtonStyle sendStyle = new ButtonStyle(
				skin.getDrawable("option_send_button"),
				skin.getDrawable("option_send_button_pressed"), null);
		btnSend = new Button(sendStyle);
		btnSend.setPosition(0, 0);
		final BoxCallback sendTurnCallback = new BoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == BoxCallback.YES) {
					GameEngine.showLoading();
					world.sendTurn();
				}
				else {
					MessageBox.build().hide();
					setReadInput(true);
				}
			}
		};
		ClickListener sendListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!btnSend.isDisabled()) {
					if (gameRender.canSend()) {
						setReadInput(false);
						MessageBox.build()
								.setMessage("world_send", BoxButtons.Two)
								.setCallback(sendTurnCallback)
								.setHideOnAction(false)
								.show();
					} else {
						gameRender.onSend();
					}
				}
			}
		};
		btnSend.addListener(sendListener);
		grpSend.addActor(btnSend);
		
		grpSend.setSize(imgBtnSendBackground.getWidth(), imgBtnSendBackground.getHeight());
		grpSend.setPosition(-grpSend.getWidth(), -grpSend.getHeight());
		
		// Grp Options
		grpOptions = new Group();
		imgBtnOptionsBackground = new Image(skin.getRegion("option_more_bar"));
		imgBtnOptionsBackground.setPosition(0, 0);
		grpOptions.addActor(imgBtnOptionsBackground);

		ButtonStyle moreStyle = new ButtonStyle(
				skin.getDrawable("option_more_button"),
				skin.getDrawable("option_more_button_pressed"), null);
		btnOptions = new Button(moreStyle);
		btnOptions.setPosition(0, 0);
		btnOptions.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showOptions();
			}
		});
		grpOptions.addActor(btnOptions);
		
		grpOptions.setSize(imgBtnOptionsBackground.getWidth(), imgBtnOptionsBackground.getHeight());
		grpOptions.setPosition(CrystalClash.WIDTH, -grpOptions.getHeight());
	}

	private void finishLoad(boolean enemyDetails) {
		TextureAtlas atlas = ResourceHelper.getTextureAtlas("in_game/options_bar.pack");
		Skin skin = new Skin(atlas);
		skin.add("normal_font", ResourceHelper.getNormalFont());
		LabelStyle style = new LabelStyle();
		style.font = skin.getFont("normal_font");
		skin.add("lblStyle", style);
		
		TextureRegion aux = skin.getRegion("actions_hud");
		actionsHud = new Image(aux);

		ButtonStyle attackStyle = new ButtonStyle(
				skin.getDrawable("action_attack_button"),
				skin.getDrawable("action_attack_button_pressed"), null);
		btnAttack = new Button(attackStyle);
		btnAttack.setPosition(actionsHud.getX(), actionsHud.getY() + 155);
		btnAttack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.onAttackAction();
			}
		});

		ButtonStyle defenseStyle = new ButtonStyle(
				skin.getDrawable("action_defensive_button"),
				skin.getDrawable("action_defensive_button_pressed"), null);
		btnDefense = new Button(defenseStyle);
		btnDefense.setPosition(actionsHud.getX() + 5, actionsHud.getY() + 13);
		btnDefense.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.onDefendAction();
			}
		});

		ButtonStyle moveStyle = new ButtonStyle(
				skin.getDrawable("action_run_button"),
				skin.getDrawable("action_run_button_pressed"), null);
		btnMove = new Button(moveStyle);
		btnMove.setPosition(actionsHud.getX() + 233, actionsHud.getY() + 155);
		btnMove.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.onMoveAction();
			}
		});

		ButtonStyle undoStyle = new ButtonStyle(
				skin.getDrawable("action_cancel_button"),
				skin.getDrawable("action_cancel_button_pressed"), null);
		btnUndo = new Button(undoStyle);
		btnUndo.setPosition(actionsHud.getX() + 231, actionsHud.getY() + 9);
		btnUndo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.onUndoAction();
			}
		});

		arrow = new Image(ResourceHelper.getTexture("in_game/selector_arrow"));
		arrow.setPosition(arrowX, arrowY);
		pointingHand = new Image(ResourceHelper.getTexture("tutorial/pointing_hand"));
		pointingHand.setPosition(handX, handY);

		grpActionHud = new Group();
		grpActionHud.addActor(actionsHud);
		grpActionHud.addActor(btnAttack);
		grpActionHud.addActor(btnMove);
		grpActionHud.addActor(btnDefense);
		grpActionHud.addActor(btnUndo);
		grpActionHud.setSize(actionsHud.getWidth(), actionsHud.getHeight());
		grpActionHud.setOrigin(actionsHud.getWidth() / 2, actionsHud.getHeight() / 2);
		grpActionHud.setPosition(CrystalClash.WIDTH / 2 - grpActionHud.getWidth() / 2, CrystalClash.HEIGHT + 50);

		statsPopup = new UnitStatsPopup();
		addActor(statsPopup);

		// Grp Details
		grpPlayer1Details = new Group();
		grpPlayer2Details = new Group();
		
		User me = GameController.getUser();
		User enemy = null;
		
		boolean addBannerPlayer1 = true;
		if (world.gameTurn % 2 == 0)
			addBannerPlayer1 = false;
		
		banner = new SuperAnimatedActor(ResourceHelper.getSuperAnimation("in_game/moves_first"), true, FACING.right);
		
		if (world.player == 1) {
			loadLeftDetailsGrp(skin, me, addBannerPlayer1);
			
			if(enemyDetails){
				enemy = GameController.getEnemyUser();
				loadRightDetailsGrp(skin, enemy, !addBannerPlayer1);
				GameController.setEnemyUser(null);
			}
		} else {
			loadRightDetailsGrp(skin, me, !addBannerPlayer1);
			
			if(enemyDetails){
				enemy = GameController.getEnemyUser();
				loadLeftDetailsGrp(skin, enemy, addBannerPlayer1);
				GameController.setEnemyUser(null);
			}
		}
				
		addActor(grpActionHud);
		addActor(arrow);
		addActor(pointingHand);

		addActor(grpOptions);
		addActor(grpSend);
		addActor(grpPlayer1Details);
		addActor(grpPlayer2Details);
		addActor(txrBlackScreen);
		addActor(grpPopupMenu);
	}

	private void loadLeftDetailsGrp(Skin skin, User u, boolean addBanner) {
		imgDetails1Bar = new Image(skin.getRegion("player_details_bar_left"));
		imgDetails1Bar.setPosition(0, 0);
		grpPlayer1Details.addActor(imgDetails1Bar);

		lblPlayer1Name = new Label(u.getName(), skin, "lblStyle");
		lblPlayer1Name.setPosition(155, 140);
		grpPlayer1Details.addActor(lblPlayer1Name);

		imgPlayer1Emblem = new Image(EmblemHelper.getEmblem(u.getEmblem()));
		imgPlayer1Emblem.setSize(115, 115);
		imgPlayer1Emblem.setPosition(5, 56);
		grpPlayer1Details.addActor(imgPlayer1Emblem);
		
		if(addBanner){
			banner.setPosition(45, -75);
			grpPlayer1Details.addActor(banner);
		}
		
		grpPlayer1Details.setSize(imgDetails1Bar.getWidth(), imgDetails1Bar.getHeight());
		grpPlayer1Details.setPosition(-grpPlayer1Details.getWidth(), CrystalClash.HEIGHT);
	}

	private void loadRightDetailsGrp(Skin skin, User u, boolean addBanner) {
		imgDetails2Bar = new Image(skin.getRegion("player_details_bar_right"));
		imgDetails2Bar.setPosition(0, 0);
		grpPlayer2Details.addActor(imgDetails2Bar);

		lblPlayer2Name = new Label(u.getName(), skin, "lblStyle");
		lblPlayer2Name.setPosition(80, 140);
		grpPlayer2Details.addActor(lblPlayer2Name);

		imgPlayer2Emblem = new Image(EmblemHelper.getEmblem(u.getEmblem()));
		imgPlayer2Emblem.setSize(115, 115);
		imgPlayer2Emblem.setPosition(397, 56);
		grpPlayer2Details.addActor(imgPlayer2Emblem);
		
		if(addBanner){
			banner.setPosition(415, -75);
			grpPlayer2Details.addActor(banner);
		}
		
		grpPlayer2Details.setSize(imgDetails2Bar.getWidth(), imgDetails2Bar.getHeight());
		grpPlayer2Details.setPosition(CrystalClash.WIDTH, CrystalClash.HEIGHT);
	}
	
	private void back() {
		pause();
		MessageBox.build()
				.setMessage("world_back_to_menu", BoxButtons.Two)
				.setCallback(backCallback)
				.setHideOnAction(false)
				.show();
	}

	private void showOptions() {
		popupMenuVisible = true;
		setReadInput(false);
		GameEngine.start(pushHideGameMenuButtons(Timeline.createParallel()
				.push(Tween.set(txrBlackScreen, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(txrBlackScreen, ActorAccessor.Y).target(0))
				.push(Tween.to(txrBlackScreen, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED).target(1))
				.push(Tween.to(grpPopupMenu, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT / 2 - grpPopupMenu.getHeight() / 2).ease(TweenEquations.easeNone))));
	}

	private void hideOptions() {
		popupMenuVisible = false;
		setReadInput(true);
		GameEngine.start(pushShowGameMenuButtons(Timeline.createParallel()
				.push(Tween.to(txrBlackScreen, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED).target(0))
				.push(Tween.to(grpPopupMenu, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(-grpPopupMenu.getHeight()).ease(TweenEquations.easeNone))
				.push(Tween.set(txrBlackScreen, ActorAccessor.Y).target(-txrBlackScreen.getHeight()))));
	}

	public void showGameMenuButtons() {
		GameEngine.start(pushShowGameMenuButtons(Timeline.createSequence()));
	}

	public Timeline pushShowGameMenuButtons(Timeline t) {
		return t.beginParallel()
				.push(Tween.to(grpSend, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpSend, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpOptions, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.WIDTH - grpOptions.getWidth()).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpOptions, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpPlayer1Details, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpPlayer1Details, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT - grpPlayer1Details.getHeight()).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpPlayer2Details, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.WIDTH - grpPlayer2Details.getWidth()).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpPlayer2Details, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT - grpPlayer2Details.getHeight()).ease(TweenEquations.easeOutCirc))
				.end();
	}

	public Timeline pushHideGameMenuButtons(Timeline t) {
		return t.beginParallel()
				.push(Tween.to(grpSend, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(-grpSend.getWidth()))
				.push(Tween.to(grpSend, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(-grpSend.getHeight()))
				.push(Tween.to(grpOptions, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.WIDTH))
				.push(Tween.to(grpOptions, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(-grpSend.getHeight()))
				.push(Tween.to(grpPlayer1Details, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(-grpPlayer1Details.getWidth()))
				.push(Tween.to(grpPlayer1Details, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT))
				.push(Tween.to(grpPlayer2Details, ActorAccessor.X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.WIDTH))
				.push(Tween.to(grpPlayer2Details, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT))
				.end();
	}

	public void selectUnitInCell(Cell cell) {
		Unit u = cell.getUnit();
		if (!u.isEnemy()) {
			moveActionsRing(cell);
			u.getRender().playSFX(SOUND.select);
		}
		cell.addState(Cell.SELECTED);
		showStatsPopup(u);
		GameEngine.start(pushHideGameMenuButtons(Timeline.createParallel()));
	}

	public void deselectUnitInCell(Cell cell) {
		if (cell != null)
			cell.removeState(Cell.SELECTED);
		hideActionsRing();
		hideStatsPopup();
		GameEngine.start(pushShowGameMenuButtons(Timeline.createParallel()));
	}

	public void showStatsPopup(Unit u) {
		statsPopup.show(u);
	}

	public void showStatsPopupFirstTurn(String unitName) {
		statsPopup.show(unitName, UnitStatsPopup.FIXED_TOP);
	}

	public void hideStatsPopup() {
		statsPopup.hide();
	}

	public void moveActionsRing(final Cell selectedCell) {
		if (selectedCell.getUnit() != null) {
			Timeline t = Timeline.createSequence();
			pushFadeOutActionsRing(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (selectedCell.getAction() == null ||
							selectedCell.getAction().getActionType().equals(UnitActionType.PLACE) ||
							selectedCell.getAction().getActionType().equals(UnitActionType.NONE)) {
						btnAttack.setVisible(true);
						btnDefense.setVisible(true);
						btnMove.setVisible(true);
						btnUndo.setVisible(false);
					} else {
						btnAttack.setVisible(false);
						btnDefense.setVisible(false);
						btnMove.setVisible(false);
						btnUndo.setVisible(true);
					}
					grpActionHud.setPosition(CellHelper.getCenterX(selectedCell) - actionsHud.getWidth() / 2, selectedCell.getY() - 80);
					fadeInActionsRing();
				}
			});
			GameEngine.start(t);
		}
	}

	public void hideActionsRing() {
		GameEngine.start(pushFadeOutActionsRing(Timeline.createSequence()));
	}

	private Timeline pushFadeOutActionsRing(Timeline t) {
		return t.beginParallel()
				.push(Tween.to(grpActionHud, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0))
				.push(Tween.to(grpActionHud, ActorAccessor.SCALE_X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0.8f))
				.push(Tween.to(grpActionHud, ActorAccessor.SCALE_Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0.8f))
				.push(Tween.call(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						grpActionHud.setPosition(CrystalClash.WIDTH + actionsHud.getWidth(), 0);
					}
				}))
				.end();
	}

	private void fadeInActionsRing() {
		grpActionHud.setScale(0.8f, 0.8f);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(grpActionHud, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
						.target(1))
				.push(Tween.to(grpActionHud, ActorAccessor.SCALE_X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(1))
				.push(Tween.to(grpActionHud, ActorAccessor.SCALE_Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(1)));
	}

	public void undoAction() {
		btnAttack.setVisible(true);
		btnDefense.setVisible(true);
		btnMove.setVisible(true);
		btnUndo.setVisible(false);
	}

	public void moveArrow(Unit u) {
		if (u != null) {
			if (arrow.getY() >= CrystalClash.HEIGHT) {
				arrow.setPosition(u.getX(), CrystalClash.HEIGHT + 20);
			}
			arrowX = u.getX();
			arrowY = u.getY() + 120;
		} else {
			arrowY = CrystalClash.HEIGHT + 20;
		}

		GameEngine.kill(arrow);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(arrow, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(arrowX))
				.push(Tween.to(arrow, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(arrowY))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						arrow.setPosition(arrowX, arrowY);
						selectorAnimation(arrow);
					}
				}));
	}

	public void moveArrow(float x, float y) {
		arrowX = x;
		arrowY = y;

		GameEngine.kill(arrow);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(arrow, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED).target(arrowX))
				.push(Tween.to(arrow, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED).target(arrowY))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						arrow.setPosition(arrowX, arrowY);
						selectorAnimation(arrow);
					}
				}));
	}

	public void hideArrow() {
		arrowX = arrow.getX();
		arrowY = CrystalClash.HEIGHT + 20;

		GameEngine.kill(arrow);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(arrow, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED).target(arrowY))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						arrow.setPosition(arrowX, arrowY);
					}
				}));
	}

	public void moveHand(Unit u) {
		if (u != null) {
			if (pointingHand.getY() >= CrystalClash.HEIGHT) {
				pointingHand.setPosition(u.getX(), CrystalClash.HEIGHT + 20);
			}
			handX = u.getX() - 30;
			handY = u.getY() + 120;
		} else {
			handY = CrystalClash.HEIGHT + 20;
		}

		GameEngine.kill(pointingHand);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(pointingHand, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED).target(handX))
				.push(Tween.to(pointingHand, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED).target(handY))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						pointingHand.setPosition(handX, handY);
						selectorAnimation(pointingHand);
					}
				}));
	}

	public void moveHand(float x, float y) {
		handX = x;
		handY = y;

		GameEngine.kill(pointingHand);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(pointingHand, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED).target(handX))
				.push(Tween.to(pointingHand, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED).target(handY))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						pointingHand.setPosition(handX, handY);
						selectorAnimation(pointingHand);
					}
				}));
	}

	public void hideHand() {
		handX = pointingHand.getX();
		handY = CrystalClash.HEIGHT + 20;

		GameEngine.kill(pointingHand);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(pointingHand, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED).target(handY))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						pointingHand.setPosition(handX, handY);
					}
				}));
	}

	private void selectorAnimation(Image selector) {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.set(selector, ActorAccessor.Y).target(selector.getY()))
				.push(Tween.to(selector, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(selector.getY() - 10))
				.push(Tween.to(selector, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(selector.getY())).repeat(Tween.INFINITY, 0));
	}

	public void dispose() {
		// txrTerrain.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK)
			back();
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (readInput) {
			Vector2 vec = GameEngine.getRealPosition(screenX, screenY);
			gameRender.touchDown(vec.x, vec.y, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (readInput) {
			Vector2 vec = GameEngine.getRealPosition(screenX, screenY);
			gameRender.touchUp(vec.x, vec.y, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (readInput) {
			Vector2 vec = GameEngine.getRealPosition(screenX, screenY);
			gameRender.touchDragged(vec.x, vec.y, pointer);
		}
		return false;
	}

	public Timeline pushEnterAnimation(Timeline t) {
		return gameRender.pushEnterAnimation(t);
	}

	public Timeline pushExitAnimation(Timeline t) {
		hideOptions();
		t.beginSequence();
		pushHideGameMenuButtons(t)
				.push(Tween.to(grpActionHud, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + grpActionHud.getHeight()))
				.push(Tween.to(arrow, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + arrow.getHeight()))
				.push(Tween.to(pointingHand, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + pointingHand.getHeight()))
				.end();

		gameRender.pushExitAnimation(t);
		return t;
	}

	public void setReadInput(boolean read) {
		readInput = read;
	}

	public void setBlockButtons(boolean block) {
		btnSend.setDisabled(block);
		btnOptions.setDisabled(true);
	}

	public void pause() {
		setReadInput(false);
		gameRender.pause();
	}

	public void resume() {
		if(!popupMenuVisible)
			setReadInput(true);
		gameRender.resume();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}
}
