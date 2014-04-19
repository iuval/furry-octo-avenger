package com.crystalclash.renders.attacks;

import java.util.Random;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Path;
import com.crystalclash.entities.Unit;
import com.crystalclash.entities.helpers.AttackUnitAction;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.helpers.PathManager;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.UnitHelper;
import com.crystalclash.renders.helpers.ui.SuperAnimatedActor;
import com.crystalclash.renders.particle_system.ParticleSystem;
import com.crystalclash.renders.particle_system.particles.JumpingNumber;

public class AttackFactory {
	private WorldController world;
	private ParticleSystem numbers;
	private static float ARROW_MOVEMENT_SPEED = 0.150f;
	private Random rand;

	public AttackFactory(WorldController controller, ParticleSystem particles) {
		world = controller;
		this.numbers = particles;
		rand = new Random();
	}

	public void pushAttack(Timeline t, AttackUnitAction action, Group entities) {
		String unitName = action.origin.getUnit().getName();
		if (unitName.equals("fire_archer"))
			pushFireArcherAttack(t, action, entities);
		if (unitName.equals("darkness_mage"))
			pushDarkessMageAttack(t, action, entities);
	}

	private void addJumpingNumber(Unit unit, float damage) {
		float vx = 1f + rand.nextFloat();
		if (rand.nextBoolean())
			vx *= -1;
		numbers.addParticle(new JumpingNumber(Float.toString(damage),
				unit.getX(),
				unit.getY() + UnitHelper.HEIGHT,
				vx, 2f + rand.nextFloat(), 1f));
	}

	public void doSoftRangedDamage(AttackUnitAction action) {
		String unitName = action.origin.getUnit().getName();
		if (unitName.equals("fire_archer"))
			doFireArcherSoftDamage(action);
		if (unitName.equals("darkness_mage"))
			doDarkessMageSoftDamage(action);
	}

	public void doSoftMeleeDamage(Unit enemy, Unit player) {
		if (enemy != null && enemy.isAlive()) {
			enemy.softDamage(player.getDamage());
		}
	}

	public void doMeleeDamage(Unit enemy, Unit player) {
		damage(enemy, player.getDamage());
	}

	public void doRangedDamage(AttackUnitAction action) {
		String unitName = action.origin.getUnit().getName();
		if (unitName.equals("fire_archer"))
			doFireArcherDamage(action);
		if (unitName.equals("darkness_mage"))
			doDarkessMageDamage(action);
	}

	private boolean damage(Unit unit, float damage) {
		if (unit != null && unit.isAlive()) {
			addJumpingNumber(unit, unit.damage(damage));
			if (!unit.isAlive()) {
				if (unit.isEnemy())
					world.enemiesCount--;
				else
					world.allysCount--;
			}
			return true;
		}
		return false;
	}

	private void softDamage(Unit unit, float damage) {
		if (unit != null && unit.isAlive()) {
			unit.softDamage(damage);
		}
	}

	// FireArcher
	private void doFireArcherSoftDamage(AttackUnitAction action) {
		Unit targetUnit = action.target.getUnit();
		Unit ally = action.origin.getUnit();
		if (targetUnit != null) {
			System.out.println("Movio: " + targetUnit.getName() + " a:[" + action.target.getGridPosition().getX() + ", "
					+ action.target.getGridPosition().getY() + "]");
		}
		softDamage(targetUnit, ally.getDamage());
	}

	private boolean doFireArcherDamage(AttackUnitAction action) {
		Unit targetUnit = action.target.getUnit();
		Unit ally = action.origin.getUnit();

		return damage(targetUnit, ally.getDamage());
	}

