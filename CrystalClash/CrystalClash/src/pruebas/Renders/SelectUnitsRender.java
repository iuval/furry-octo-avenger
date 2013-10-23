package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Audio.AudioManager;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.ResourceHelper;
import pruebas.Renders.helpers.ui.UnitItemSplashListener;
import pruebas.Renders.helpers.ui.UnitListSelectListener;
import pruebas.Renders.helpers.ui.UnitThumbsList;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SelectUnitsRender extends GameRender {
	private int unitCount = 0;
	private String selectedUnitName;
	private Unit selectedUnit = null;
	private UnitThumbsList unitList;

	private boolean onSplash = false;
	private Image unitSplash;

	public SelectUnitsRender(WorldController world) {
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
			unitList.setPosition(CrystalClash.WIDTH / 2, 0);
		} else {
			unitList.setPosition(0, 0);
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
		unitList.setUnitCountText(unitCount + " of " + GameController.unitsPerPlayer);
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

	public boolean touchUp(float x, float y, int pointer, int button) {
		if (selectedUnit != null) {
			if (world.placeUnit(x, y, selectedUnit)) {
				changeUnitsCountBy(1);
			}
			desSelectUnit();
		}
		return true;
	}

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
