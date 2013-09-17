package pruebas.Renders;

import java.util.Random;

import pruebas.Accessors.ActorAccessor;
import pruebas.Accessors.UnitAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.AttackUnitAction;
import pruebas.Entities.helpers.DefendUnitAction;
import pruebas.Entities.helpers.MoveUnitAction;
import pruebas.Entities.helpers.PlaceUnitAction;
import pruebas.Entities.helpers.UnitAction;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.UnitRender.STATE;
import pruebas.Renders.helpers.CellHelper;
import pruebas.Renders.helpers.ResourceHelper;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class TurnAnimations extends GameRender {

	private Array<PlaceUnitAction> player1Places;
	private Array<PlaceUnitAction> player2Places;
	private Array<MoveUnitAction> player1Moves;
	private Array<MoveUnitAction> player2Moves;
	private Array<AttackUnitAction> player1MeleeAttacks;
	private Array<AttackUnitAction> player2MeleeAttacks;
	private Array<AttackUnitAction> player1RangedAttacks;
	private Array<AttackUnitAction> player2RangedAttacks;
	private Array<DefendUnitAction> player1Defend;
	private Array<DefendUnitAction> player2Defend;
	private Array<Unit> deadUnits;

	private static TweenManager tweenManager;

	private Image panel;
	private TextButton btnPlay;
	private TextButton btnSkip;
	private Group grpPanel;

	private Texture victoryTexture;
	private Texture defeatTexture;
	private Texture drawTexture;
	private Image gameEndMessage;
	private TextButton btnBackToMenu;

	private Random rand;

	public TurnAnimations(WorldController world) {
		super(world);

		player1Places = new Array<PlaceUnitAction>();
		player2Places = new Array<PlaceUnitAction>();
		player1Moves = new Array<MoveUnitAction>();
		player2Moves = new Array<MoveUnitAction>();
		player1MeleeAttacks = new Array<AttackUnitAction>();
		player2MeleeAttacks = new Array<AttackUnitAction>();
		player1RangedAttacks = new Array<AttackUnitAction>();
		player2RangedAttacks = new Array<AttackUnitAction>();
		player1Defend = new Array<DefendUnitAction>();
		player2Defend = new Array<DefendUnitAction>();
		deadUnits = new Array<Unit>();

		rand = new Random();

		load();

		readActions();
		if (player1Places.size > 0 || player2Places.size > 0)
			setFirstTurnAnimation();

		GameEngine.hideLoading();
	}

	public void load() {
		tweenManager = new TweenManager();
		Tween.registerAccessor(Unit.class, new UnitAccessor());
		
		GameController.getInstance().loadUnitsStats();

		Texture panelTexture = ResourceHelper.getTexture("data/Images/TurnAnimation/games_list_background.png");
		panel = new Image(panelTexture);

		victoryTexture = ResourceHelper.getTexture("data/Images/TurnAnimation/Messages/banner_victory.png");
		defeatTexture = ResourceHelper.getTexture("data/Images/TurnAnimation/Messages/banner_defeat.png");
		drawTexture = ResourceHelper.getTexture("data/Images/TurnAnimation/Messages/banner_draw.png");

		btnBackToMenu = new TextButton("Back to menu", ResourceHelper.getButtonStyle());
		btnBackToMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				world.sendTurn();
			}
		});

		btnPlay = new TextButton("PLAY", ResourceHelper.getOuterButtonStyle());
		btnPlay.setPosition(panel.getWidth() / 2 - btnPlay.getWidth() / 2, panel.getHeight() / 2);
		btnPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				play();
			}
		});

		btnSkip = new TextButton("SKIP", ResourceHelper.getOuterButtonStyle());
		btnSkip.setPosition(panel.getWidth() / 2 - btnSkip.getWidth() / 2, panel.getHeight() / 2);
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				start(pushExitAnimation(Timeline.createParallel()));
			}
		});

		grpPanel = new Group();
		grpPanel.addActor(panel);
		grpPanel.addActor(btnPlay);
		// grpPanel.addActor(btnSkip);

		addActor(grpPanel);
	}

	private void start(Timeline t) {
		t.start(tweenManager);
	}

	private void play() {
		hidePanel();
		defensiveUnits(true);
		placeUnits();
	}

	private void setFirstTurnAnimation() {
		PlaceUnitAction action = null;
		for (int m = 0; m < player1Places.size; m++) {
			action = player1Places.get(m);
			action.origin.getUnit().setPosition(-100, 354 + rand.nextInt(100));
		}

		for (int m = 0; m < player2Places.size; m++) {
			action = player2Places.get(m);
			action.origin.getUnit().setPosition(CrystalClash.WIDTH + 100, 354 + rand.nextInt(100));
		}
	}

	private void defensiveUnits(boolean active) {
		DefendUnitAction action = null;
		for (int i = 0; i < player1Defend.size; i++) {
			action = player1Defend.get(i);
			action.origin.getUnit().setDefendingPosition(active);
		}

		for (int i = 0; i < player2Defend.size; i++) {
			action = player2Defend.get(i);
			action.origin.getUnit().setDefendingPosition(active);
		}
	}

	private void placeUnits() {
		Timeline t = Timeline.createSequence();
		t.beginParallel();
		createPlacingPath(player1Places, 1, t);
		t.end();
		t.beginParallel();
		createPlacingPath(player2Places, 2, t);
		t.end();
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				meleeAttackUnits();
			}
		});
		start(t);
	}

	private void createPlacingPath(Array<PlaceUnitAction> placeActions, int player, Timeline placeTimeline) {
		PlaceUnitAction action = null;
		for (int m = 0; m < placeActions.size; m++) {
			action = placeActions.get(m);

			Timeline move = Timeline.createParallel();
			move.delay(rand.nextFloat())
					.push(Tween
							.to(action.origin.getUnit(), UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
							.target(CellHelper.getUnitX(action.origin)).ease(TweenEquations.easeNone))
					.push(Tween
							.to(action.origin.getUnit(), UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
							.target(CellHelper.getUnitY(action.origin)).ease(TweenEquations.easeNone));
			move.setUserData(new Object[] { action });
			move.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
			move.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					PlaceUnitAction action = (PlaceUnitAction) (((Object[]) source.getUserData())[0]);
					Unit unit = action.origin.getUnit();

					if (type == TweenCallback.COMPLETE) {
						unit.getRender().setState(STATE.idle);
					} else if (type == TweenCallback.BEGIN) {
						unit.getRender().setState(STATE.walking);
					}
				}
			});
			placeTimeline.push(move);
		}
	}

	private void meleeAttackUnits() {
		Timeline t = Timeline.createSequence();
		t.beginParallel();
		createMeleeAttacks(player1MeleeAttacks, 1, t);
		t.end();
		t.beginParallel();
		createMeleeAttacks(player2MeleeAttacks, 2, t);
		t.end();
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				moveUnits();
			}
		});
		start(t);
	}

	private void createMeleeAttacks(Array<AttackUnitAction> attackActions, int player, Timeline attackTimeline) {
		AttackUnitAction action = null;
		for (int m = 0; m < attackActions.size; m++) {
			action = attackActions.get(m);

			Timeline attack = Timeline.createSequence();
			Timeline move = Timeline.createParallel();
			move.delay(rand.nextFloat())
					.push(Tween.to(action.origin.getUnit(), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
							.target(getXForMeeleTarget(player, action.target, action.origin.getUnit())))
					.push(Tween.to(action.origin.getUnit(), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
							.target(getYForMeeleTarget(player, action.target, action.origin.getUnit())));
			move.setUserData(new Object[] { action });
			move.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
			move.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					AttackUnitAction action = (AttackUnitAction) (((Object[]) source.getUserData())[0]);
					Unit unit = action.origin.getUnit();
					if (type == TweenCallback.COMPLETE) {
						Unit enemy = action.target.getUnit();
						doDamage(enemy, unit);
						unit.getRender().setState(STATE.fighting);

					} else if (type == TweenCallback.BEGIN) {
						unit.getRender().setState(STATE.walking);
					}
				}
			});
			attack.push(move);

			Timeline moveBack = Timeline.createParallel();
			moveBack.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
			moveBack.push(Tween
					.to(action.origin.getUnit(), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitX(action.origin)));
			moveBack.push(Tween
					.to(action.origin.getUnit(), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitY(action.origin)));
			moveBack.setUserData(new Object[] { action.origin.getUnit() });
			moveBack.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
			moveBack.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					if (type == TweenCallback.COMPLETE) {
						unit.getRender().setState(STATE.idle);
						if (unit.isPlayerOne())
							unit.getRender().setFacing(FACING.right);
						else
							unit.getRender().setFacing(FACING.left);
					} else {
						unit.getRender().setState(STATE.walking);
					}
				}
			});

			attack.push(moveBack);
			attackTimeline.push(attack);
		}
	}

	private float getXForMeeleTarget(int player, Cell target, Unit attacker) {
		float resultX = CellHelper.getUnitX(target);

		if (attacker.getX() > resultX) {
			resultX += 30 + rand.nextInt(25);
		} else {
			resultX -= 75 + rand.nextInt(25);
		}
		return resultX;
	}

	private float getYForMeeleTarget(int player, Cell target, Unit attacker) {
		float resultY = CellHelper.getUnitY(target);

		if (attacker.getY() > resultY) {
			resultY += 10 + rand.nextInt(30);
		} else {
			resultY -= 10 + rand.nextInt(30);
		}
		return resultY;
	}

	private void moveUnits() {
		Timeline t = Timeline.createSequence();
		t.beginParallel();
		createPaths(player1Moves, 1, t);
		t.end();
		t.beginParallel();
		createPaths(player2Moves, 2, t);
		t.end();
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				rangedAttackUnits();
			}
		});
		start(t);
	}

	private void createPaths(Array<MoveUnitAction> moveActions, int player, Timeline pathsTimeline) {
		MoveUnitAction action = null;
		for (int m = 0; m < moveActions.size; m++) {
			action = moveActions.get(m);

			Timeline path = Timeline.createSequence()
					.delay(rand.nextFloat())
					.setUserData(new Object[] { action.origin, action.moves.get(action.moves.size - 1) })
					.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							Cell cellFrom = (Cell) (((Object[]) source.getUserData())[0]);
							Cell cellTo = (Cell) (((Object[]) source.getUserData())[1]);

							if (type == TweenCallback.COMPLETE)
								repositionUnit(cellFrom, cellTo);
							else {
								Unit unit = cellFrom.getUnit();
								unit.getRender().setState(STATE.walking);
							}
						}
					});

			for (int i = 0; i + 1 < action.moves.size; i++) {
				path.push(createStep(action, i, action.moves.size, player));
			}
			pathsTimeline.push(path);
		}
	}

	private Timeline createStep(MoveUnitAction action, int currentStepIndex, int stepsCount, int player) {
		return Timeline.createParallel()
				.push(Tween
						.to(action.origin.getUnit(), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.ease(Linear.INOUT)
						.target(CellHelper.getUnitX(action.moves.get(currentStepIndex + 1))))
				.push(Tween
						.to(action.origin.getUnit(), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.ease(Linear.INOUT)
						.target(currentStepIndex + 1 == stepsCount - 1 ?
								CellHelper.getUnitY(action.moves.get(currentStepIndex + 1)) :
								rand(CellHelper.getUnitY(action.moves.get(currentStepIndex + 1)))));
	}

	private float rand(float value) {
		return value + rand.nextInt(100) - 50;
	}

	private void repositionUnit(Cell cellFrom, Cell cellTo) {
		Unit unit = cellFrom.getUnit();

		cellFrom.removeUnit();
		cellTo.setUnit(unit);

		unit.getRender().setState(STATE.idle);
		if (unit.isPlayerOne())
			unit.getRender().setFacing(FACING.right);
		else
			unit.getRender().setFacing(FACING.left);

		String coordsFrom = cellFrom.getGridPosition().getX() + ","
				+ cellFrom.getGridPosition().getY();
		String coordsTo = cellTo.getGridPosition().getX() + ","
				+ cellTo.getGridPosition().getY();
		System.out.println("step:[" + coordsFrom + "]-->[" + coordsTo + "]");
	}

	private void rangedAttackUnits() {
		Timeline t = Timeline.createSequence();
		t.beginParallel();
		createRangedAttacks(player1RangedAttacks, 1, t);
		t.end();
		t.beginParallel();
		createRangedAttacks(player2RangedAttacks, 2, t);
		t.end();
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				defensiveUnits(false);
				playDeaths();
			}
		});
		start(t);
	}

	private void createRangedAttacks(Array<AttackUnitAction> attackActions, int player,
			Timeline attackTimeline) {
		AttackUnitAction action = null;
		for (int m = 0; m < attackActions.size; m++) {
			action = attackActions.get(m);

			Timeline startAnim = Timeline.createSequence();
			startAnim.setUserData(new Object[] { action });
			startAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					AttackUnitAction action = (AttackUnitAction) (((Object[]) source.getUserData())[0]);
					Unit unit = action.origin.getUnit();

					unit.getRender().setState(STATE.fighting);
					action.target.state = Cell.ATTACK_TARGET_CENTER;
				}
			});

			Timeline stopAnim = Timeline.createSequence();
			stopAnim.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
			stopAnim.setUserData(new Object[] { action });
			stopAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					AttackUnitAction action = (AttackUnitAction) (((Object[]) source.getUserData())[0]);
					Unit unit = action.origin.getUnit();

					Unit enemy = action.target.getUnit();
					doDamage(enemy, unit);
					unit.getRender().setState(STATE.idle);
					action.target.state = Cell.NONE;
				}
			});

			attackTimeline.push(startAnim);
			attackTimeline.push(stopAnim);
		}
	}

	private boolean doDamage(Unit enemy, Unit player) {
		if (enemy != null) {
			enemy.damage(player.getDamage());

			if (!enemy.isAlive() && !deadUnits.contains(enemy, true)) {
				deadUnits.add(enemy);
				if (enemy.isEnemy())
					world.enemiesCount--;
				else
					world.allysCount--;
			}
			return true;
		}
		return false;
	}

	private void playDeaths() {
		Timeline deathTimeline = Timeline.createParallel();
		if (deadUnits.size > 0) {
			Unit unit = null;
			for (int m = 0; m < deadUnits.size; m++) {
				unit = deadUnits.get(m);
				if (unit.getRender().getState() != STATE.dead)
					unit.getRender().setState(STATE.dieing);
			}
			deathTimeline.delay(6);
		}
		deathTimeline.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				endTurnAnimations();
			}
		});
		start(deathTimeline);
	}

	private void endTurnAnimations() {
		world.removeAllDeadUnits();
		if (world.allysCount == 0 && world.enemiesCount > 0) {
			world.gameEnded = true;
			gameEndMessage = new Image(defeatTexture);
		} else if (world.enemiesCount == 0 && world.allysCount > 0) {
			world.gameEnded = true;
			gameEndMessage = new Image(victoryTexture);
		} else if (world.allysCount == 0 && world.enemiesCount == 0) {
			world.gameEnded = true;
			gameEndMessage = new Image(drawTexture);
		}
		if (world.gameEnded) {
			grpPanel.remove();
			addActor(btnBackToMenu);
			addActor(gameEndMessage);

			gameEndMessage.setPosition(CrystalClash.WIDTH / 2 - gameEndMessage.getWidth() / 2,
					CrystalClash.HEIGHT);
			btnBackToMenu.setPosition(gameEndMessage.getX() + gameEndMessage.getWidth() / 2 - btnBackToMenu.getWidth() / 2,
					gameEndMessage.getY() + gameEndMessage.getHeight() / 2 - btnBackToMenu.getHeight() / 2);
			start(Timeline.createSequence()
					.beginParallel()
					.push(Tween.to(gameEndMessage, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT / 2 - gameEndMessage.getHeight() / 2))
					.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT / 2 - btnBackToMenu.getHeight() / 2))
					.end()
					.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT / 2 - gameEndMessage.getHeight() / 2 - btnBackToMenu.getHeight())
							.ease(TweenEquations.easeOutBounce)));
		} else {
			showPanel();
		}
	}

	private void readActions() {
		for (int row = 0; row < world.cellGrid.length; row++) {
			for (int col = 0; col < world.cellGrid[0].length; col++) {

				UnitAction action = world.cellGrid[row][col].getAction();
				Unit unit = world.cellGrid[row][col].getUnit();

				if (action != null) {
					switch (action.getActionType()) {
					case ATTACK:
						AttackUnitAction aux = (AttackUnitAction) action;
						if (unit.isPlayerOne()) {
							if (aux.meleeAttack) {
								player1MeleeAttacks.add(aux);
							} else {
								player1RangedAttacks.add(aux);
							}
						} else {
							if (aux.meleeAttack) {
								player2MeleeAttacks.add(aux);
							} else {
								player2RangedAttacks.add(aux);
							}
						}
						break;
					case DEFENSE:
						if (unit.isPlayerOne()) {
							player1Defend.add((DefendUnitAction) action);
						} else {
							player2Defend.add((DefendUnitAction) action);
						}
						break;
					case MOVE:
						if (unit.isPlayerOne()) {
							player1Moves.add((MoveUnitAction) action);
						} else {
							player2Moves.add((MoveUnitAction) action);
						}
						break;
					case NONE:
						break;
					case PLACE:
						if (unit.isPlayerOne()) {
							player1Places.add((PlaceUnitAction) action);
						} else {
							player2Places.add((PlaceUnitAction) action);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

	@Override
	public void clearAllChanges() {
		// TODO Auto-generated method stub
	}

	private void hidePanel() {
		start(Timeline.createSequence()
				.push(Tween.to(grpPanel, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED).target(CrystalClash.HEIGHT)));
	}

	private void showPanel() {
		grpPanel.removeActor(btnPlay);
		grpPanel.addActor(btnSkip);

		start(Timeline.createSequence()
				.push(Tween.to(grpPanel, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED).target(0)));
	}

	@Override
	public void renderInTheBack(float dt, SpriteBatch batch) {
		tweenManager.update(dt);
	}

	@Override
	public void renderInTheFront(float dt, SpriteBatch batch) {
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
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
		return t;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		if (world.gameEnded) {
			t.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(CrystalClash.HEIGHT))
					.push(Tween.to(gameEndMessage, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT))
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							world.initNormalTurn();
						}
					});
		} else {
			t.push(Tween.to(grpPanel, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(CrystalClash.HEIGHT))
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							world.initNormalTurn();
						}
					});
		}
		return t;
	}

	@Override
	public boolean canSend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSend() {
		// TODO Auto-generated method stub

	}

	public void pause() {
		tweenManager.pause();
	}

	@Override
	public void resume() {
		tweenManager.resume();
	}

	@Override
	public ClickListener attackListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClickListener defendListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClickListener moveListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClickListener undoListener() {
		// TODO Auto-generated method stub
		return null;
	}
}
