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
import com.crystalclash.controllers.GameController;
import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.UnitItemSplashListener;
import com.crystalclash.renders.helpers.ui.UnitListSelectListener;
import com.crystalclash.renders.helpers.ui.UnitThumbsList;

public class SelectUnitsView extends GameView {
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
		GameController.loadUnitsStats();

		unitList = new UnitThumbsList(new UnitListSelectListener() {
			@Override
			public void select(String unitName, boolean selected, float x, float y) {
				if (selected) {
					selectedUnitName = unitName;
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
			unitList.setPosition(CrystalClash.WIDTH, 0);
		} else {
			unitList.setPosition(-unitList.getWidth(), 0);
		}
		addActor(unitList);

		resetUnitsCount();
	}

	private void enterSplash() {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(unitSplash, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(0)
						.ease(TweenEquations.easeOutBounce)));
	}

	private void exitSplash() {
		GameEngine.start(Timeline.createSequence()
				.push(Tween.to(unitSplash, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT)
						.ease(TweenEquations.easeInOutBack)));
	}

	private boolean canPlaceUnit() {
		return world.allysCount < GameController.unitsPerPlayer;
	}

	private void changeUnitsCountBy(int du) {
		world.allysCount += du;
		updateUnitsCountLabel();
	}

	private void resetUnitsCount() {
		world.allysCount = 0;
		updateUnitsCountLabel();
	}

	private void updateUnitsCountLabel() {
		unitList.setUnitCountText(world.allysCount + " of " + GameController.unitsPerPlayer);
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
				world.getRender().showStatsPopupFirstTurn(selectedUnit.getName());
				cell.removeUnit();
				changeUnitsCountBy(-1);
			} else {
				if (selectedUnitName != null && canPlaceUnit()) {
					selectedUnit = new Unit(selectedUnitName, false);
					if (world.player == 2)
						selectedUnit.getRender().setFacing(FACING.left);
					selectedUnit.setPosition(x, y);
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
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		return false;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		AudioManager.playMusic(MUSIC.select_units);
		if (world.player == 1) {
			t.push(Tween.to(unitList, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(CrystalClash.WIDTH / 2));
		} else {
			t.push(Tween.to(unitList, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(0));
		}
		return t;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		if (world.player == 1) {
			t.push(Tween.to(unitList, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(CrystalClash.WIDTH));
		} else {
			t.push(Tween.to(unitList, ActorAccessor.X, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(-unitList.getWidth()));
		}
		return t;
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
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onSend() {
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

	@Override
	public void prepareNextTurn() {
		for (int i = 0; i < world.cellGrid.length; i++) {
			for (int j = 0; j < world.cellGrid[0].length; j++) {
				world.cellGrid[i][j].state = Cell.NONE;
			}
		}
	}
}
