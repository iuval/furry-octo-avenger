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
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
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
	private Array<Cell> deadUnits;

	private Image panel;
	private TextButton btnPlay;
	private TextButton btnSkip;
	private Group grpPanel;

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
		deadUnits = new Array<Cell>();

		init();

		readActions(1);
		readActions(2);
		GameEngine.hideLoading();
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

			Timeline walkAnim = Timeline.createParallel();
			walkAnim.setUserData(new Object[] { action.origin.getUnit(player) });
			walkAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					unit.getRender().setState(STATE.walking);
				}
			});
			attack.push(walkAnim);

			Timeline move = Timeline.createParallel();
			move.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitX(player, action.target)));
			move.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitY(player, action.target)));
			move.setUserData(new Object[] { action, player });
			move.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					AttackUnitAction action = (AttackUnitAction) (((Object[]) source.getUserData())[0]);
					int player = Integer.parseInt(((Object[]) source.getUserData())[1].toString());
					Unit unit = action.origin.getUnit(player);
					Unit enemy = action.target.getUnit(player == 1 ? 2 : 1);
					enemy.damage(unit.getDamage());
					if (!enemy.isAlive()) {
						deadUnits.add(action.target);
					}
					unit.getRender().setState(STATE.fighting);
				}
			});

			attack.push(move);

			Timeline walkAnim2 = Timeline.createParallel();
			walkAnim2.setUserData(new Object[] { action.origin.getUnit(player) });
			walkAnim2.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					unit.getRender().setState(STATE.walking);
				}
			});
			walkAnim2.delay(CrystalClash.FIGTH_ANIMATION_SPEED);
			attack.push(walkAnim2);

			Timeline moveBack = Timeline.createParallel();
			moveBack.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitX(player, action.origin)));
			moveBack.push(Tween
					.to(action.origin.getUnit(player), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
					.target(CellHelper.getUnitY(player, action.origin)));
			moveBack.setUserData(new Object[] { action.origin.getUnit(player), player });
			moveBack.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					int player = (Integer) (((Object[]) source.getUserData())[1]);
					unit.getRender().setState(STATE.idle);
					if (player == 1)
						unit.getRender().setFacing(FACING.right);
					else
						unit.getRender().setFacing(FACING.left);
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

	private void createPaths(Array<MoveUnitAction> moveActions, int player,
			Timeline pathsTimeline) {
		MoveUnitAction action = null;
		for (int m = 0; m < moveActions.size; m++) {
			action = moveActions.get(m);

			Timeline walkAnim = Timeline.createSequence();
			walkAnim.setUserData(new Object[] { action.origin.getUnit(player) });
			walkAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					unit.getRender().setState(STATE.walking);
				}
			});

			Timeline path = Timeline.createSequence();
			path.setUserData(new Object[] { player, action.origin, action.moves.get(action.moves.size - 1) });
			path.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					repositionUnit(source);
				}
			});

			for (int i = 0; i + 1 < action.moves.size; i++) {
				Timeline step = createStep(action, i, action.moves.size, player);
				path.push(step);
			}
			pathsTimeline.push(walkAnim);
			pathsTimeline.push(path);
		}
	}

	private Timeline createStep(MoveUnitAction action, int currentStepIndex, int stepsCount,
			int player) {
		Timeline step = Timeline.createParallel();
		step.push(Tween
				.to(action.origin.getUnit(player), UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitX(player, action.moves.get(currentStepIndex + 1))));
		step.push(Tween
				.to(action.origin.getUnit(player), UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
				.target(CellHelper.getUnitY(player, action.moves.get(currentStepIndex + 1))));
		return step;
	}

	private void playDeaths() {
		Timeline deathTimeline = Timeline.createParallel();
		if (deadUnits.size > 0) {
			Unit unit = null;
			for (int m = 0; m < deadUnits.size; m++) {
				Cell cell = deadUnits.get(m);
				for (int i = 0; i < 2; i++) {
					if (cell.getUnit(i) != null) {
						unit = deadUnits.get(m).getUnit(i);
						if (unit.getRender().getState() != STATE.dead)
							unit.getRender().setState(STATE.dieing);
					}
				}
			}
			deathTimeline.delay(6);
		}
		deathTimeline.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				for (int i = 0; i < deadUnits.size; i++) {
					deadUnits.get(i).removeDeadUnits();
				}
				showPanel();
			}
		});
		deathTimeline.start(tweenManager);
	}

	private void repositionUnit(BaseTween<?> source) {
		int player = (Integer) (((Object[]) source.getUserData())[0]);
		Cell cellFrom = (Cell) (((Object[]) source.getUserData())[1]);
		Cell cellTo = (Cell) (((Object[]) source.getUserData())[2]);
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

			Timeline attackAnim = Timeline.createSequence();
			attackAnim.setUserData(new Object[] { action.origin.getUnit(player) });
			attackAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					unit.getRender().setState(STATE.fighting);
				}
			});

			Timeline stopAnim = Timeline.createSequence();
			stopAnim.setUserData(new Object[] { action.origin.getUnit(player), action.target.getUnit(player == 1 ? 2 : 1) });
			stopAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit unit = (Unit) (((Object[]) source.getUserData())[0]);
					Unit enemy = (Unit) (((Object[]) source.getUserData())[1]);
					if (enemy != null)
						enemy.damage(unit.getDamage());

					unit.getRender().setState(STATE.idle);
				}
			});
			stopAnim.delay(CrystalClash.FIGTH_ANIMATION_SPEED);

			attackTimeline.push(attackAnim);
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

	public void init() {
		GameController.getInstancia().loadUnitsStats();
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		TextureAtlas atlas = new TextureAtlas("data/Images/Buttons/buttons.pack");
		Skin skin = new Skin(atlas);

		Texture panelTexture = new Texture(Gdx.files.internal("data/Images/TurnAnimation/games_list_background.png"));
		panel = new Image(panelTexture);

		BitmapFont font = new BitmapFont(Gdx.files.internal("data/Fonts/font.fnt"), false);
		TextButtonStyle playStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_orange"),
				skin.getDrawable("outer_button_orange_pressed"), null, font);
		btnPlay = new TextButton("PLAY", playStyle);
		btnPlay.setPosition(panel.getWidth() / 2 - btnPlay.getWidth() / 2, panel.getHeight() / 2);
		btnPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				play();
			}
		});

		TextButtonStyle skipStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_orange"),
				skin.getDrawable("outer_button_orange_pressed"), null, font);
		btnSkip = new TextButton("SKIP", skipStyle);
		btnSkip.setPosition(panel.getWidth() / 2 - btnSkip.getWidth() / 2, panel.getHeight() / 2);
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				extiAnimation();
			}
		});

		grpPanel = new Group();
		grpPanel.addActor(panel);
		grpPanel.addActor(btnPlay);
		// grpPanel.addActor(btnSkip);
	}

	@Override
	public void clearAllChanges() {
		// TODO Auto-generated method stub
	}

	private void extiAnimation() {
		float speed = 0.5f; // CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(grpPanel, ActorAccessor.ALPHA, speed).target(0))
				.push(Tween.to(grpPanel, ActorAccessor.Y, speed).target(CrystalClash.HEIGHT))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						world.initNormalTurn();
					}
				}).start(tweenManager);
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

		stage.addActor(grpPanel);
		grpPanel.act(dt);
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
		// TODO Auto-generated method stub
		return null;
	}
}
