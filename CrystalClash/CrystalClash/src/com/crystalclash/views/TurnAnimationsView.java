package com.crystalclash.views;

import java.util.Random;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.accessors.UnitAccessor;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.MUSIC;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;
import com.crystalclash.entities.helpers.AttackUnitAction;
import com.crystalclash.entities.helpers.DefendUnitAction;
import com.crystalclash.entities.helpers.MoveUnitAction;
import com.crystalclash.entities.helpers.NoneUnitAction;
import com.crystalclash.entities.helpers.PlaceUnitAction;
import com.crystalclash.entities.helpers.UnitAction;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.UnitRender.STATE;
import com.crystalclash.renders.helpers.CellHelper;
import com.crystalclash.renders.helpers.ResourceHelper;

public class TurnAnimationsView extends GameView {

	private Array<Unit> units;
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

	private static TweenManager tweenManager;

	private Image panel;
	private TextButton btnPlay;
	private TextButton btnSkip;
	private Group grpPanel;

	private TextureRegion victoryTexture;
	private TextureRegion defeatTexture;
	private TextureRegion drawTexture;
	private Image gameEndMessage;
	private TextButton btnBackToMenu;

	private Random rand;
	Timeline mainTimeline = Timeline.createSequence();

	public TurnAnimationsView(WorldController world) {
		super(world);

		units = new Array<Unit>();
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

		rand = new Random();

		load();

		readActions();
		if (player1Places.size > 0 || player2Places.size > 0)
			setFirstTurnAnimation();

		GameEngine.hideLoading();
	}

	public void load() {
		tweenManager = new TweenManager();

		GameController.loadUnitsStats();

		TextureRegion panelTexture = ResourceHelper.getTexture("turn_animation/games_list_background");
		panel = new Image(panelTexture);

		victoryTexture = ResourceHelper.getTexture("turn_animation/messages/banner_victory");
		defeatTexture = ResourceHelper.getTexture("turn_animation/messages/banner_defeat");
		drawTexture = ResourceHelper.getTexture("turn_animation/messages/banner_draw");

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
	}

	private void start(Timeline t) {
		t.start(tweenManager);
	}

