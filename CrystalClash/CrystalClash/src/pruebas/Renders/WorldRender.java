package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.UnitAction.UnitActionType;
import pruebas.Renders.helpers.CellHelper;
import pruebas.Renders.helpers.ResourceHelper;
import pruebas.Renders.helpers.UnitHelper;
import pruebas.Renders.helpers.ui.MessageBox;
import pruebas.Renders.helpers.ui.MessageBoxCallback;
import pruebas.Renders.helpers.ui.UnitStatsPopup;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WorldRender extends Group implements InputProcessor {
	public static CellHelper cellHelper;

	private TextureRegion txrTerrain;
	private Image imgTerrain;

	private Group grpBtnSend;
	private Image imgBtnSendBackground;
	private TextButton btnSend;

	private Group grpBtnOptions;
	private Image imgBtnOptionsBackground;
	private TextButton btnOptions;

	private Group grpOptions;
	private Image imgOptionsBackground;
	private TextButton btnSurrender;
	private TextButton btnBack;
	private TextButton btnClear;
	private boolean hideMoreOptions;

	private Image actionsBar;
	private TextButton btnAttack;
	private TextButton btnMove;
	private TextButton btnDefense;
	private TextButton btnUndo;
	private Group grpActionBar;

	private Image arrow;
	private Image pointingHand;
	private float arrowX;
	private float arrowY;
	private float handX;
	private float handY;

	private UnitStatsPopup statsPopup;

	private WorldController world;
	GameRender gameRender;

	private boolean readInput = true;

	private MessageBoxCallback backCallback;

	public WorldRender(WorldController world) {
		this.world = world;

		cellHelper = new CellHelper();
		cellHelper.load();

		hideMoreOptions = false;
		arrowX = 0;
		arrowY = CrystalClash.HEIGHT + 20;
		handX = arrowX;
		handY = arrowY;

		UnitHelper.init();
	}

	public void initFirstTurn() {
		gameRender = new SelectUnitsRender(world);
		addActor(gameRender);
		finishLoad();
		showHuds();
	}

	public void initNormalTurn() {
		gameRender = new NormalGame(world);
		addActor(gameRender);
		finishLoad();
		showHuds();
	}

	public void initTurnAnimations() {
		gameRender = new TurnAnimations(world);
		addActor(gameRender);
		finishLoad();
	}

	public void initTutorial() {
		gameRender = new Tutorial(world);
		addActor(gameRender);
		finishLoad();
		showHuds();
	}

	public void render(float dt, SpriteBatch batch) {
		imgTerrain.draw(batch, 1);

		for (int i = 0; i < world.cellGrid.length; i++) {
			for (int j = world.cellGrid[i].length - 1; j >= 0; j--) {
				world.cellGrid[i][j].getRender().draw(dt, batch);
			}
		}

		gameRender.renderInTheBack(dt, batch);

		for (int j = 5; j >= 0; j--) {
			for (int i = 0; i < 9; i += 2) {
				world.cellGrid[i][j].getRender().drawUnits(dt, batch);
			}
			for (int i = 1; i < 9; i += 2) {
				world.cellGrid[i][j].getRender().drawUnits(dt, batch);
			}
		}

		gameRender.renderInTheFront(dt, batch);
	}

	private void load() {
		TextureAtlas atlas = ResourceHelper.getTextureAtlas("in_game/options_bar.pack");
		Skin skin = new Skin(atlas);

		// Terrain
		txrTerrain = ResourceHelper.getTexture("in_game/terrain");
		imgTerrain = new Image(txrTerrain);
		imgTerrain.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);

		// Options bar
		TextButtonStyle optionsStyle = new TextButtonStyle(
				skin.getDrawable("option_button"),
				skin.getDrawable("option_button_pressed"), null, ResourceHelper.getFont());

		grpOptions = new Group();
		imgOptionsBackground = new Image(skin.getRegion("options_bar"));
		imgOptionsBackground.setPosition(0, 0);
		grpOptions.addActor(imgOptionsBackground);

		btnSurrender = new TextButton("Surrender", optionsStyle);
		btnSurrender.setPosition(75, 5);
		final MessageBoxCallback leaveCallback = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {
					GameEngine.showLoading();
					world.surrenderCurrentGame();
				} else {
					MessageBox.build().hide();
					setReadInput(true);
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
						.setMessage("\"He who knows when he can fight and when he cannot, will be victorious.\"\n- Sun Tzu")
						.twoButtonsLayout("Surrender", "Not yet!")
						.setHideOnAction(false)
						.setCallback(leaveCallback)
						.show();
			}
		});
		grpOptions.addActor(btnSurrender);

		backCallback = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {
					GameEngine.showLoading();
					world.leaveGame();
				} else {
					MessageBox.build().hide();
					setReadInput(true);
					resume();
				}
			}
		};
		btnBack = new TextButton("Back to Menu", optionsStyle);
		btnBack.setPosition(btnSurrender.getX() + btnSurrender.getWidth() + 2, 5);
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setReadInput(false);
				back();
			}
		});
		grpOptions.addActor(btnBack);

		btnClear = new TextButton("Clear Moves", optionsStyle);
		btnClear.setPosition(btnBack.getX() + btnBack.getWidth() + 2, 5);
		btnClear.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.clearAllChanges();
			}
		});
		grpOptions.addActor(btnClear);
		grpOptions.setSize(imgOptionsBackground.getWidth(), imgOptionsBackground.getHeight());
		grpOptions.setPosition(-grpOptions.getWidth(), 0);

		// Btn Options
		grpBtnOptions = new Group();
		imgBtnOptionsBackground = new Image(skin.getRegion("option_more_bar"));
		imgBtnOptionsBackground.setPosition(0, 0);
		grpBtnOptions.addActor(imgBtnOptionsBackground);

		TextButtonStyle moreStyle = new TextButtonStyle(
				skin.getDrawable("option_more_button"),
				skin.getDrawable("option_more_button_pressed"), null, ResourceHelper.getFont());
		btnOptions = new TextButton("", moreStyle);
		btnOptions.setPosition(imgBtnOptionsBackground.getWidth() - btnOptions.getWidth(), 0);
		btnOptions.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!btnOptions.isDisabled()) {
					hideMoreOptions = true;
					showOptions();
				}
			}
		});
		grpBtnOptions.addActor(btnOptions);
		grpBtnOptions.setSize(imgBtnOptionsBackground.getWidth(), imgBtnOptionsBackground.getHeight());
		grpBtnOptions.setPosition(-grpBtnOptions.getWidth(), 0);

		// Btn Send
		grpBtnSend = new Group();
		imgBtnSendBackground = new Image(skin.getRegion("option_send_bar"));
		imgBtnSendBackground.setPosition(0, 0);
		grpBtnSend.addActor(imgBtnSendBackground);

		TextButtonStyle sendStyle = new TextButtonStyle(
				skin.getDrawable("option_send_button"),
				skin.getDrawable("option_send_button_pressed"), null, ResourceHelper.getFont());
		btnSend = new TextButton("", sendStyle);
		btnSend.setPosition(0, 0);
		final MessageBoxCallback sendTurnCallback = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {
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
								.setMessage("Comander!\nTroops are ready and waiting for battle!\nJust say the word")
								.twoButtonsLayout("Charge!!", "Hold your horses!")
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
		grpBtnSend.addActor(btnSend);
		grpBtnSend.setSize(imgBtnSendBackground.getWidth(), imgBtnSendBackground.getHeight());
		grpBtnSend.setPosition(-grpBtnSend.getWidth(), 0);

		addActor(grpBtnOptions);
		addActor(grpOptions);
		addActor(grpBtnSend);
	}

	private void finishLoad() {
		TextureAtlas atlas = ResourceHelper.getTextureAtlas("in_game/options_bar.pack");
		Skin skin = new Skin(atlas);

		TextureRegion aux = skin.getRegion("actions_hud");
		actionsBar = new Image(aux);

		TextButtonStyle attackStyle = new TextButtonStyle(
				skin.getDrawable("action_attack_button"),
				skin.getDrawable("action_attack_button_pressed"), null, ResourceHelper.getFont());
		btnAttack = new TextButton("", attackStyle);
		btnAttack.setPosition(actionsBar.getX(), actionsBar.getY() + 155);
		btnAttack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.onAttackAction();
			}
		});

		TextButtonStyle defenseStyle = new TextButtonStyle(
				skin.getDrawable("action_defensive_button"),
				skin.getDrawable("action_defensive_button_pressed"), null, ResourceHelper.getFont());
		btnDefense = new TextButton("", defenseStyle);
		btnDefense.setPosition(actionsBar.getX() + 5, actionsBar.getY() + 13);
		btnDefense.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.onDefendAction();
			}
		});

		TextButtonStyle moveStyle = new TextButtonStyle(
				skin.getDrawable("action_run_button"),
				skin.getDrawable("action_run_button_pressed"), null, ResourceHelper.getFont());
		btnMove = new TextButton("", moveStyle);
		btnMove.setPosition(actionsBar.getX() + 233, actionsBar.getY() + 155);
		btnMove.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameRender.onMoveAction();
			}
		});

		TextButtonStyle undoStyle = new TextButtonStyle(
				skin.getDrawable("action_cancel_button"),
				skin.getDrawable("action_cancel_button_pressed"), null, ResourceHelper.getFont());
		btnUndo = new TextButton("", undoStyle);
		btnUndo.setPosition(actionsBar.getX() + 231, actionsBar.getY() + 9);
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

		grpActionBar = new Group();
		grpActionBar.addActor(actionsBar);
		grpActionBar.addActor(btnAttack);
		grpActionBar.addActor(btnMove);
		grpActionBar.addActor(btnDefense);
		grpActionBar.addActor(btnUndo);
		grpActionBar.setSize(actionsBar.getWidth(), actionsBar.getHeight());
		grpActionBar.setOrigin(actionsBar.getWidth() / 2, actionsBar.getHeight() / 2);
		grpActionBar.setPosition(CrystalClash.WIDTH / 2 - grpActionBar.getWidth() / 2, CrystalClash.HEIGHT + 50);

		addActor(grpActionBar);
		addActor(arrow);
		addActor(pointingHand);

		statsPopup = new UnitStatsPopup();
		addActor(statsPopup);
	}

	private void back() {
		pause();
		MessageBox.build()
				.setMessage("If we leave now Commander,\ntroops will lose the given formation!")
				.twoButtonsLayout("Do it anyway", "Let me think...")
				.setCallback(backCallback)
				.setHideOnAction(false)
				.show();
	}

	private void showOptions() {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(grpBtnOptions, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(-grpBtnOptions.getWidth()))
				.push(Tween.to(grpOptions, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(75).ease(TweenEquations.easeOutCirc)));
	}

	private void hideOptions() {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(grpOptions, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(-grpOptions.getWidth()))
				.push(Tween.to(grpBtnOptions, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(grpBtnSend.getWidth() - 35).ease(TweenEquations.easeOutCirc)));
	}

	public void showHuds() {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(grpBtnSend, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(0).ease(TweenEquations.easeOutCirc))
				.push(Tween.to(grpBtnOptions, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(grpBtnSend.getWidth() - 35).ease(TweenEquations.easeOutCirc)));
	}

	public void selectUnit(Unit unit) {
		statsPopup.show(unit);
	}

	public void selectUnitInCell(Unit unit, Cell cell) {
		if (!unit.isEnemy()) {
			moveActionsRing(cell);
		}
		cell.addState(Cell.SELECTED);
		showStatsPopup(unit);
	}

	public void deselectUnitInCell(Cell cell) {
		statsPopup.hide();
		if (cell != null)
			cell.removeState(Cell.SELECTED);
		hideActionsRing();
	}
	
	public void showStatsPopup(Unit u) {
		statsPopup.show(u);
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
					UnitActionType actionType = selectedCell.getAction().getActionType();
					if (actionType.equals(UnitActionType.PLACE) || actionType.equals(UnitActionType.NONE)) {
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
					grpActionBar.setPosition(CellHelper.getCenterX(selectedCell) - actionsBar.getWidth() / 2, selectedCell.getY() - 80);
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
				.push(Tween.to(grpActionBar, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0))
				.push(Tween.to(grpActionBar, ActorAccessor.SCALE_X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0.8f))
				.push(Tween.to(grpActionBar, ActorAccessor.SCALE_Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0.8f))
				.push(Tween.call(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						grpActionBar.setPosition(CrystalClash.WIDTH + actionsBar.getWidth(), 0);
					}
				}))
				.end();
	}

	private void fadeInActionsRing() {
		grpActionBar.setScale(0.8f, 0.8f);
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(grpActionBar, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
						.target(1))
				.push(Tween.to(grpActionBar, ActorAccessor.SCALE_X, CrystalClash.FAST_ANIMATION_SPEED)
						.target(1))
				.push(Tween.to(grpActionBar, ActorAccessor.SCALE_Y, CrystalClash.FAST_ANIMATION_SPEED)
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

	public void init() {
		load();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK)
			back();
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (readInput) {
			Vector2 vec = GameEngine.getRealPosition(screenX, screenY);

			if (hideMoreOptions
					&& (vec.x > imgOptionsBackground.getX() + imgOptionsBackground.getWidth() || vec.y > btnSurrender
							.getTop() + 25)) {
				hideOptions();
			}
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

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	public Timeline pushEnterAnimation(Timeline t) {
		return gameRender.pushEnterAnimation(t);
	}

	public Timeline pushExitAnimation(Timeline t) {
		t.beginSequence();
		t.push(Tween.to(grpOptions, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
				.target(-grpOptions.getWidth()))
				.push(Tween.to(grpBtnOptions, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(-grpBtnOptions.getWidth()))
				.push(Tween.to(grpBtnSend, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(-grpBtnSend.getWidth()))
				.push(Tween.to(grpActionBar, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + grpActionBar.getHeight()))
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
		gameRender.pause();
	}

	public void resume() {
		gameRender.resume();
	}
}
