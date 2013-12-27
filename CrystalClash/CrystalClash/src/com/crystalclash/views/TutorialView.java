package com.crystalclash.views;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.accessors.UnitAccessor;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.GAME_END_SFX;
import com.crystalclash.audio.AudioManager.SOUND;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;
import com.crystalclash.entities.helpers.AttackUnitAction;
import com.crystalclash.entities.helpers.MoveUnitAction;
import com.crystalclash.entities.helpers.NoneUnitAction;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.PathRender;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.UnitRender.STATE;
import com.crystalclash.renders.attacks.AttackFactory;
import com.crystalclash.renders.helpers.CellHelper;
import com.crystalclash.renders.helpers.PathManager;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.BaseBox.BoxButtons;
import com.crystalclash.renders.helpers.ui.BoxCallback;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.util.I18n;

public class TutorialView extends GameView {

	private static TweenManager tweenManager;
	private AttackFactory attacks;
	private Group entities;

	private Image fireArcher;
	private Image balloon;
	private Label lblMessage;
	private TextButton btnNext;
	private Button btnSkip;
	private Image imgBtnSkipBackground;

	private Array<String> messages;
	private int messageIndex;

	private Unit slayer;
	private Unit tank;
	private Unit archer;
	private MoveUnitAction slayerMove;

	private Unit selectedUnit;
	private boolean archerAttacked;
	private boolean slayerAttacked;
	private boolean actionInProgress;

	private boolean actionRingVisible;
	private boolean blockButtons;
	private int movePathIndex;

	private PathManager paths;

	private Image imgAttackIcon;
	private Image imgMoveIcon;
	private Image imgDefendIcon;
	private Image imgUndoIcon;
	private Image imgLifeIcon;
	private Image imgDamageIcon;
	private Image imgMobilityIcon;

	public TutorialView(WorldController world) {
		super(world);
		messageIndex = 0;
		movePathIndex = 0;
		attacks = new AttackFactory(world);

		load();
		readTutorialScript();
		GameController.loadSharedStats();
		setData();

		world.getRender().setReadInput(false);
		world.getRender().setBlockButtons(true);

		btnNext.setDisabled(true);
		btnSkip.setDisabled(true);
		actionRingVisible = false;
		blockButtons = true;

		selectedUnit = null;
		archerAttacked = false;
		slayerAttacked = false;
		actionInProgress = false;

		paths = new PathManager();
		GameEngine.hideLoading();
	}

