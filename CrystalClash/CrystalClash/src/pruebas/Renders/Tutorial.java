package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Accessors.UnitAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Path;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.AttackUnitAction;
import pruebas.Entities.helpers.MoveUnitAction;
import pruebas.Entities.helpers.NoneUnitAction;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.UnitRender.STATE;
import pruebas.Renders.helpers.CellHelper;
import pruebas.Renders.helpers.PathManager;
import pruebas.Renders.helpers.ResourceHelper;
import pruebas.Renders.helpers.ui.MessageBox;
import pruebas.Renders.helpers.ui.MessageBoxCallback;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class Tutorial extends GameRender {

	private static TweenManager tweenManager;

	private Image fireArcher;
	private Image balloon;
	private Label lblMessage;
	private TextButton btnNext;
	private TextButton btnSkip;
	private Image imgBtnSkipBackground;

	private Array<String> messages;
	private int messageIndex;

	private Unit slayer;
	private Unit tank;
	private Unit archer;
	private MoveUnitAction slayerMove;

	private boolean blockButtons;
	private int movePathX;
	
	private PathManager paths;

	public Tutorial(WorldController world) {
		super(world);
		messageIndex = 0;
		movePathX = 2;

		load();
		readTutorialScript();
		GameController.loadUnitsStats();
		setData();

		world.getRender().setReadInput(false);
		world.getRender().setBlockButtons(true);

		btnNext.setDisabled(true);
		btnSkip.setDisabled(true);
		blockButtons = true;
		
		paths = new PathManager();
		
		GameEngine.hideLoading();
	}

	public void load() {
		TextureAtlas atlas = ResourceHelper.getTextureAtlas("in_game/options_bar.pack");
		Skin skin = new Skin(atlas);

		tweenManager = new TweenManager();
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		fireArcher = new Image(ResourceHelper.getTexture("tutorial/fire_archer"));
		fireArcher.scale(-0.55f);
		fireArcher.setPosition(-fireArcher.getWidth(), 0);

		balloon = new Image(ResourceHelper.getTexture("tutorial/message_balloon"));
		balloon.scale(-0.1f);
		balloon.setPosition(160 + fireArcher.getWidth() * 0.45f, -balloon.getHeight());

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.BLACK));
		lblMessage.setPosition(balloon.getX() + 145, balloon.getTop() - 50);

		btnNext = new TextButton("Next", ResourceHelper.getNextButtonStyle());
		btnNext.setPosition(CrystalClash.WIDTH - btnNext.getWidth() - 20, -balloon.getHeight() - btnNext.getHeight());
		btnNext.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!blockButtons)
					next();
			}
		});

		final MessageBoxCallback confirmation = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {

				} else {
					MessageBox.build().hide();
				}
			}
		};

		imgBtnSkipBackground = new Image(skin.getRegion("exit_hud"));
		imgBtnSkipBackground.setPosition(CrystalClash.WIDTH, CrystalClash.HEIGHT - imgBtnSkipBackground.getHeight());
		TextButtonStyle skipStyle = new TextButtonStyle(
				skin.getDrawable("exit_button"),
				skin.getDrawable("exit_button_pressed"), null, ResourceHelper.getFont());
		btnSkip = new TextButton("", skipStyle);
		btnSkip.setPosition(CrystalClash.WIDTH, CrystalClash.HEIGHT - btnSkip.getHeight());
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String text = "We have just started...";
				if (messageIndex >= messages.size / 2)
					text = "We are half road down...";
				if (messageIndex >= messages.size - 8)
					text = "We are almost finished...";

				MessageBox.build()
						.setMessage("Are you sure you want to leave?\n" + text)
						.twoButtonsLayout("Yes, i got it", "No, let's go on")
						.setCallback(confirmation)
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
	}

	private void readTutorialScript() {
		messages = new Array<String>();
		// Scene 1
		messages.add("Welcome to the front line.\nYou must lead our troop!"); // 0
		messages.add("Our goal is to defeat the\nenemies army."); // 1
		messages.add("Look!! There's an ally\nover there."); // 2
		messages.add("Tap to select him!!"); // 3
		messages.add("Those are the thing you\ncan ask him to do.\n\nAttack, Move or Defend!"); // 4
		messages.add("Keep in mind he can only\ndo one at a time!!"); // 5
		messages.add("Here you can see his life,\ndamage and mobility"); // 6
		messages.add("Watch out !! there's an\nenemy there. "); // 7
		messages.add("Let's get closer to attack."); // 8
		messages.add("This seems like a good\nposition."); // 9
		messages.add("You must get there by\ndescribing the road."); // 10
		messages.add("To confirm the road tap\nthe tick."); // 11
		messages.add("Good Job!! You have ordered\nhim to move there."); // 12
		messages.add("Your orders will be executed\nonce you end your turn."); // 13
		messages.add("Tap Tick!!"); // 14
		// Scene 2
		messages.add("oh oh. . . I wasn't counting\non the enemy moving."); // 15
		messages.add("I'm going to help him."); // 16
		messages.add("The enemy seems to be very\ntough."); // 17
		messages.add("Order him to take a\ndefensive position while\nI cover him."); // 18
		messages.add("Don't forget to tap the\ntick to confirm your orders."); // 19
		messages.add("I should attack him."); // 20
		messages.add("I'm ranged. I can attack\nfrom far away"); // 21
		messages.add("Aren't you forgetting\nsomething?"); // 22
		messages.add("Now you should end\nyour turn.\nDo you remember how?"); // 23
		messages.add("Yes!\nExactly!"); // 24
		// Scene 3
		messages.add("Now the enemy seems weaker."); // 25
		messages.add("Let's both attack to\ndefeat him!!"); // 26
		// Scene 4
		messages.add("Oh crap!! He moved. . .\nI missed!!"); // 27
		messages.add("That's the problem we\n(ranged units) have."); // 28
		messages.add("They can dodge our shots\nby moving."); // 29
		messages.add("But if we can foresee\nwhere he's moving,\nwe can hit him."); // 30
		messages.add("Now you are ready to\nfight on your own."); // 31
		messages.add("Defeat him to achieve\nyour goal!!"); // 32
	}

	private void setData() {
		slayer = new Unit("wind_slayer", false);
		world.addUnit(slayer, 300, 700);
		slayer.setPosition(-100, 354);
		slayer.getRender().setState(STATE.walking);

		tank = new Unit("earth_tank", true);
		tank.getRender().setFacing(FACING.left);
		world.addUnit(tank, 900, 500);
		tank.setPosition(CrystalClash.WIDTH + 100, 354);
		tank.getRender().setState(STATE.walking);
		
		archer = new Unit("fire_archer", false);
		world.addUnit(archer, 650, 500);
		archer.setPosition(-100, 354);
		archer.getRender().setState(STATE.walking);

		slayerMove = new MoveUnitAction();
		slayerMove.origin = world.cellAt(300, 700);
	}

	private void next() {
		messageIndex++;
		if (messageIndex < messages.size) {
			lblMessage.setText(messages.get(messageIndex));
		} else {
			world.getRender().setReadInput(true);
			world.getRender().setBlockButtons(false);
			lblMessage.setText("");
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
			world.getRender().setReadInput(false);
			showNext();
			break;
		case 6:
			world.getRender().hideActionsRing();
			world.getRender().showStatsPopup(slayer);
			break;
		case 7:
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
			world.getRender().moveHand(675, CrystalClash.HEIGHT - 300);
			world.cellAtByGrid(5, 4).addState(Cell.MOVE_TARGET);
			showNext();
			break;
		case 10:
			world.getRender().setReadInput(true);
			hideNext();
			break;
		case 11:
			world.getRender().moveHand(0, 125);
			world.getRender().setBlockButtons(false);
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
			Timeline.createParallel()
					.push(Tween.to(archer, UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
							.target(CellHelper.getUnitX(world.cellAt(650, 500))).ease(TweenEquations.easeNone))
					.push(Tween.to(archer, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
							.target(CellHelper.getUnitY(world.cellAt(650, 500))).ease(TweenEquations.easeNone))
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
			break;
		case 18:
			world.getRender().setReadInput(true);
			world.getRender().moveHand(slayer);
			hideNext();
			break;
		case 24:
			world.getRender().setBlockButtons(false);
			world.getRender().moveHand(0, 125);
			hideNext();
			break;
		case 25:
			world.getRender().setReadInput(false);
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
	}

	@Override
	public void clearAllChanges() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Cell cell = world.cellAt(x, y);
		if (cell != null) {
			Unit u = cell.getUnit();
			if (u != null) {
				if (!u.isEnemy()) {
					switch (messageIndex) {
					case 3:
						world.getRender().moveActionsRing(cell);
						world.getRender().hideHand();
						next();
						break;
					case 8:
						world.getRender().moveActionsRing(cell);
						world.getRender().moveHand(390, CrystalClash.HEIGHT - 75);
						break;
					case 18:
						if(u.equals(slayer)){
							world.getRender().moveActionsRing(cell);
							world.getRender().moveHand(540, CrystalClash.HEIGHT / 2 + 110);
						}
						break;
					case 20:
						if(u.equals(archer)){
							world.getRender().moveActionsRing(cell);
							world.getRender().moveHand(460, CrystalClash.HEIGHT / 2 + 185);
						}
						break;
					case 26:
						world.getRender().moveActionsRing(cell);
						break;
					}
				} else {
					if (cell.hasState(Cell.ABLE_TO_ATTACK)) {
						switch (messageIndex) {
						case 21:
							cell.removeState(Cell.ABLE_TO_ATTACK);
							cell.addState(Cell.ATTACK_TARGET_CENTER);

							Path p = paths.createOrResetPath(archer, Path.TYPE.ATTACK);
							PathManager.addArc(p,
									world.cellAtByGrid(4, 3).getCenterX(),
									world.cellAtByGrid(4, 3).getCenterY(),
									cell.getCenterX(),
									cell.getCenterY());

							world.getRender().moveHand(0, 125);
							world.getRender().setBlockButtons(false);
							next();
							break;
						}
					}
				}
			} else {
				if (cell.hasState(Cell.ABLE_TO_MOVE)) {
					switch (messageIndex) {
					case 10:
						Array<Cell> moves = slayerMove.moves;
						Path p = paths.getOrCreatePath(slayer, Path.TYPE.MOVE);

						if (moves.size == 0) {
							PathManager.addLine(p,
									world.cellAtByGrid(1, 5).getCenterX(),
									world.cellAtByGrid(1, 5).getCenterY(),
									cell.getCenterX(),
									cell.getCenterY());
						} else {
							PathManager.addLine(p,
									moves.get(moves.size - 1).getCenterX(),
									moves.get(moves.size - 1).getCenterY(),
									cell.getCenterX(),
									cell.getCenterY());
						}
						
						slayerMove.moves.add(world.cellAtByGrid(movePathX, 4));
						movePathX++;
						
						if(cell.hasState(Cell.MOVE_TARGET)) {
							next();
						} else {
							world.cellAtByGrid(movePathX, 4).addState(Cell.ABLE_TO_MOVE);
							
//							Unit ghost;
//							if (moves.size > 1) {
//								ghost = popUnitFromPath(moves);
//							} else {
//								ghost = new Unit(slayer.getName(), world.player);
//								ghost.getRender().setState(STATE.ghost);
//								ghostlyCells.add(cell);
//							}
						}
						break;
					}
				}
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
						.target(CellHelper.getUnitX(world.cellAt(300, 700))).ease(TweenEquations.easeNone))
				.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAt(300, 700))).ease(TweenEquations.easeNone))
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

		return t.push(moveslayer).push(moveTank)
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
							lblMessage.setPosition(balloon.getX() + 145, balloon.getTop() - 150);
							lblMessage.setText(messages.get(messageIndex));

							btnNext.setDisabled(false);
							btnSkip.setDisabled(false);
							blockButtons = false;
						}
					}
				});
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t;
	}

	@Override
	public boolean canSend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSend() {
		switch (messageIndex) {
		case 11:
			next();
			break;
		case 14:
			world.getRender().hideHand();
			world.cellAtByGrid(2, 4).removeState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(3, 4).removeState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(4, 4).removeState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(5, 4).removeState(Cell.ABLE_TO_MOVE);
			world.cellAtByGrid(5, 4).removeState(Cell.MOVE_TARGET);
			paths.removePath(slayer);
			Timeline slayerM = Timeline.createSequence()
					.beginParallel()
					.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitX(world.cellAtByGrid(2, 4))).ease(TweenEquations.easeNone))
					.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitY(world.cellAtByGrid(2, 4))).ease(TweenEquations.easeNone))
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
					.end().beginParallel()
					.push(Tween.to(slayer, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitX(world.cellAtByGrid(5, 4))).ease(TweenEquations.easeNone))
					.push(Tween.to(slayer, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitY(world.cellAtByGrid(5, 4))).ease(TweenEquations.easeNone))
					.end()
					.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							if (type == TweenCallback.COMPLETE) {
								slayer.getRender().setState(STATE.idle);
								world.cellAtByGrid(1, 5).removeUnit();
								world.cellAtByGrid(5, 4).setUnit(slayer);
								world.cellAtByGrid(5, 4).setAction(new NoneUnitAction());
							} else {
								slayer.getRender().setState(STATE.walking);
							}
						}
					});
			
			Timeline tankM = Timeline.createSequence()
					.beginParallel()
					.push(Tween.to(tank, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitX(world.cellAtByGrid(6, 3))).ease(TweenEquations.easeNone))
					.push(Tween.to(tank, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitY(world.cellAtByGrid(6, 3))).ease(TweenEquations.easeNone))
					.end()
					.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							if (type == TweenCallback.COMPLETE) {
								tank.getRender().setState(STATE.idle);
								world.cellAtByGrid(8, 4).removeUnit();
								world.cellAtByGrid(6, 3).setUnit(tank);
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
							if(type == COMPLETE)
								next();
						}
					}).start(tweenManager);
			break;
		case 19:
			world.getRender().moveHand(archer);
			next();
			break;
		case 22:
			world.getRender().hideHand();
			showNext();
			next();
			break;
		case 24:
			paths.removePath(archer);
			world.getRender().hideHand();
			Timeline tankAtt = Timeline.createSequence();
			Timeline tankMove = Timeline.createParallel()
					.push(Tween.to(tank, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitX(world.cellAtByGrid(5, 4)) + 45).ease(TweenEquations.easeNone))
					.push(Tween.to(tank, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
							.target(CellHelper.getUnitY(world.cellAtByGrid(5, 4))).ease(TweenEquations.easeNone));
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
					.target(CellHelper.getUnitX(world.cellAtByGrid(6, 3))).ease(TweenEquations.easeNone));
			tankMoveBack.push(Tween
					.to(tank, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitY(world.cellAtByGrid(6, 3))).ease(TweenEquations.easeNone));
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
			archerAttStart.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					archer.getRender().setState(STATE.fighting);
					world.cellAtByGrid(6, 3).addState(Cell.ATTACK_TARGET_CENTER);
					tank.damage(archer.getDamage());
				}
			});

			Timeline archerAttStop = Timeline.createSequence();
			archerAttStop.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
			archerAttStop.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					archer.getRender().setState(STATE.idle);
					world.cellAtByGrid(6, 3).removeState(Cell.ATTACK_TARGET_CENTER);
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
								world.cellAtByGrid(6, 3).removeState(Cell.ATTACK_TARGET_CENTER);
								next();
							}
						}
					}).start(tweenManager);
			break;
		}

		world.getRender().setBlockButtons(true);
	}

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
			world.getRender().hideHand();
			world.cellAtByGrid(6, 3).addState(Cell.ABLE_TO_ATTACK);
			break;
		case 26:
			world.getRender().hideActionsRing();
			world.cellAtByGrid(6, 3).addState(Cell.ABLE_TO_ATTACK);
			break;
		}
	}
	
	@Override
	public void onDefendAction() {
		switch (messageIndex) {
		case 18:
			slayer.setDefendingPosition(true);
			world.getRender().hideActionsRing();
			world.getRender().setBlockButtons(false);
			world.getRender().moveHand(0, 125);
			next();
			break;
		}
	}
	
	@Override
	public void onMoveAction() {
		switch (messageIndex) {
		case 8:
			world.getRender().hideActionsRing();
			world.cellAtByGrid(movePathX, 4).addState(Cell.ABLE_TO_MOVE);
			next();
			break;
		}
	}
	
	@Override
	public void onUndoAction() {
	}
	
	@Override
	public void renderInTheBack(float dt, SpriteBatch batch) {
		tweenManager.update(dt);
		paths.render(batch, dt, Path.TYPE.MOVE);
	}
	
	@Override
	public void renderInTheFront(float dt, SpriteBatch batch) {
		paths.render(batch, dt, Path.TYPE.ATTACK);
	}
}