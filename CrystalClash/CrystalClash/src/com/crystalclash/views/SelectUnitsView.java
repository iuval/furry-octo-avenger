package com.crystalclash.views;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.audio.AudioManager;
import com.crystalclash.audio.AudioManager.MUSIC;
import com.crystalclash.audio.AudioManager.SOUND;
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.renders.helpers.ui.UnitItemSplashListener;
import com.crystalclash.renders.helpers.ui.UnitListSelectListener;
import com.crystalclash.renders.helpers.ui.UnitThumbsList;
import com.crystalclash.renders.helpers.ui.BaseBox.BoxButtons;
import com.crystalclash.util.I18n;

public class SelectUnitsView extends GameView {
	private int unitCount = 0;
	private String selectedUnitName;
	private Unit selectedUnit = null;
	private UnitThumbsList unitList;

	private boolean onSplash = false;
	private Image unitSplash;

	public SelectUnitsView(WorldController world) {
		super(world);
		world.assignFirstTurnAvailablePlaces();

		load();
		GameEngine.hideLoading();
	}

	public void load() {
		GameController.loadSharedStats();
		AudioManager.loadFirstTurnSFX();

		unitList = new UnitThumbsList(new UnitListSelectListener() {
			@Override
			public void select(String unitName, boolean selected, float x, float y) {
				if (selected) {
					selectedUnitName = unitName;
					GameEngine.start(world.getRender().pushHideGameMenuButtons(Timeline.createParallel()));
					world.getRender().showStatsPopupFirstTurn(selectedUnitName);
				} else {
					desSelectUnit();
				}
			}
		}, new UnitItemSplashListener() {

			@Override
			public void openSplash(String unitName) {
				if (!onSplash) {
					desSelectUnit();
					onSplash = true;
					unitSplash = new Image(ResourceHelper.getUnitSplash(unitName));
					addActor(unitSplash);
					unitSplash.setPosition(0, CrystalClash.HEIGHT);
					unitSplash.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							if (onSplash) {
								onSplash = false;
								exitSplash();
							}
						}
					});
					enterSplash();
				}
			}
		});

		if (world.player == 1) {
			unitList.setPosition(CrystalClash.WIDTH / 2, 0);
		} else {
			unitList.setPosition(0, 0);
		}
		addActor(unitList);

		resetUnitsCount();
	}

	private void enterSplash() {
		GameEngine.start(world.getRender().pushHideGameMenuButtons(Timeline.createSequence())
				.push(Tween.to(unitSplash, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(0)
						.ease(TweenEquations.easeOutQuint)));
	}

	private void exitSplash() {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(unitSplash, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT)
						.ease(TweenEquations.easeInQuint))
				.push(world.getRender().pushShowGameMenuButtons(Timeline.createSequence())));
	}

	private boolean canPlaceUnit() {
		return unitCount < GameController.MAX_UNIT_PER_PLAYER;
	}

	private void changeUnitsCountBy(int du) {
		unitCount += du;
		updateUnitsCountLabel();
	}

	private void resetUnitsCount() {
		unitCount = 0;
		updateUnitsCountLabel();
	}

	private void updateUnitsCountLabel() {
		unitList.setUnitCountText(unitCount + " " + I18n.t("unit_list_count") + " " + GameController.MAX_UNIT_PER_PLAYER);
	}

	@Override
	public void clearAllChanges() {
		world.deleteAllUnits();
		resetUnitsCount();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Cell cell = world.cellAt(x, y);
		if (cell != null) {
			selectedUnit = cell.getUnit();
			if (selectedUnit != null) {
				unitList.desSelect();
				GameEngine.start(world.getRender().pushHideGameMenuButtons(Timeline.createParallel()));
				world.getRender().showStatsPopupFirstTurn(selectedUnit.getName());
				cell.removeUnit();
				changeUnitsCountBy(-1);
			} else {
				if (selectedUnitName != null && canPlaceUnit()) {
					selectedUnit = new Unit(selectedUnitName, false);
					if (world.player == 2)
						selectedUnit.getRender().setFacing(FACING.left);
					selectedUnit.setPosition(x, y);

					selectedUnit.getRender().playSFX(SOUND.place);
				}
			}
		}

		return true;
	}

	@Override
	public boolean touchUp(float x, float y, int pointer, int button) {
		if (selectedUnit != null) {
			if (world.placeUnit(x, y, selectedUnit)) {
				changeUnitsCountBy(1);
			}
			desSelectUnit();
		}
		return true;
	}

	@Override
	public boolean touchDragged(float x, float y, int pointer) {
		if (selectedUnit != null) {
			selectedUnit.setPosition(x, y);
		}
		return true;
	}

	private void desSelectUnit() {
		world.getRender().hideStatsPopup();
		unitList.desSelect();
		selectedUnitName = null;
		selectedUnit = null;
		GameEngine.start(world.getRender().pushShowGameMenuButtons(Timeline.createParallel()));
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		return false;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		AudioManager.playMusic(MUSIC.select_units);
		return t.push(Tween.set(unitList, ActorAccessor.ALPHA)
				.target(0))
				.push(Tween.to(unitList, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED)
						.target(1));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t.push(Tween.to(unitList, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED)
				.target(0));
	}

	@Override
	public void renderInTheBack(float dt, SpriteBatch batch) {
	}

	@Override
	public void renderInTheFront(float dt, SpriteBatch batch) {
		if (selectedUnit != null) {
			selectedUnit.getRender().draw(batch, dt);
		}
	}

	@Override
	public boolean canSend() {
		if(unitCount == 0)
			return false;
		else
			return true;
	}

	@Override
	public void onSend() {
		MessageBox.build()
			.setMessage("select_units_send", BoxButtons.One)
			.setCallback(null)
			.setHideOnAction(true)
			.show();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

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
