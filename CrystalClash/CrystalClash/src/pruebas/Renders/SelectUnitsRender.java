package pruebas.Renders;

import pruebas.Audio.AudioManager;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.ui.UnitThumbListener;
import pruebas.Renders.helpers.ui.UnitThumbsList;
import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SelectUnitsRender extends GameRender {
	private int unitCount = 0;
	private String selectedUnitName;
	private Unit selectedUnit = null;
	private UnitThumbsList unitList;

	public SelectUnitsRender(WorldController world) {
		super(world);
		world.assignFirstTurnAvailablePlaces();

		load();
		GameEngine.hideLoading();
	}

	public void load() {
		GameController.loadUnitsStats();

		unitList = new UnitThumbsList(world.player, new UnitThumbListener() {
			@Override
			public void onClick(String unitName, boolean selected, float x, float y) {
				if (selected) {
					selectedUnitName = unitName;
				} else {
					selectedUnitName = null;
					selectedUnit = null;
				}
			}
		});
		addActor(unitList);

		resetUnitsCount();
	}

	private boolean canPlaceUnit() {
		return unitCount < GameController.unitsPerPlayer;
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
		unitList.setUnitCountText(unitCount + "/" + GameController.unitsPerPlayer);
	}

	@Override
	public void clearAllChanges() {
		world.deleteAllUnits();
		resetUnitsCount();
	}

	public boolean touchDown(float x, float y, int pointer, int button) {
		Cell cell = world.cellAt(x, y);
		if (cell != null) {
			selectedUnit = cell.getUnit();
			if (selectedUnit != null) {
				cell.removeUnit();
				changeUnitsCountBy(-1);
			} else {
				if (selectedUnitName != null && canPlaceUnit()) {
					if (selectedUnit == null) {
						Unit u = new Unit(selectedUnitName, false);
						if (world.player == 2)
							u.getRender().setFacing(FACING.left);
						selectedUnit = u;
					}
					selectedUnit.setPosition(x, y);
				}
			}
		}

		return true;
	}

	public boolean touchUp(float x, float y, int pointer, int button) {
		if (selectedUnit != null) {
			if (world.placeUnit(x, y, selectedUnit)) {
				changeUnitsCountBy(1);
			}
			selectedUnit = null;
		}
		return true;
	}

	public boolean touchDragged(float x, float y, int pointer) {
		if (selectedUnit != null) {
			selectedUnit.setPosition(x, y);
		}
		return true;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		return false;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		AudioManager.playMusic("choose your destiny");
		return t;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		// TODO Auto-generated method stub
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
}