	private void play() {
		hidePanel();
		start(mainTimeline);
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

	private void pushUnitStats(Timeline t) {
		Unit u;
		for (int i = 0; i < units.size; i++) {
			u = units.get(i);
			t.push(Tween.set(u, UnitAccessor.HP).target(u.getHP()));
		}
	}

	private void pushDefensiveShileds(Timeline t, boolean active) {
		Timeline timeline = Timeline.createParallel();
		timeline.setUserData(active);
		timeline.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				boolean active = Boolean.valueOf(source.getUserData().toString());
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
		});
		t.push(timeline);
	}

	private Timeline pushPlaceUnits(Timeline t) {
		t.beginSequence();
		if (world.gameTurn % 2 == 0) {
			t.beginParallel();
			createPlacingPath(player1Places, t);
			t.end();
			t.beginParallel();
			createPlacingPath(player2Places, t);
			t.end();
		} else {
			t.beginParallel();
			createPlacingPath(player2Places, t);
			t.end();
			t.beginParallel();
			createPlacingPath(player1Places, t);
			t.end();
		}
		t.end();
		return t;
	}

	private void createPlacingPath(Array<PlaceUnitAction> placeActions, Timeline placeTimeline) {
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

	private Timeline pushMeleeAttackUnits(Timeline t) {
		t.beginSequence();
		if (world.gameTurn % 2 == 0) {
			t.beginParallel();
			createMeleeAttacks(player1MeleeAttacks, 1, t);
			t.end();
			t.beginParallel();
			createMeleeAttacks(player2MeleeAttacks, 2, t);
			t.end();
		} else {
			t.beginParallel();
			createMeleeAttacks(player2MeleeAttacks, 2, t);
			t.end();
			t.beginParallel();
			createMeleeAttacks(player1MeleeAttacks, 1, t);
			t.end();
		}
		t.end();
		return t;
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

						if (action.origin.getUnit().getX() > enemy.getX()) {
							unit.getRender().setFacing(FACING.left);
						} else {
							unit.getRender().setFacing(FACING.right);
						}

						doDamage(enemy, unit);
						unit.getRender().setState(STATE.fighting);

					} else if (type == TweenCallback.BEGIN) {
						unit.getRender().setState(STATE.walking);
					}
				}
			});
			attack.push(move);

			doSoftDamage(action.target.getUnit(), action.origin.getUnit());

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

	private Timeline pushMoveUnits(Timeline t) {
		t.beginSequence();
		final Timeline fallbackPathsTimeline = Timeline.createParallel();

		if (world.gameTurn % 2 == 0) {
			t.beginParallel();
			createPaths(player1Moves, null, t, fallbackPathsTimeline);
			t.end();
			t.beginParallel();
			createPaths(player2Moves, player1Moves, t, fallbackPathsTimeline);
			t.end();
		} else {
			t.beginParallel();
			createPaths(player2Moves, null, t, fallbackPathsTimeline);
			t.end();
			t.beginParallel();
			createPaths(player1Moves, player2Moves, t, fallbackPathsTimeline);
			t.end();
		}
		t.push(fallbackPathsTimeline);
		t.end();
		return t;
	}

	private void createPaths(Array<MoveUnitAction> allyMoveActions, Array<MoveUnitAction> enemyMoveActions, Timeline pathsTimeline,
			Timeline fallbackPathsTimeline) {
		MoveUnitAction action = null;
		Cell cellToAlly;
		Cell cellToEnemy;
		Cell cellFrom;
		boolean conflict = false;
		TweenCallback walkCallback = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				Cell cellFrom = (Cell) (((Object[]) source.getUserData())[0]);
				Unit unit = cellFrom.getUnit();
				if (type == TweenCallback.COMPLETE) {
					Cell cellTo = (Cell) (((Object[]) source.getUserData())[1]);
					repositionUnit(cellFrom, cellTo);
					unit.getRender().setState(STATE.idle);
				} else {
					unit.getRender().setState(STATE.walking);
				}
			}
		};
		for (int m = 0; m < allyMoveActions.size; m++) {
			conflict = false;
			action = allyMoveActions.get(m);
			Timeline walkTimeline = Timeline.createSequence()
					.setUserData(new Object[] { action.origin, action.moves.get(action.moves.size - 1) });
			for (int i = 1; i < action.moves.size; i++) {
				pushUnitStep(action.origin.getUnit(), action.moves.get(i), walkTimeline);
			}
			walkTimeline.setCallback(walkCallback);
			pathsTimeline.push(walkTimeline);

			cellToAlly = action.moves.get(action.moves.size - 1);
			if (enemyMoveActions != null && enemyMoveActions.size > 0) {
				MoveUnitAction enemyAction;
				for (int e = 0; !conflict && e < enemyMoveActions.size; e++) {
					enemyAction = enemyMoveActions.get(e);
					cellToEnemy = enemyAction.moves.get(enemyAction.moves.size - 1);
					if (cellToEnemy == cellToAlly) {
						conflict = true;
						cellFrom = action.origin;

						MoveUnitAction allyActions = ((MoveUnitAction) cellFrom.getAction());
						Timeline walkBackPath = Timeline.createSequence()
								.setUserData(new Object[] { action.origin, action.moves.get(action.moves.size - 1) })
								.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE)
								.setCallback(walkCallback);
						int j = allyActions.moves.size - 2;
						do {
							cellToAlly = allyActions.moves.get(j);
							pushUnitStep(allyActions.origin.getUnit(), cellToAlly, walkBackPath);
						} while (--j >= 0 && cellToAlly.getUnit() != null);

						walkBackPath.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
						walkBackPath.setUserData(new Object[] { cellFrom, cellToAlly });
						fallbackPathsTimeline.push(walkBackPath);
					}
				}
			}
			if (conflict) {
				walkTimeline.setCallbackTriggers(TweenCallback.BEGIN);
			} else {
				walkTimeline.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.COMPLETE);
			}
		}
	}

	private Timeline pushUnitStep(Unit unit, Cell targetCell, Timeline t) {
		return t.beginParallel()
				.push(Tween
						.to(unit, UnitAccessor.X, CrystalClash.WALK_ANIMATION_SPEED)
						.ease(Linear.INOUT)
						.target(CellHelper.getUnitX(targetCell)))
				.push(Tween
						.to(unit, UnitAccessor.Y, CrystalClash.WALK_ANIMATION_SPEED)
						.ease(Linear.INOUT)
						.target(CellHelper.getUnitY(targetCell)))
				.end();
	}

	private void repositionUnit(Cell cellFrom, Cell cellTo) {
		Unit unit = cellFrom.getUnit();

		cellFrom.removeUnit();
		cellTo.setUnit(unit);

		cellTo.setAction(cellFrom.getAction());
		cellFrom.setAction(new NoneUnitAction());

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

	private Timeline pushRangedAttackUnits(Timeline t) {
		t.beginSequence();
		if (world.gameTurn % 2 == 0) {
			t.beginParallel();
			createRangedAttacks(player1RangedAttacks, t);
			t.end();
			t.beginParallel();
			createRangedAttacks(player2RangedAttacks, t);
			t.end();
		} else {
			t.beginParallel();
			createRangedAttacks(player2RangedAttacks, t);
			t.end();
			t.beginParallel();
			createRangedAttacks(player1RangedAttacks, t);
			t.end();
		}
		t.end();
		return t;
	}

	private void createRangedAttacks(Array<AttackUnitAction> attackActions, Timeline attackTimeline) {
		AttackUnitAction action = null;
		for (int m = 0; m < attackActions.size; m++) {
			action = attackActions.get(m);

			Timeline startAnim = Timeline.createSequence();
			startAnim.setCallbackTriggers(TweenCallback.BEGIN);
			startAnim.setUserData(new Object[] { action });
			startAnim.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					AttackUnitAction action = (AttackUnitAction) ((Object[]) source.getUserData())[0];
					Unit unit = action.origin.getUnit();

					unit.getRender().setState(STATE.fighting);
					if (action.target.getCenterX() < unit.getX()) {
						unit.getRender().setFacing(FACING.left);
					} else {
						unit.getRender().setFacing(FACING.right);
					}
					action.target.state = Cell.ATTACK_TARGET_CENTER;
				}
			});

			// Unit unit = action.origin.getUnit();

			// // If the unit is ranged
			// if (GameController.getUnitTypeIndex(unit.getName()) ==
			// Unit.TYPE_RANGED) {
			// // Calculate the arrow's path
			// Path arrowPath = new Path();
			// PathManager.addArc(arrowPath,
			// action.origin.getCenterX(), action.origin.getCenterY() + 30,
			// action.target.getCenterX(), action.target.getCenterY() + 30);
			//
			// float speed = CrystalClash.FIGTH_ANIMATION_SPEED /
			// arrowPath.dots.size;
			//
			// Image arrow = new
			// Image(ResourceHelper.getUnitResourceTexture(unit.getName(),
			// "arrow"));
			// arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
			// addActor(arrow);
			//
			// Vector2 first = arrowPath.dots.get(0).cpy();
			// Vector2 second = arrowPath.dots.get(1).cpy();
			// float angleOrigin = second.sub(first).cpy().angle();
			//
			// startAnim.push(Tween.set(arrow, ActorAccessor.ALPHA)
			// .target(0))
			// .push(Tween.set(arrow, ActorAccessor.X)
			// .target(CellHelper.getCenterX(action.origin)))
			// .push(Tween.set(arrow, ActorAccessor.Y)
			// .target(CellHelper.getCenterY(action.origin)))
			// .push(Tween.set(arrow, ActorAccessor.ROTATION)
			// .target(angleOrigin));
			//
			// Vector2 prelast = arrowPath.dots.get(arrowPath.dots.size -
			// 2).cpy();
			// Vector2 last = arrowPath.dots.get(arrowPath.dots.size - 1).cpy();
			// float angleTarget = last.sub(prelast).angle();
			//
			// Timeline arrowTimeline = Timeline.createParallel();
			// arrowTimeline.delay(unit.getRender().fightAnim.getAnimationTime());
			//
			// // Arrow rotation
			// if (arrowPath.dots.get(0).x <
			// arrowPath.dots.get(arrowPath.dots.size - 1).x) {
			// arrowTimeline.beginSequence();
			// arrowTimeline.push(Tween.to(arrow, ActorAccessor.ROTATION,
			// CrystalClash.FIGTH_ANIMATION_SPEED / 2)
			// .target(0)
			// .ease(TweenEquations.easeNone));
			// arrowTimeline.push(Tween.set(arrow, ActorAccessor.ROTATION)
			// .target(360));
			// arrowTimeline.push(Tween.to(arrow, ActorAccessor.ROTATION,
			// CrystalClash.FIGTH_ANIMATION_SPEED / 2)
			// .target(angleTarget)
			// .ease(TweenEquations.easeNone));
			// arrowTimeline.end();
			// } else {
			// arrowTimeline.push(Tween.to(arrow, ActorAccessor.ROTATION,
			// CrystalClash.FIGTH_ANIMATION_SPEED)
			// .target(angleTarget)
			// .ease(TweenEquations.easeNone));
			// }
			//
			// // Arrow alpha
			// arrowTimeline.push(Tween.to(arrow, ActorAccessor.ALPHA,
			// CrystalClash.FAST_ANIMATION_SPEED)
			// .target(1)
			// .ease(TweenEquations.easeNone));
			//
			// // Arrow movement
			// arrowTimeline.beginSequence();
			// for (int i = 0; i < arrowPath.dots.size; i++) {
			// arrowTimeline.beginParallel();
			// Vector2 v = arrowPath.dots.get(i);
			// arrowTimeline.push(Tween.to(arrow, ActorAccessor.X, speed)
			// .target(v.x)
			// .ease(TweenEquations.easeNone));
			// arrowTimeline.push(Tween.to(arrow, ActorAccessor.Y, speed)
			// .target(v.y)
			// .ease(TweenEquations.easeNone));
			// arrowTimeline.end();
			// }
			// arrowTimeline.end();
			//
			// arrowTimeline.setUserData(new Object[] { arrow });
			// arrowTimeline.setCallback(new TweenCallback() {
			// @Override
			// public void onEvent(int type, BaseTween<?> source) {
			// Image arrow = (Image) ((Object[]) source.getUserData())[0];
			// arrow.remove();
			// }
			// });
			// startAnim.push(arrowTimeline);
			// }

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

					if (unit.isPlayerOne()) {
						unit.getRender().setFacing(FACING.right);
					} else {
						unit.getRender().setFacing(FACING.left);
					}
				}
			});

			doSoftDamage(action.target.getUnit(), action.origin.getUnit());

			attackTimeline.push(startAnim);
			attackTimeline.push(stopAnim);
		}
	}

	private boolean doDamage(Unit enemy, Unit player) {
		if (enemy != null && enemy.isAlive()) {
			enemy.damage(player.getDamage());

			if (!enemy.isAlive()) {
				if (enemy.isEnemy())
					world.enemiesCount--;
				else
					world.allysCount--;
			}
			return true;
		}
		return false;
	}

	private void doSoftDamage(Unit enemy, Unit player) {
		if (enemy != null && enemy.isAlive()) {
			enemy.softDamage(player.getDamage());
		}
	}

	private void pushDeaths(Timeline t) {
		boolean anyDead = false;
		for (int i = 0; !anyDead && i < units.size; i++) {
			anyDead = units.get(i).getHP() <= 0;
		}
		if (anyDead) {
			Timeline timeline = Timeline.createParallel();
			timeline.delay(6);
			t.push(Tween.call(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Unit u;
					for (int i = 0; i < units.size; i++) {
						u = units.get(i);
						if (u.getHP() <= 0 && u.getRender().getState() != STATE.dead) {
							u.getRender().setState(STATE.dieing);
						}

					}
				}
			}));
			t.push(timeline);
		}
	}

	private void endTurnAnimations() {
		world.removeAllDeadUnits();
		if (world.allysCount == 0 && world.enemiesCount > 0) {
			world.gameEnded = true;
			gameEndMessage = new Image(defeatTexture);
			AudioManager.playSound("defeat");
		} else if (world.enemiesCount == 0 && world.allysCount > 0) {
			world.gameEnded = true;
			gameEndMessage = new Image(victoryTexture);
			AudioManager.playSound("victory");
		} else if (world.allysCount == 0 && world.enemiesCount == 0) {
			world.gameEnded = true;
			gameEndMessage = new Image(drawTexture);
			AudioManager.playSound("draw");
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
					units.add(unit);
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

		addActor(grpPanel);

		pushUnitStats(mainTimeline);
		pushDefensiveShileds(mainTimeline, true);
		pushPlaceUnits(mainTimeline);
		pushMeleeAttackUnits(mainTimeline);
		pushMoveUnits(mainTimeline);
		pushRangedAttackUnits(mainTimeline);
		pushDefensiveShileds(mainTimeline, false);
		pushDeaths(mainTimeline);

		mainTimeline.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				endTurnAnimations();
			}
		});
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
		AudioManager.playMusic(MUSIC.animations);
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
	}

	@Override
	public void onDefendAction() {
	}

	@Override
	public void onMoveAction() {
	}

	@Override
	public void onUndoAction() {
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