package pruebas.Renders;

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
import pruebas.Entities.helpers.UnitAction;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.UnitRender.STATE;
import pruebas.Renders.helpers.CellHelper;
import pruebas.Renders.helpers.UIHelper;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class TurnAnimations extends GameRender {

	private TweenManager tweenManager;

	private Array<MoveUnitAction> player1Moves;
	private Array<MoveUnitAction> player2Moves;
	private Array<AttackUnitAction> player1MeleeAttacks;
	private Array<AttackUnitAction> player2MeleeAttacks;
	private Array<AttackUnitAction> player1RangedAttacks;
	private Array<AttackUnitAction> player2RangedAttacks;
	private Array<DefendUnitAction> player1Defend;
	private Array<DefendUnitAction> player2Defend;
	private Array<Unit> deadUnits;

	private Image panel;
	private TextButton btnPlay;
	private TextButton btnSkip;
	private Group grpPanel;

	private Texture victoryTexture;
	private Texture defeatTexture;
	private Texture drawTexture;
	private Image gameEndMessage;
	private TextButton btnBackToMenu;

	public TurnAnimations(WorldController world) {
		super(world);

		tweenManager = new TweenManager();

		player1Moves = new Array<MoveUnitAction>();
		player2Moves = new Array<MoveUnitAction>();
		player1MeleeAttacks = new Array<AttackUnitAction>();
		player2MeleeAttacks = new Array<AttackUnitAction>();
		player1RangedAttacks = new Array<AttackUnitAction>();
		player2RangedAttacks = new Array<AttackUnitAction>();
		player1Defend = new Array<DefendUnitAction>();
		player2Defend = new Array<DefendUnitAction>();
		deadUnits = new Array<Unit>();

		init();

		readActions(1);
		readActions(2);
		GameEngine.hideLoading();
	}

	public void init() {
		GameController.getInstancia().loadUnitsStats();
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		Texture panelTexture = new Texture(Gdx.files.internal("data/Images/TurnAnimation/games_list_background.png"));
		panel = new Image(panelTexture);

		victoryTexture = new Texture(Gdx.files.internal("data/Images/TurnAnimation/Messages/victory.png"));
		defeatTexture = new Texture(Gdx.files.internal("data/Images/TurnAnimation/Messages/defeat.png"));
		drawTexture = new Texture(Gdx.files.internal("data/Images/TurnAnimation/Messages/defeat.png"));

		btnBackToMenu = new TextButton("Back to menu", UIHelper.getButtonStyle());
		btnBackToMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				world.leaveGame();
			}
		});

		btnPlay = new TextButton("PLAY", UIHelper.getOuterButtonStyle());
		btnPlay.setPosition(panel.getWidth() / 2 - btnPlay.getWidth() / 2, panel.getHeight() / 2);
		btnPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				play();
			}
		});

		btnSkip = new TextButton("SKIP", UIHelper.getOuterButtonStyle());
		btnSkip.setPosition(panel.getWidth() / 2 - btnSkip.getWidth() / 2, panel.getHeight() / 2);
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				pushExitAnimation(Timeline.createParallel()).start(tweenManager);
			}
		});

		grpPanel = new Group();
		grpPanel.addActor(panel);
		grpPanel.addActor(btnPlay);
		// grpPanel.addActor(btnSkip);
	}

	private void play() {
		hidePanel();
		defensiveUnits(true);
		meleeAttackUnits();
	}

	private void defensiveUnits(boolean active) {
		DefendUnitAction action = null;
		for (int i = 0; i < player1Defend.size; i++) {
			action = player1Defend.get(i);
			action.origin.getUnit(1).setDefendingPosition(active);
		}

		for (int i = 0; i < player2Defend.size; i++) {
			action = player2Defend.get(i);
			action.origin.getUnit(2).setDefendingPosition(active);
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
		}).start(tweenManager);
	}

	private void createMeleeAttacks(Array<AttackUnitAction> attackActions, int player, Timeline attackTimeline) {
		AttackUnitAction action = null;
		for (int m = 0; m < attackActions.size; m++) {
			action = attackActions.get(m);

			Timeline attack = Timeline.createSequence();

			Timeline move = Timeline.createParallel();
			move.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitX(player, action.target)));
			move.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitY(player, action.target)));
			move.setUserData(new Object[] { action, player });
			move.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
			move.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					AttackUnitAction action = (AttackUnitAction) (((Object[]) source.getUserData())[0]);
					int player = Integer.parseInt(((Object[]) source.getUserData())[1].toString());
					Unit unit = action.origin.getUnit(player);
					System.out.println(TweenCallback.BEGIN + " -> " + type);
					if (type == TweenCallback.COMPLETE) {
						Unit enemy = action.target.getUnit(player == 1 ? 2 : 1);
						if (enemy != null) {
							enemy.damage(unit.getDamage());

							if (!enemy.isAlive()) {
								deadUnits.add(enemy);
								if (player == world.player)
									world.enemiesCount--;
								else
									world.allysCount--;
							}
							unit.getRender().setState(STATE.fighting);
						}
					} else {
						unit.getRender().setState(STATE.walking);
					}
				}
			});
			attack.push(move);

			Timeline moveBack = Timeline.createParallel();
			moveBack.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
			moveBack.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitX(player, action.origin)));
			moveBack.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitY(player, action.origin)));
			moveBack.setUserData(new Object[] { action.origin.getUnit(player), player });
			moveBack.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
			moveBack.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					if (type == TweenCallback.COMPLETE) {
						int player = (Integer) (((Object[]) source.getUserData())[1]);
						unit.getRender().setState(STATE.idle);
						if (player == 1)
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
		}).start(tweenManager);
	}

	private void createPaths(Array<MoveUnitAction> moveActions, int player, Timeline pathsTimeline) {
		MoveUnitAction action = null;
		for (int m = 0; m < moveActions.size; m++) {
			action = moveActions.get(m);

			Timeline path = Timeline.createSequence();
			path.setUserData(new Object[] { player, action.origin, action.moves.get(action.moves.size - 1) });
			path.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
			path.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					int player = (Integer) (((Object[]) source.getUserData())[0]);
					Cell cellFrom = (Cell) (((Object[]) source.getUserData())[1]);
					Cell cellTo = (Cell) (((Object[]) source.getUserData())[2]);

					if (type == TweenCallback.COMPLETE)
						repositionUnit(player, cellFrom, cellTo);
					else {
						Unit unit = cellFrom.getUnit(player);
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
						.to(action.origin.getUnit(player), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.ease(Linear.INOUT)
						.target(CellHelper.getUnitX(player, action.moves.get(currentStepIndex + 1))))
				.push(Tween
						.to(action.origin.getUnit(player), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.ease(Linear.INOUT)
						.target(CellHelper.getUnitY(player, action.moves.get(currentStepIndex + 1))));
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
		deathTimeline.start(tweenManager);
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
			gameEndMessage.setPosition(CrystalClash.WIDTH / 2 - gameEndMessage.getWidth() / 2,
					CrystalClash.HEIGHT);
			btnBackToMenu.setPosition(gameEndMessage.getX() + gameEndMessage.getWidth() / 2 - btnBackToMenu.getWidth() / 2,
					gameEndMessage.getY() + gameEndMessage.getHeight() / 2 - btnBackToMenu.getHeight() / 2);
			Timeline.createSequence()
					.beginParallel()
					.push(Tween.to(gameEndMessage, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT / 2 - gameEndMessage.getHeight() / 2))
					.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT / 2 - btnBackToMenu.getHeight() / 2))
					.end()
					.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT / 2 - gameEndMessage.getHeight() / 2 - btnBackToMenu.getHeight())
							.ease(TweenEquations.easeOutBounce))
					.start(tweenManager);
		} else {
			showPanel();
		}
	}

	private void repositionUnit(int player, Cell cellFrom, Cell cellTo) {
		Unit unit = cellFrom.getUnit(player);

		cellFrom.removeUnit(player);
		cellTo.setUnit(unit, player);

		unit.getRender().setState(STATE.idle);
		if (player == 1)
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
		}).start(tweenManager);
	}

	private void createRangedAttacks(Array<AttackUnitAction> attackActions, int player,
			Timeline attackTimeline) {
		AttackUnitAction action = null;
		for (int m = 0; m < attackActions.size; m++) {
			action = attackActions.get(m);
			action.origin.getUnit(player).getRender().setState(STATE.fighting);

			Timeline stopAnim = Timeline.createSequence();
			stopAnim.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
			stopAnim.setUserData(new Object[] { action, player });
			stopAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					AttackUnitAction action = (AttackUnitAction) (((Object[]) source.getUserData())[0]);
					int player = Integer.parseInt(((Object[]) source.getUserData())[1].toString());
					Unit unit = action.origin.getUnit(player);

					Unit enemy = action.target.getUnit(player == 1 ? 2 : 1);
					if (enemy != null) {
						enemy.damage(unit.getDamage());

						if (!enemy.isAlive()) {
							deadUnits.add(enemy);
							if (player == world.player)
								world.enemiesCount--;
							else
								world.allysCount--;
						}
					}
					unit.getRender().setState(STATE.idle);
				}
			});

			attackTimeline.push(stopAnim);
		}
	}

	private void readActions(int player) {
		for (int row = 0; row < world.cellGrid.length; row++) {
			for (int col = 0; col < world.cellGrid[0].length; col++) {

				UnitAction action = world.cellGrid[row][col].getAction(player);

				if (action != null) {
					switch (action.getActionType()) {
					case ATTACK:
						AttackUnitAction aux = (AttackUnitAction) action;
						if (player == 1) {
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
						if (player == 1) {
							player1Defend.add((DefendUnitAction) action);
						} else {
							player2Defend.add((DefendUnitAction) action);
						}
						break;
					case MOVE:
						if (player == 1) {
							player1Moves.add((MoveUnitAction) action);
						} else {
							player2Moves.add((MoveUnitAction) action);
						}
						break;
					case NONE:
						break;
					case PLACE:
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
		float speed = 0.5f; // CrystalClash.ANIMATION_SPEED;
		Timeline.createSequence()
				.push(Tween.to(grpPanel, ActorAccessor.Y, speed).target(CrystalClash.HEIGHT))
				.start(tweenManager);
	}

	private void showPanel() {
		grpPanel.removeActor(btnPlay);
		grpPanel.addActor(btnSkip);

		float speed = 0.5f; // CrystalClash.ANIMATION_SPEED;
		Timeline.createSequence()
				.push(Tween.to(grpPanel, ActorAccessor.Y, speed).target(0))
				.start(tweenManager);
	}

	@Override
	public void render(float dt, SpriteBatch batch, Stage stage) {
		tweenManager.update(dt);
		if (world.gameEnded) {
			stage.addActor(btnBackToMenu);
			stage.addActor(gameEndMessage);
		} else {
			stage.addActor(grpPanel);
		}
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		if (world.gameEnded) {
			t.push(Tween.to(btnBackToMenu, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
					.target(CrystalClash.HEIGHT))
					.push(Tween.to(gameEndMessage, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
							.target(CrystalClash.HEIGHT))
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							world.initNormalTurn();
						}
					});
		} else {
			t.push(Tween.to(grpPanel, ActorAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
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
}