	public void load() {
		AudioManager.loadTutorialSFX();
		TextureAtlas atlas = ResourceHelper.getTextureAtlas("in_game/options_bar.pack");
		Skin skin = new Skin(atlas);

		tweenManager = new TweenManager();

		fireArcher = new Image(ResourceHelper.getTexture("tutorial/fire_archer"));
		fireArcher.scale(-0.55f);
		fireArcher.setPosition(-fireArcher.getWidth(), -10);

		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(fireArcher, ActorAccessor.SCALE_Y, CrystalClash.REALLY_SLOW_ANIMATION_SPEED)
						.target(fireArcher.getScaleY() + 0.005f))
				.push(Tween.to(fireArcher, ActorAccessor.ROTATION, CrystalClash.REALLY_SLOW_ANIMATION_SPEED)
						.target(0.5f))
				.repeatYoyo(-1, 0));

		balloon = new Image(ResourceHelper.getTexture("tutorial/message_balloon"));
		balloon.scale(-0.1f);
		balloon.setPosition(160 + fireArcher.getWidth() * 0.45f, -balloon.getHeight());

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getNormalFont(), Color.BLACK));
		lblMessage.setAlignment(Align.center | Align.top);
		lblMessage.setSize(600, 338);

		btnNext = new TextButton("Next", ResourceHelper.getNextButtonStyle());
		btnNext.setPosition(CrystalClash.WIDTH - btnNext.getWidth() - 120, -balloon.getHeight() - btnNext.getHeight());
		btnNext.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!blockButtons)
					next();
			}
		});
		final BoxCallback confirmation = new BoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == BoxCallback.YES) {
					GameEngine.showLoading();
					GameEngine.getInstance().openMenuGames();
					GameController.setTutorialDone();
				} else {
					MessageBox.build().hide();
				}
			}
		};

		imgBtnSkipBackground = new Image(skin.getRegion("exit_hud"));
		imgBtnSkipBackground.setPosition(CrystalClash.WIDTH, CrystalClash.HEIGHT - imgBtnSkipBackground.getHeight());
		ButtonStyle skipStyle = new ButtonStyle(
				skin.getDrawable("exit_button"),
				skin.getDrawable("exit_button_pressed"), null);
		btnSkip = new Button(skipStyle);
		btnSkip.setPosition(CrystalClash.WIDTH, CrystalClash.HEIGHT - btnSkip.getHeight());
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageBox msg = MessageBox.build();
				if (messageIndex < messages.size / 2)
					msg.setMessage("tutorial_leave_start", BoxButtons.Two);
				if (messageIndex < messages.size - 8)
					msg.setMessage("tutorial_leave_middle", BoxButtons.Two);
				else
					msg.setMessage("tutorial_leave_end", BoxButtons.Two);

				msg.setCallback(confirmation)
						.setHideOnAction(false)
						.show();
			}
		});

		addActor(fireArcher);
		addActor(balloon);
		addActor(lblMessage);
		addActor(btnNext);
		addActor(imgBtnSkipBackground);
		addActor(btnSkip);

		PathManager.load();

		// Load icons hud
		imgAttackIcon = new Image(skin.getRegion("action_attack_button"));
		imgMoveIcon = new Image(skin.getRegion("action_run_button"));
		imgDefendIcon = new Image(skin.getRegion("action_defensive_button"));
		imgUndoIcon = new Image(skin.getRegion("action_cancel_button"));

		// Position and scaling icons hud
		imgAttackIcon.scale(-0.5f);
		imgMoveIcon.scale(-0.5f);
		imgDefendIcon.scale(-0.5f);
		imgUndoIcon.scale(-0.5f);
		imgAttackIcon.setPosition(755, 157);
		imgMoveIcon.setPosition(905, 157);
		imgDefendIcon.setPosition(755, 70);
		imgUndoIcon.setPosition(905, 70);

		// Load icons attack, life & speed
		atlas = ResourceHelper.getTextureAtlas("in_game/unit_stats_popup/unit_stats_popup.pack");
		imgLifeIcon = new Image(atlas.findRegion("icon_life"));
		imgDamageIcon = new Image(atlas.findRegion("icon_attack"));
		imgMobilityIcon = new Image(atlas.findRegion("icon_speed"));

		imgDamageIcon.setPosition(795, 205);
		imgLifeIcon.setPosition(920, 205);
		imgMobilityIcon.setPosition(855, 115);

		entities = new Group();
		addActor(entities);
	}

	private void readTutorialScript() {
		messages = new Array<String>();
		// Scene 1
		messages.add(I18n.t("tutorial_line_0"));
		messages.add(I18n.t("tutorial_line_1"));
		messages.add(I18n.t("tutorial_line_2"));
		messages.add(I18n.t("tutorial_line_3"));
		messages.add(I18n.t("tutorial_line_4"));
		messages.add(I18n.t("tutorial_line_5"));
		messages.add(I18n.t("tutorial_line_6"));
		messages.add(I18n.t("tutorial_line_7"));
		messages.add(I18n.t("tutorial_line_8"));
		messages.add(I18n.t("tutorial_line_9"));
		messages.add(I18n.t("tutorial_line_10"));
		messages.add(I18n.t("tutorial_line_11"));
		messages.add(I18n.t("tutorial_line_12"));
		messages.add(I18n.t("tutorial_line_13"));
		messages.add(I18n.t("tutorial_line_14"));
		// Scene 2
		messages.add(I18n.t("tutorial_line_15"));
		messages.add(I18n.t("tutorial_line_16"));
		messages.add(I18n.t("tutorial_line_17"));
		messages.add(I18n.t("tutorial_line_18"));
		messages.add(I18n.t("tutorial_line_19"));
		messages.add(I18n.t("tutorial_line_20"));
		messages.add(I18n.t("tutorial_line_21"));
		messages.add(I18n.t("tutorial_line_22"));
		messages.add(I18n.t("tutorial_line_23"));
		messages.add(I18n.t("tutorial_line_24"));
		// Scene 3
		messages.add(I18n.t("tutorial_line_25"));
		messages.add(I18n.t("tutorial_line_26"));
		// Scene 4
		messages.add(I18n.t("tutorial_line_27"));
		messages.add(I18n.t("tutorial_line_28"));
		messages.add(I18n.t("tutorial_line_29"));
		messages.add(I18n.t("tutorial_line_30"));
		messages.add(I18n.t("tutorial_line_31"));
		messages.add(I18n.t("tutorial_line_32"));
	}

	private void setData() {
		slayer = new Unit("wind_slayer", false);
		world.addUnit(slayer, 300, 500);
		slayer.setPosition(-100, 354);
		slayer.getRender().setState(STATE.walking);

		tank = new Unit("earth_tank", true);
		tank.getRender().setFacing(FACING.left);
		world.addUnit(tank, 900, 500);
		tank.setPosition(CrystalClash.WIDTH + 100, 354);
		tank.getRender().setState(STATE.walking);

		archer = new Unit("fire_archer", false);
		world.addUnit(archer, 550, 500);
		archer.setPosition(-100, 354);
		archer.getRender().setState(STATE.walking);

		slayerMove = new MoveUnitAction();
		slayerMove.origin = world.cellAt(300, 700);

		slayerMove.moves.add(world.cellAtByGrid(1, 3));
		slayerMove.moves.add(world.cellAtByGrid(2, 3));
		slayerMove.moves.add(world.cellAtByGrid(3, 3));
		slayerMove.moves.add(world.cellAtByGrid(3, 4));
		slayerMove.moves.add(world.cellAtByGrid(4, 4));
	}

	private void next() {
		if (messageIndex < 32)
			messageIndex++;

		if (messageIndex + 1 < messages.size) {
			lblMessage.setText(messages.get(messageIndex));
		} else {
			lblMessage.setText("");
			blockButtons = true;
			btnNext.setDisabled(true);
			Timeline.createParallel()
					.push(Tween.to(fireArcher, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED).target(-fireArcher.getWidth()))
					.push(Tween.to(balloon, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(-balloon.getHeight()))
					.push(Tween.to(lblMessage, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(-balloon.getHeight()))
					.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(-btnNext.getHeight()))
					.start(tweenManager);
		}
		action();
	}

	private void action() {
		switch (messageIndex) {
		case 2:
			world.getRender().moveHand(slayer);
			break;
		case 3:
			world.getRender().setReadInput(true);
			hideNext();
			break;
		case 4:
			addActor(imgAttackIcon);
			addActor(imgMoveIcon);
			addActor(imgDefendIcon);
			addActor(imgUndoIcon);

			world.getRender().setReadInput(false);
			showNext();
			break;
		case 5:
			removeActor(imgAttackIcon);
			removeActor(imgMoveIcon);
			removeActor(imgDefendIcon);
			removeActor(imgUndoIcon);
			break;
		case 6:
			addActor(imgLifeIcon);
			addActor(imgDamageIcon);
			addActor(imgMobilityIcon);

			world.getRender().hideActionsRing();
			world.cellAt(300, 500).removeState(Cell.SELECTED);
			actionRingVisible = false;
			world.getRender().showStatsPopup(slayer);
			break;
		case 7:
			removeActor(imgLifeIcon);
			removeActor(imgDamageIcon);
			removeActor(imgMobilityIcon);

			world.getRender().hideStatsPopup();
			world.getRender().moveArrow(tank);
			break;
		case 8:
			world.getRender().hideArrow();
			world.getRender().moveHand(slayer);
			world.getRender().setReadInput(true);
			hideNext();
			break;
		case 9:
			world.getRender().moveArrow(695, CrystalClash.HEIGHT - 230);
			world.cellAtByGrid(1, 3).addState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(2, 3).addState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(3, 3).addState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(3, 4).addState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(4, 4).addState(Cell.ABLE_TO_MOVE);
			showNext();
			break;
		case 10:
			world.getRender().setReadInput(true);
			hideNext();
			break;
		case 12:
			world.getRender().hideHand();
			showNext();
			break;
		case 14:
			world.getRender().setBlockButtons(false);
			world.getRender().moveHand(0, 125);
			hideNext();
			break;
		case 15:
			world.getRender().setBlockButtons(true);
			world.getRender().setReadInput(false);
			showNext();
			break;
		case 16:
			btnNext.setDisabled(true);
			blockButtons = true;
			playAnimation2();
			break;
		case 18:
			world.getRender().setReadInput(true);
			world.getRender().moveHand(slayer);
			hideNext();
			break;
		case 19:
			showNext();
			break;
		case 20:
			world.getRender().moveHand(archer);
			world.getRender().setBlockButtons(true);
			hideNext();
			break;
		case 24:
			world.getRender().setBlockButtons(false);
			world.getRender().moveHand(0, 125);
			hideNext();
			break;
		case 25:
			world.getRender().setReadInput(false);
			world.getRender().setBlockButtons(true);
			showNext();
			break;
		case 26:
			world.getRender().setReadInput(true);
			hideNext();
			break;
		case 27:
			world.getRender().setReadInput(false);
			showNext();
			break;
		case 32:
			world.getRender().setReadInput(true);
			archerAttacked = false;
			slayerAttacked = false;
			actionInProgress = false;
			selectedUnit = null;
			break;
		}
	}

	private void hideNext() {
		blockButtons = true;
		btnNext.setDisabled(true);
		Timeline.createSequence()
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(-btnNext.getHeight())).start(tweenManager);
	}

	private void showNext() {
		blockButtons = false;
		btnNext.setDisabled(false);
		Timeline.createSequence()
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(20)).start(tweenManager);

		btnNext.rotate(-10f);
		Timeline.createParallel()
				.push(Tween.to(btnNext, ActorAccessor.ROTATION, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(20f)
						.repeatYoyo(5, 0))
				.repeat(-1, 2f).start(tweenManager);
	}

	@Override
	public void clearAllChanges() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Cell cell = world.cellAt(x, y);
		if (cell != null) {

			switch (messageIndex) {
			case 10:
				if (cell.hasState(Cell.ABLE_TO_MOVE)) {
					if (!cell.hasState(Cell.MOVE_TARGET)) {
						cell.addState(Cell.MOVE_TARGET);
						PathRender p = paths.getOrCreatePath(slayer, PathRender.TYPE.MOVE);

						for (; slayerMove.moves.get(movePathIndex) != cell; movePathIndex++) {
							PathManager.addLine(p,
									slayerMove.moves.get(movePathIndex).getCenterX(),
									slayerMove.moves.get(movePathIndex).getCenterY(),
									slayerMove.moves.get(movePathIndex + 1).getCenterX(),
									slayerMove.moves.get(movePathIndex + 1).getCenterY(),
									PathRender.DOT_CENTER_X,
									PathRender.DOT_CENTER_Y);
						}

						if (movePathIndex == slayerMove.moves.size - 1) {
							world.getRender().hideArrow();
							next();
						}
					}
				}
				break;
			default: {
				Unit u = cell.getUnit();
				if (u != null) {
					if (!u.isEnemy()) {
						switch (messageIndex) {
						case 3:
							if (!actionRingVisible) {
								actionRingVisible = true;
								world.getRender().selectUnitInCell(cell);
								world.getRender().hideHand();
								next();
							}
							break;
						case 8:
							if (!actionRingVisible) {
								actionRingVisible = true;
								world.getRender().selectUnitInCell(cell);
								world.getRender().moveHand(370, CrystalClash.HEIGHT - 240);
							}
							break;
						case 18:
							if (u.equals(slayer)) {
								if (!actionRingVisible) {
									actionRingVisible = true;
									world.getRender().selectUnitInCell(cell);
									world.getRender().moveHand(540, CrystalClash.HEIGHT / 2 + 110);
								}
							}
							break;
						case 20:
							if (u.equals(archer)) {
								if (!actionRingVisible) {
									actionRingVisible = true;
									world.getRender().selectUnitInCell(cell);
									world.getRender().moveHand(375, CrystalClash.HEIGHT / 2 + 185);
								}
							}
							break;
						case 26:
						case 32:
							boolean canShow = (u.equals(archer) && !archerAttacked) || (u.equals(slayer) && !slayerAttacked);
							if (!actionInProgress && canShow && !u.equals(selectedUnit)) {
								selectedUnit = u;
								if (!actionRingVisible) {
									actionRingVisible = true;
									world.getRender().selectUnitInCell(cell);
								}
							}
							break;
						}
					} else {
						if (cell.hasState(Cell.ABLE_TO_ATTACK)) {
							switch (messageIndex) {
							case 21:
								cell.removeState(Cell.ABLE_TO_ATTACK);

								PathRender p = paths.createOrResetPath(archer, PathRender.TYPE.ATTACK);
								PathManager.addArc(p,
										world.cellAtByGrid(3, 3).getCenterX(),
										world.cellAtByGrid(3, 3).getCenterY(),
										cell.getCenterX(),
										cell.getCenterY(),
										PathRender.DOT_CENTER_X,
										PathRender.DOT_CENTER_Y);

								world.getRender().setBlockButtons(false);
								next();
								break;
							case 26:
							case 32:
								cell.removeState(Cell.ABLE_TO_ATTACK);

								if (selectedUnit.equals(archer) && !archerAttacked) {
									PathRender archerA = paths.createOrResetPath(archer, PathRender.TYPE.ATTACK);
									PathManager.addArc(archerA,
											world.cellAtByGrid(3, 3).getCenterX(),
											world.cellAtByGrid(3, 3).getCenterY(),
											cell.getCenterX(),
											cell.getCenterY(),
											PathRender.DOT_CENTER_X,
											PathRender.DOT_CENTER_Y);
								} else if (selectedUnit.equals(slayer) && !slayerAttacked) {
									PathRender slayerA = paths.createOrResetPath(slayer, PathRender.TYPE.ATTACK);
									PathManager.addLine(slayerA,
											world.cellAtByGrid(4, 4).getCenterX(),
											world.cellAtByGrid(4, 4).getCenterY(),
											cell.getCenterX(),
											cell.getCenterY(),
											PathRender.DOT_CENTER_X,
											PathRender.DOT_CENTER_Y);
								}

								world.getRender().setBlockButtons(false);
								break;
							}
						}
					}
				}
			}
			}
		} else {
			switch (messageIndex) {
			case 11:
				next();
				slayer.getRender().playSFX(SOUND.chose_move);
				world.getRender().deselectUnitInCell(world.cellAtByGrid(1, 3));
				world.getRender().setBlockButtons(true);
				break;
			case 22:
				showNext();
				next();
				archer.getRender().playSFX(SOUND.chose_attack);
				world.getRender().deselectUnitInCell(world.cellAtByGrid(3, 3));
				world.getRender().setBlockButtons(true);
				break;
			case 26:
				if (selectedUnit.equals(archer)) {
					archerAttacked = true;
					archer.getRender().playSFX(SOUND.chose_attack);
					world.getRender().deselectUnitInCell(world.cellAtByGrid(3, 3));
				} else if (selectedUnit.equals(slayer)) {
					slayerAttacked = true;
					slayer.getRender().playSFX(SOUND.chose_attack);
					world.getRender().deselectUnitInCell(world.cellAtByGrid(4, 4));
				}

				actionInProgress = false;
				if (archerAttacked && slayerAttacked) {
					world.getRender().setBlockButtons(false);
				}
				break;
			case 32:
				if (selectedUnit.equals(archer)) {
					archerAttacked = true;
					archer.getRender().playSFX(SOUND.chose_attack);
					world.getRender().deselectUnitInCell(world.cellAtByGrid(3, 3));
				} else if (selectedUnit.equals(slayer)) {
					slayerAttacked = true;
					slayer.getRender().playSFX(SOUND.chose_attack);
					world.getRender().deselectUnitInCell(world.cellAtByGrid(4, 4));
				}

				actionInProgress = false;
				if (archerAttacked && slayerAttacked) {
					world.getRender().setBlockButtons(false);
				}
				break;
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(float screenX, float screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(float screenX, float screenY, int pointer) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		Timeline moveslayer = Timeline.createParallel()
				.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAt(300, 500))).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAt(300, 500))).ease(TweenEquations.easeNone))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						// TODO Auto-generated method stub
						slayer.getRender().setState(STATE.idle);
					}
				});

		Timeline moveTank = Timeline.createParallel()
				.push(Tween.to(tank, UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAt(900, 500))).ease(TweenEquations.easeNone))
				.push(Tween.to(tank, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAt(900, 500))).ease(TweenEquations.easeNone))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						// TODO Auto-generated method stub
						tank.getRender().setState(STATE.idle);
					}
				});

		Timeline tutorialStuff = Timeline.createParallel()
				.push(Tween.to(fireArcher, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(200))
				.push(Tween.to(balloon, ActorAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(0))
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(20))
				.push(Tween.to(imgBtnSkipBackground, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(
						CrystalClash.WIDTH - imgBtnSkipBackground.getWidth()))
				.push(Tween.to(btnSkip, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(CrystalClash.WIDTH - btnSkip.getWidth()))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == COMPLETE) {
							lblMessage.setPosition(balloon.getX(), balloon.getY());
							lblMessage.setText(messages.get(messageIndex));

							btnNext.setDisabled(false);
							btnSkip.setDisabled(false);
							blockButtons = false;
						}
					}
				});

		return t.push(moveslayer).push(moveTank).push(tutorialStuff);
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		world.getRender().hideHand();
		world.getRender().hideArrow();
		world.getRender().hideActionsRing();
		world.getRender().hideStatsPopup();

		Timeline tutorialStuff = Timeline.createParallel()
				.push(Tween.to(fireArcher, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(-fireArcher.getWidth() * 2))
				.push(Tween.to(balloon, ActorAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(-balloon.getWidth() * 2))
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(-balloon.getWidth() * 2))
				.push(Tween.to(imgBtnSkipBackground, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(CrystalClash.WIDTH))
				.push(Tween.to(btnSkip, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(CrystalClash.WIDTH));

		return t.push(tutorialStuff);
	}

	@Override
	public boolean canSend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSend() {
		switch (messageIndex) {
		case 14:
			world.getRender().hideHand();

			world.cellAtByGrid(1, 3).removeState(Cell.ABLE_TO_MOVE | Cell.MOVE_TARGET);
			world.cellAtByGrid(2, 3).removeState(Cell.ABLE_TO_MOVE | Cell.MOVE_TARGET);
			world.cellAtByGrid(3, 3).removeState(Cell.ABLE_TO_MOVE | Cell.MOVE_TARGET);
			world.cellAtByGrid(3, 4).removeState(Cell.ABLE_TO_MOVE | Cell.MOVE_TARGET);
			world.cellAtByGrid(4, 4).removeState(Cell.ABLE_TO_MOVE | Cell.MOVE_TARGET);
			paths.removePath(slayer);
			playAnimation1();
			world.getRender().setBlockButtons(true);
			break;
		case 24:
			paths.removePath(archer);
			world.getRender().hideHand();
			playAnimation3();
			world.getRender().setBlockButtons(true);
			break;
		case 26:
			if (archerAttacked && slayerAttacked) {
				paths.removePath(slayer);
				paths.removePath(archer);
				world.getRender().setBlockButtons(true);
				playAnimation4();
			}
			break;
		case 32:
			if (archerAttacked && slayerAttacked) {
				paths.removePath(slayer);
				paths.removePath(archer);
				world.getRender().setBlockButtons(true);
				playAnimation5();
			}
			break;
		}
	}

	@Override
	public void pause() {
		tweenManager.pause();
	}

	@Override
	public void resume() {
		tweenManager.resume();
	}

	@Override
	public void onAttackAction() {
		switch (messageIndex) {
		case 20:
			next();
			world.getRender().hideActionsRing();
			actionRingVisible = false;
			world.getRender().hideHand();
			world.cellAtByGrid(5, 3).addState(Cell.ABLE_TO_ATTACK);
			break;
		case 26:
			actionInProgress = true;
			world.getRender().hideActionsRing();
			actionRingVisible = false;
			world.cellAtByGrid(5, 3).addState(Cell.ABLE_TO_ATTACK);
			break;
		case 32:
			actionInProgress = true;
			world.getRender().hideActionsRing();
			actionRingVisible = false;
			world.cellAtByGrid(4, 3).addState(Cell.ABLE_TO_ATTACK);
			break;
		}
	}

	@Override
	public void onDefendAction() {
		switch (messageIndex) {
		case 18:
			slayer.getRender().playSFX(SOUND.chose_defend);
			slayer.setDefendingPosition(true);
			world.getRender().deselectUnitInCell(world.cellAtByGrid(4, 4));
			world.getRender().hideHand();
			next();
			actionRingVisible = false;
			world.getRender().setBlockButtons(false);
			break;
		}
	}

	@Override
	public void onMoveAction() {
		switch (messageIndex) {
		case 8:
			world.getRender().hideActionsRing();
			actionRingVisible = false;
			world.getRender().hideHand();
			next();
			break;
		}
	}

	@Override
	public void onUndoAction() {
	}

	private void playAnimation1() {
		Timeline slayerM = Timeline.createSequence()
				.beginParallel()
				.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(2, 3))).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(2, 3))).ease(TweenEquations.easeNone))
				.end().beginParallel()
				.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(3, 3))).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(3, 3))).ease(TweenEquations.easeNone))
				.end().beginParallel()
				.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(3, 4))).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(3, 4))).ease(TweenEquations.easeNone))
				.end().beginParallel()
				.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(4, 4))).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(4, 4))).ease(TweenEquations.easeNone))
				.end()
				.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == TweenCallback.COMPLETE) {
							slayer.getRender().setState(STATE.idle);
							world.cellAtByGrid(1, 3).removeUnit();
							world.cellAtByGrid(4, 4).setUnit(slayer);
							world.cellAtByGrid(4, 4).setAction(new NoneUnitAction());
						} else {
							slayer.getRender().setState(STATE.walking);
						}
					}
				});

		Timeline tankM = Timeline.createSequence()
				.beginParallel()
				.push(Tween.to(tank, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(5, 3))).ease(TweenEquations.easeNone))
				.push(Tween.to(tank, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(5, 3))).ease(TweenEquations.easeNone))
				.end()
				.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == TweenCallback.COMPLETE) {
							tank.getRender().setState(STATE.idle);
							world.cellAtByGrid(6, 3).removeUnit();
							world.cellAtByGrid(5, 3).setUnit(tank);
						} else {
							tank.getRender().setState(STATE.walking);
						}
					}
				});

		Timeline.createSequence()
				.push(slayerM)
				.push(tankM)
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == COMPLETE)
							next();
					}
				}).start(tweenManager);
	}

	private void playAnimation2() {
		Timeline.createParallel()
				.push(Tween.to(archer, UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(3, 3))).ease(TweenEquations.easeNone))
				.push(Tween.to(archer, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(3, 3))).ease(TweenEquations.easeNone))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == COMPLETE) {
							archer.getRender().setState(STATE.idle);
							btnNext.setDisabled(false);
							blockButtons = false;
						}
					}
				}).start(tweenManager);
	}

	private void playAnimation3() {
		Timeline tankAtt = Timeline.createSequence();
		Timeline tankMove = Timeline.createParallel()
				.push(Tween.to(tank, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(4, 4)) + 45).ease(TweenEquations.easeNone))
				.push(Tween.to(tank, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(4, 4))).ease(TweenEquations.easeNone));
		tankMove.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
		tankMove.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.COMPLETE) {
					tank.getRender().setState(STATE.fighting);
					slayer.damage(tank.getDamage());
				} else if (type == TweenCallback.BEGIN) {
					tank.getRender().setState(STATE.walking);
				}
			}
		});
		tankAtt.push(tankMove);

		Timeline tankMoveBack = Timeline.createParallel();
		tankMoveBack.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
		tankMoveBack.push(Tween
				.to(tank, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitX(world.cellAtByGrid(5, 3))).ease(TweenEquations.easeNone));
		tankMoveBack.push(Tween
				.to(tank, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitY(world.cellAtByGrid(5, 3))).ease(TweenEquations.easeNone));
		tankMoveBack.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
		tankMoveBack.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.COMPLETE) {
					tank.getRender().setState(STATE.idle);
					tank.getRender().setFacing(FACING.left);
				} else {
					tank.getRender().setState(STATE.walking);
				}
			}
		});
		tankAtt.push(tankMoveBack);

		Timeline archerAttStart = Timeline.createSequence();
		archerAttStart.setCallbackTriggers(TweenCallback.BEGIN);
		archerAttStart.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				archer.getRender().setState(STATE.fighting);
				world.cellAtByGrid(5, 3).state = Cell.ATTACK_TARGET_CENTER;
			}
		});

		AttackUnitAction action = new AttackUnitAction(false);
		action.origin = world.cellAtByGrid(3, 3);
		action.target = world.cellAtByGrid(5, 3);
		attacks.pushAttack(archerAttStart, action, entities);

		Timeline archerAttStop = Timeline.createSequence();
		archerAttStop.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
		archerAttStop.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				tank.damage(200);
				archer.getRender().setState(STATE.idle);
				world.cellAtByGrid(5, 3).state = Cell.NONE;
			}
		});

		Timeline.createSequence()
				.push(tankAtt)
				.push(archerAttStart)
				.push(archerAttStop)
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == COMPLETE) {
							slayer.setDefendingPosition(false);
							next();
						}
					}
				}).start(tweenManager);
	}

	private void playAnimation4() {
		Timeline slayerAtt = Timeline.createSequence();
		Timeline slayerMove = Timeline.createParallel()
				.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(5, 3)) - 45).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(5, 3))).ease(TweenEquations.easeNone));
		slayerMove.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
		slayerMove.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.COMPLETE) {
					slayer.getRender().setState(STATE.fighting);
					tank.damage(200);
				} else if (type == TweenCallback.BEGIN) {
					slayer.getRender().setState(STATE.walking);
				}
			}
		});
		slayerAtt.push(slayerMove);

		Timeline slayerMoveBack = Timeline.createParallel();
		slayerMoveBack.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
		slayerMoveBack.push(Tween
				.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitX(world.cellAtByGrid(4, 4))).ease(TweenEquations.easeNone));
		slayerMoveBack.push(Tween
				.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitY(world.cellAtByGrid(4, 4))).ease(TweenEquations.easeNone));
		slayerMoveBack.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
		slayerMoveBack.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.COMPLETE) {
					slayer.getRender().setState(STATE.idle);
					slayer.getRender().setFacing(FACING.right);
				} else {
					slayer.getRender().setState(STATE.walking);
				}
			}
		});
		slayerAtt.push(slayerMoveBack);

		Timeline tankM = Timeline.createParallel()
				.push(Tween.to(tank, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(4, 3))).ease(TweenEquations.easeNone))
				.push(Tween.to(tank, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(4, 3))).ease(TweenEquations.easeNone))
				.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == TweenCallback.COMPLETE) {
							tank.getRender().setState(STATE.idle);
							world.cellAtByGrid(5, 3).removeUnit();
							world.cellAtByGrid(4, 3).setUnit(tank);
						} else {
							tank.getRender().setState(STATE.walking);
						}
					}
				});

		Timeline archerAttStart = Timeline.createSequence();
		archerAttStart.setCallbackTriggers(TweenCallback.BEGIN);
		archerAttStart.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				archer.getRender().setState(STATE.fighting);
				world.cellAtByGrid(5, 3).state = Cell.ATTACK_TARGET_CENTER;
			}
		});

		AttackUnitAction action = new AttackUnitAction(false);
		action.origin = world.cellAtByGrid(3, 3);
		action.target = world.cellAtByGrid(5, 3);
		attacks.pushAttack(archerAttStart, action, entities);

		Timeline archerAttStop = Timeline.createSequence();
		archerAttStop.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
		archerAttStop.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				archer.getRender().setState(STATE.idle);
				world.cellAtByGrid(5, 3).state = Cell.NONE;
			}
		});

		Timeline.createSequence()
				.push(slayerAtt)
				.push(tankM)
				.push(archerAttStart)
				.push(archerAttStop)
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == COMPLETE) {
							world.getRender().setReadInput(true);
							next();
						}
					}
				}).start(tweenManager);
	}

	private void playAnimation5() {
		Timeline slayerAtt = Timeline.createSequence();
		Timeline slayerMove = Timeline.createParallel()
				.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAtByGrid(4, 3)) + 45).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAtByGrid(4, 3))).ease(TweenEquations.easeNone));
		slayerMove.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
		slayerMove.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.COMPLETE) {
					slayer.getRender().setState(STATE.fighting);
					tank.damage(200);
				} else if (type == TweenCallback.BEGIN) {
					slayer.getRender().setState(STATE.walking);
				}
			}
		});
		slayerAtt.push(slayerMove);

		Timeline slayerMoveBack = Timeline.createParallel();
		slayerMoveBack.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
		slayerMoveBack.push(Tween
				.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitX(world.cellAtByGrid(4, 4))).ease(TweenEquations.easeNone));
		slayerMoveBack.push(Tween
				.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitY(world.cellAtByGrid(4, 4))).ease(TweenEquations.easeNone));
		slayerMoveBack.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
		slayerMoveBack.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.COMPLETE) {
					slayer.getRender().setState(STATE.idle);
					slayer.getRender().setFacing(FACING.right);
				} else {
					slayer.getRender().setState(STATE.walking);
				}
			}
		});
		slayerAtt.push(slayerMoveBack);

		Timeline archerAttStart = Timeline.createSequence();
		archerAttStart.setCallbackTriggers(TweenCallback.BEGIN);
		archerAttStart.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				archer.getRender().setState(STATE.fighting);
				world.cellAtByGrid(4, 3).state = Cell.ATTACK_TARGET_CENTER;
			}
		});

		AttackUnitAction action = new AttackUnitAction(false);
		action.origin = world.cellAtByGrid(3, 3);
		action.target = world.cellAtByGrid(4, 3);
		attacks.pushAttack(archerAttStart, action, entities);

		Timeline archerAttStop = Timeline.createSequence();
		archerAttStop.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
		archerAttStop.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				tank.damage(200);
				archer.getRender().setState(STATE.idle);
				world.cellAtByGrid(4, 3).state = Cell.NONE;
			}
		});

		Timeline tankDeath = Timeline.createSequence()
				.setCallbackTriggers(TweenCallback.BEGIN)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						tank.getRender().setState(STATE.dieing);
					}
				});

		Timeline tankDeathTime = Timeline.createSequence()
				.delay(5);

		Timeline.createSequence()
				.push(slayerAtt)
				.push(archerAttStart)
				.push(archerAttStop)
				.push(tankDeath)
				.push(tankDeathTime)
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if (type == COMPLETE) {
							AudioManager.playEndSound(GAME_END_SFX.victory);
							Image endGameMessage = new Image(ResourceHelper.getTexture("turn_animation/messages/banner_victory"));
							TextButton btnBackToMenu = new TextButton("Back to menu", ResourceHelper.getButtonStyle());
							btnBackToMenu.addListener(new ClickListener() {
								@Override
								public void clicked(InputEvent event, float x, float y) {
									GameController.setTutorialDone();
									GameEngine.showLoading();
									GameEngine.getInstance().openMenuGames();
								}
							});
							endGameMessage.setPosition(CrystalClash.WIDTH / 2 - endGameMessage.getWidth() / 2,
									CrystalClash.HEIGHT);
							btnBackToMenu.setPosition(endGameMessage.getX() + endGameMessage.getWidth() / 2 - btnBackToMenu.getWidth() / 2,
									endGameMessage.getY() + endGameMessage.getHeight() / 2 - btnBackToMenu.getHeight() / 2);

							addActor(btnBackToMenu);
							addActor(endGameMessage);

							Timeline.createSequence()
									.beginParallel()
									.push(Tween.to(endGameMessage, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
											.target(CrystalClash.HEIGHT / 2 - endGameMessage.getHeight() / 2))
									.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
											.target(CrystalClash.HEIGHT / 2 - btnBackToMenu.getHeight() / 2))
									.end()
									.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
											.target(CrystalClash.HEIGHT / 2 - endGameMessage.getHeight() / 2 - btnBackToMenu.getHeight())
											.ease(TweenEquations.easeOutBounce)).start(tweenManager);
						}
					}
				}).start(tweenManager);
	}

	@Override
	public void renderInTheBack(float dt, SpriteBatch batch) {
		tweenManager.update(dt);
		paths.render(batch, dt, PathRender.TYPE.MOVE);
	}

	@Override
	public void renderInTheFront(float dt, SpriteBatch batch) {
		paths.render(batch, dt, PathRender.TYPE.ATTACK);
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

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}