package com.crystalclash.renders.attacks;

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
import com.crystalclash.renders.helpers.ui.SuperAnimatedActor;

public class AttackFactory {
	private WorldController world;

	public AttackFactory(WorldController controller) {
		world = controller;
	}

	public void pushAttack(Timeline t, AttackUnitAction action, Group entities) {
		String unitName = action.origin.getUnit().getName();
		if (unitName.equals("fire_archer"))
			pushFireArcherAttack(t, action, entities);
		if (unitName.equals("darkness_mage"))
			pushDarkessMageAttack(t, action, entities);
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
		if (enemy != null && enemy.isAlive()) {
			enemy.damage(player.getDamage());

			if (!enemy.isAlive()) {
				if (enemy.isEnemy())
					world.enemiesCount--;
				else
					world.allysCount--;
			}
		}
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
			unit.damage(damage);
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

	private void softDamage(Unit enemy, float damage) {
		if (enemy != null && enemy.isAlive()) {
			enemy.softDamage(damage);
		}
	}

	// FireArcher
	private void doFireArcherSoftDamage(AttackUnitAction action) {
		Unit enemy = action.target.getUnit();
		Unit ally = action.origin.getUnit();
		if (enemy != null && enemy.isAlive()) {
			enemy.softDamage(ally.getDamage());
		}
	}

	private boolean doFireArcherDamage(AttackUnitAction action) {
		Unit enemy = action.target.getUnit();
		Unit ally = action.origin.getUnit();

		return damage(enemy, ally.getDamage());
	}

	private void pushFireArcherAttack(Timeline t, AttackUnitAction action, Group entities) {
		Unit unit = action.origin.getUnit();

		Image arrow = new Image(ResourceHelper.getUnitResourceTexture(unit.getName(), "projectile"));
		arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
		entities.addActor(arrow);

		// Calculate the arrow's path
		Path arrowPath = new Path();
		PathManager.addArc(arrowPath,
				action.origin.getCenterX(), action.origin.getCenterY() + 30,
				action.target.getCenterX(), action.target.getCenterY() + 30,
				arrow.getWidth() / 2,
				arrow.getHeight() / 2);

		float speed = CrystalClash.FIGTH_ANIMATION_SPEED / arrowPath.dots.size;

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
			}
		});
		t.push(arrowTimeline);
	}

	// FireArcher

	// DarkessMage
	private void doDarkessMageSoftDamage(AttackUnitAction action) {
		Unit enemy = action.target.getUnit();
		Unit ally = action.origin.getUnit();

		softDamage(enemy, ally.getDamage());
		for (int i = 0; i < action.target.neigbours.length; i++) {
			Cell neig = world.cellGrid[action.target.neigbours[i][0]][action.target.neigbours[i][1]];
			softDamage(neig.getUnit(), ally.getDamage());
		}
	}

	private void doDarkessMageDamage(AttackUnitAction action) {
		Unit enemy = action.target.getUnit();
		Unit ally = action.origin.getUnit();

		damage(enemy, ally.getDamage());
		for (int i = 0; i < action.target.neigbours.length; i++) {
			Cell neig = world.cellGrid[action.target.neigbours[i][0]][action.target.neigbours[i][1]];
			damage(neig.getUnit(), ally.getDamage());
		}
	}

	private void pushDarkessMageAttack(Timeline t, AttackUnitAction action, Group entities) {
		Unit unit = action.origin.getUnit();

		Image arrow = new Image(ResourceHelper.getUnitResourceTexture(unit.getName(), "projectile"));
		arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
		entities.addActor(arrow);

		// Calculate the arrow's path
		Path arrowPath = new Path();

		PathManager.addArc(arrowPath,
				action.origin.getCenterX(), action.origin.getCenterY() + 30,
				action.target.getCenterX(), action.target.getCenterY() + 30,
				arrow.getWidth() / 2,
				arrow.getHeight() / 2);

		float speed = CrystalClash.FIGTH_ANIMATION_SPEED / arrowPath.dots.size;

		Vector2 first = arrowPath.dots.get(0).cpy();
		Vector2 second = arrowPath.dots.get(1).cpy();
		float angleOrigin = second.sub(first).cpy().angle();

		t.push(Tween.set(arrow, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(arrow, ActorAccessor.X).target(first.x))
				.push(Tween.set(arrow, ActorAccessor.Y).target(first.y))
				.push(Tween.set(arrow, ActorAccessor.ROTATION).target(angleOrigin));

		Vector2 prelast = arrowPath.dots.get(arrowPath.dots.size - 2).cpy();
		Vector2 last = arrowPath.dots.get(arrowPath.dots.size - 1).cpy();
		float angleTarget = last.sub(prelast).angle();

		Timeline arrowTimeline = Timeline.createParallel();
		arrowTimeline.delay(unit.getRender().fightAnim.getAnimationTime());

		// Arrow rotation
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
			}
		});

		Timeline effectsTimeline = Timeline.createParallel();
		for (int i = 0; i < action.target.neigbours.length; i++) {
			SuperAnimatedActor effect = new SuperAnimatedActor(ResourceHelper.getUnitResourceSuperAnimation(unit.getName(), "effect"),
					true, FACING.right);
			effect.setOrigin(effect.getWidth() / 2, effect.getHeight() / 2);
			entities.addActor(effect);
			Cell neig = world.cellGrid[action.target.neigbours[i][0]][action.target.neigbours[i][1]];
			t.push(Tween.set(effect, ActorAccessor.ALPHA).target(0))
					.push(Tween.set(effect, ActorAccessor.X).target(neig.getCenterX()))
					.push(Tween.set(effect, ActorAccessor.Y).target(neig.getCenterY()));

			Timeline effectTimeline = Timeline.createSequence();
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

		t.beginSequence();
		t.push(arrowTimeline);
		t.push(effectsTimeline);
		t.end();
	}
	// DarkessMage
}