	private void pushFireArcherAttack(Timeline t, final AttackUnitAction action, Group entities) {
		Unit unit = action.origin.getUnit();

		Image arrow = new Image(ResourceHelper.getUnitResourceTexture(unit.getName(), "projectile"));
		arrow.setPosition(0 - arrow.getWidth() * 2, 0 - arrow.getHeight() * 2);
		arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
		entities.addActor(arrow);

		// Calculate the arrow's path
		Path arrowPath = new Path();
		PathManager.addArc(arrowPath,
				action.origin.getCenterX(), action.origin.getCenterY() + 30,
				action.target.getCenterX(), action.target.getCenterY() + 30,
				arrow.getWidth() / 2,
				arrow.getHeight() / 2);
		if (arrowPath.dots.size > 1) {

			// float speed = CrystalClash.FIGTH_ANIMATION_SPEED /
			// arrowPath.dots.size;
			float speed = ARROW_MOVEMENT_SPEED;

			Vector2 first = arrowPath.dots.get(0).cpy();
			Vector2 second = arrowPath.dots.get(1).cpy();
			float angleOrigin = second.sub(first).cpy().angle();

			t.push(Tween.set(arrow, ActorAccessor.ALPHA).target(0))
					.push(Tween.set(arrow, ActorAccessor.X).target(arrowPath.dots.get(0).x))
					.push(Tween.set(arrow, ActorAccessor.Y).target(arrowPath.dots.get(0).y))
					.push(Tween.set(arrow, ActorAccessor.ROTATION).target(angleOrigin));

			Vector2 prelast = arrowPath.dots.get(arrowPath.dots.size - 2).cpy();
			Vector2 last = arrowPath.dots.get(arrowPath.dots.size - 1).cpy();
			float angleTarget = last.sub(prelast).angle();

			Timeline arrowTimeline = Timeline.createParallel();
			arrowTimeline.delay(unit.getRender().fightAnim.getAnimationTime());

			// Arrow rotation
			if (Math.abs(angleOrigin - angleTarget) > 10) {
				if (arrowPath.dots.get(0).x < arrowPath.dots.get(arrowPath.dots.size - 1).x) {
					arrowTimeline.beginSequence();
					arrowTimeline.push(Tween.to(arrow, ActorAccessor.ROTATION, CrystalClash.FIGTH_ANIMATION_SPEED / 2)
							.target(0).ease(TweenEquations.easeNone));
					arrowTimeline.push(Tween.set(arrow, ActorAccessor.ROTATION)
							.target(360));
					arrowTimeline.push(Tween.to(arrow, ActorAccessor.ROTATION, CrystalClash.FIGTH_ANIMATION_SPEED / 2)
							.target(angleTarget)
							.ease(TweenEquations.easeNone));
					arrowTimeline.end();
				} else {
					arrowTimeline.push(Tween.to(arrow, ActorAccessor.ROTATION, CrystalClash.FIGTH_ANIMATION_SPEED)
							.target(angleTarget)
							.ease(TweenEquations.easeNone));
				}
			} else {
				arrowTimeline.push(Tween.set(arrow, ActorAccessor.ROTATION)
						.target(angleTarget)
						.ease(TweenEquations.easeNone));
			}

			// Arrow alpha
			arrowTimeline.push(Tween.to(arrow, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
					.target(1).ease(TweenEquations.easeNone));

			// Arrow movement
			arrowTimeline.beginSequence();
			for (int i = 1; i < arrowPath.dots.size; i++) {
				arrowTimeline.beginParallel();
				Vector2 v = arrowPath.dots.get(i);
				arrowTimeline.push(Tween.to(arrow, ActorAccessor.X, speed)
						.target(v.x).ease(TweenEquations.easeNone));
				arrowTimeline.push(Tween.to(arrow, ActorAccessor.Y, speed)
						.target(v.y).ease(TweenEquations.easeNone));
				arrowTimeline.end();
			}
			arrowTimeline.end();

			arrowTimeline.setUserData(new Object[] { arrow });
			arrowTimeline.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Image arrow = (Image) ((Object[]) source.getUserData())[0];
					arrow.remove();

					doRangedDamage(action);
				}
			});
			t.push(arrowTimeline);
		}
	}

	// DarkessMage
	private void doDarkessMageSoftDamage(AttackUnitAction action) {
		Unit targerUnit = action.target.getUnit();
		Unit ally = action.origin.getUnit();

		softDamage(targerUnit, ally.getDamage());
		Unit aux;
		for (int i = 0; i < action.target.neigbours.length; i++) {
			Cell neig = world.cellGrid[action.target.neigbours[i][0]][action.target.neigbours[i][1]];
			aux = neig.getUnit();
			if (aux != null)
				softDamage(aux, ally.getDamage() / 3);
		}
	}

	private void doDarkessMageDamage(AttackUnitAction action) {
		Unit targerUnit = action.target.getUnit();
		Unit ally = action.origin.getUnit();

		damage(targerUnit, ally.getDamage());
		Unit aux;
		for (int i = 0; i < action.target.neigbours.length; i++) {
			Cell neig = world.cellGrid[action.target.neigbours[i][0]][action.target.neigbours[i][1]];
			aux = neig.getUnit();
			if (aux != null)
				damage(aux, ally.getDamage() / 3);
		}
	}

	private void pushDarkessMageAttack(Timeline t, final AttackUnitAction action, Group entities) {
		Unit unit = action.origin.getUnit();

		Image fireBall = new Image(ResourceHelper.getUnitResourceTexture(unit.getName(), "projectile"));
		fireBall.setPosition(0 - fireBall.getWidth() * 2, 0 - fireBall.getHeight() * 2);
		fireBall.setOrigin(fireBall.getWidth() / 2, fireBall.getHeight() / 2);
		entities.addActor(fireBall);

		// Calculate the fireBall's path
		Path fireBallPath = new Path();

		PathManager.addArc(fireBallPath,
				action.origin.getCenterX(), action.origin.getCenterY() + 30,
				action.target.getCenterX(), action.target.getCenterY() + 30,
				fireBall.getWidth() / 2,
				fireBall.getHeight() / 2);

		t.beginSequence();
		if (fireBallPath.dots.size > 1) {
			float speed = CrystalClash.FIGTH_ANIMATION_SPEED / fireBallPath.dots.size;

			Vector2 first = fireBallPath.dots.get(0).cpy();
			Vector2 second = fireBallPath.dots.get(1).cpy();
			float angleOrigin = second.sub(first).cpy().angle();

			t.push(Tween.set(fireBall, ActorAccessor.ALPHA).target(0))
					.push(Tween.set(fireBall, ActorAccessor.X).target(first.x))
					.push(Tween.set(fireBall, ActorAccessor.Y).target(first.y))
					.push(Tween.set(fireBall, ActorAccessor.ROTATION).target(angleOrigin));

			Vector2 prelast = fireBallPath.dots.get(fireBallPath.dots.size - 2).cpy();
			Vector2 last = fireBallPath.dots.get(fireBallPath.dots.size - 1).cpy();
			float angleTarget = last.sub(prelast).angle();

			Timeline fireBallTimeline = Timeline.createParallel();
			fireBallTimeline.delay(unit.getRender().fightAnim.getAnimationTime());

			// fireBall rotation
			if (Math.abs(angleOrigin - angleTarget) > 10) {
				if (fireBallPath.dots.get(0).x < fireBallPath.dots.get(fireBallPath.dots.size - 1).x) {
					fireBallTimeline.beginSequence();
					fireBallTimeline.push(Tween.to(fireBall, ActorAccessor.ROTATION, CrystalClash.FIGTH_ANIMATION_SPEED / 2)
							.target(0).ease(TweenEquations.easeNone));
					fireBallTimeline.push(Tween.set(fireBall, ActorAccessor.ROTATION)
							.target(360));
					fireBallTimeline.push(Tween.to(fireBall, ActorAccessor.ROTATION, CrystalClash.FIGTH_ANIMATION_SPEED / 2)
							.target(angleTarget)
							.ease(TweenEquations.easeNone));
					fireBallTimeline.end();
				} else {
					fireBallTimeline.push(Tween.to(fireBall, ActorAccessor.ROTATION, CrystalClash.FIGTH_ANIMATION_SPEED)
							.target(angleTarget)
							.ease(TweenEquations.easeNone));
				}
			} else {
				fireBallTimeline.push(Tween.set(fireBall, ActorAccessor.ROTATION)
						.target(angleTarget)
						.ease(TweenEquations.easeNone));
			}

			// fireBall alpha
			fireBallTimeline.push(Tween.to(fireBall, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
					.target(1).ease(TweenEquations.easeNone));

			// fireBall movement
			fireBallTimeline.beginSequence();
			for (int i = 1; i < fireBallPath.dots.size; i++) {
				fireBallTimeline.beginParallel();
				Vector2 v = fireBallPath.dots.get(i);
				fireBallTimeline.push(Tween.to(fireBall, ActorAccessor.X, speed)
						.target(v.x).ease(TweenEquations.easeNone));
				fireBallTimeline.push(Tween.to(fireBall, ActorAccessor.Y, speed)
						.target(v.y).ease(TweenEquations.easeNone));
				fireBallTimeline.end();
			}

			fireBallTimeline.end();

			fireBallTimeline.setUserData(new Object[] { fireBall });
			fireBallTimeline.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Image fireBall = (Image) ((Object[]) source.getUserData())[0];
					fireBall.remove();
				}
			});

			t.push(fireBallTimeline);
		}

		// Center Fire Effect
		Timeline effectsTimeline = Timeline.createParallel();
		SuperAnimatedActor centerEffect = new SuperAnimatedActor(ResourceHelper.getUnitResourceSuperAnimation(unit.getName(), "effect"),
				true, FACING.right);
		centerEffect.setPosition(0 - centerEffect.getWidth() * 2, 0 - centerEffect.getHeight() * 2);
		centerEffect.setOrigin(centerEffect.getWidth() / 2, centerEffect.getHeight() / 2);
		entities.addActor(centerEffect);
		t.push(Tween.set(centerEffect, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(centerEffect, ActorAccessor.X).target(action.target.getCenterX()))
				.push(Tween.set(centerEffect, ActorAccessor.Y).target(action.target.getCenterY()));

		Timeline effectTimeline = Timeline.createSequence();
		effectTimeline.push(Tween.to(centerEffect, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
				.target(1).ease(TweenEquations.easeNone));
		effectTimeline.push(Tween.to(centerEffect, ActorAccessor.ALPHA, CrystalClash.FIGTH_ANIMATION_SPEED)
				.target(0).ease(TweenEquations.easeNone));
		effectTimeline.setUserData(new Object[] { centerEffect });
		effectTimeline.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				SuperAnimatedActor centerEffect = (SuperAnimatedActor) ((Object[]) source.getUserData())[0];
				centerEffect.remove();
				centerEffect = null;
			}
		});
		effectsTimeline.push(effectTimeline);

		// Neigbours Fire Effect
		SuperAnimatedActor effect;
		for (int i = 0; i < action.target.neigbours.length; i++) {
			effect = new SuperAnimatedActor(ResourceHelper.getUnitResourceSuperAnimation(unit.getName(), "effect"),
					true, FACING.right);
			effect.setPosition(0 - effect.getWidth() * 2, 0 - effect.getHeight() * 2);
			effect.setOrigin(effect.getWidth() / 2, effect.getHeight() / 2);
			entities.addActor(effect);
			Cell neig = world.cellGrid[action.target.neigbours[i][0]][action.target.neigbours[i][1]];
			t.push(Tween.set(effect, ActorAccessor.ALPHA).target(0))
					.push(Tween.set(effect, ActorAccessor.X).target(neig.getCenterX()))
					.push(Tween.set(effect, ActorAccessor.Y).target(neig.getCenterY()));

			effectTimeline = Timeline.createSequence();
			effectTimeline.push(Tween.to(effect, ActorAccessor.ALPHA, CrystalClash.FAST_ANIMATION_SPEED)
					.target(1).ease(TweenEquations.easeNone));
			effectTimeline.push(Tween.to(effect, ActorAccessor.ALPHA, CrystalClash.FIGTH_ANIMATION_SPEED)
					.target(0).ease(TweenEquations.easeNone));
			effectTimeline.setUserData(new Object[] { effect });
			effectTimeline.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					SuperAnimatedActor effect = (SuperAnimatedActor) ((Object[]) source.getUserData())[0];
					effect.remove();
					effect = null;
				}
			});
			effectsTimeline.push(effectTimeline);
		}
		effectsTimeline.setCallbackTriggers(TweenCallback.BEGIN);
		effectsTimeline.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.BEGIN) {
					doRangedDamage(action);
				}
			}
		});

		t.push(effectsTimeline);
		t.end();
	}
}
