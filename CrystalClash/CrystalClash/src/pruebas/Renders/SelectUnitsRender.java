package pruebas.Renders;

import java.util.Enumeration;

import pruebas.Audio.AudioManager;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.ResourceHelper;
import pruebas.Renders.helpers.ui.UnitListItem;
import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class SelectUnitsRender extends GameRender {
	private int unitCount = 0;
	private Label lblUnitsCount;
	private Unit selectedUnit = null;
	private Table table;
	private Image imgTableBg;
	private ScrollPane scrollPane;

	public SelectUnitsRender(WorldController world) {
		super(world);
		world.assignFirstTurnAvailablePlaces();

		load();
		GameEngine.hideLoading();
	}

	public void load() {
		GameController.loadUnitsStats();

		lblUnitsCount = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblUnitsCount.setPosition(CrystalClash.WIDTH - 100, 50);
		resetUnitsCount();
		addActor(lblUnitsCount);

		imgTableBg = new Image(ResourceHelper.getTexture("in_game/first_turn/list_background"));
		addActor(imgTableBg);

		table = new Table();
		scrollPane = new ScrollPane(table);
		if (world.player == 1) {
			scrollPane.setPosition(CrystalClash.WIDTH / 2 + 10, 155);
			imgTableBg.setPosition(CrystalClash.WIDTH / 2, 0);
		} else {
			scrollPane.setPosition(10, 155);
			imgTableBg.setPosition(0, 0);
		}
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, true);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setForceScroll(false, true);
		scrollPane.setSize(623, 685);
		scrollPane.invalidate();
		addActor(scrollPane);

		table.align(Align.top | Align.left);
		table.defaults().width(198).height(252).padLeft(6).padTop(6);
		// List items
		Enumeration<String> unit_names = GameController.getUnitNames();
		String unit_name;
		int i = 0;
		while (unit_names.hasMoreElements()) {
			if (i == 3) {
				table.row();
				i = 0;
			}
			i++;
			unit_name = unit_names.nextElement();
			UnitListItem item = new UnitListItem(unit_name);
			item.addListener((new DragListener() {
				public void touchDragged(InputEvent event, float x, float y, int pointer) {
					if (selectedUnit == null) {
						UnitListItem item = (UnitListItem) event.getListenerActor();
						if (item != null) {
							Unit u = new Unit(item.getUnitName(), false);
							if (world.player == 2)
								u.getRender().setFacing(FACING.left);
							selectedUnit = u;
							Vector2 v = GameEngine.getRealPosition(x, y);
							selectedUnit.setPosition(v.x, v.y);
						}
					}
				}

			}));
			table.add(item);
		}
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
		lblUnitsCount.setText(unitCount + "/" + GameController.unitsPerPlayer);
	}

	@Override
	public void clearAllChanges() {
		world.deleteAllUnits();
		resetUnitsCount();
	}

	public boolean touchDown(float x, float y, int pointer, int button) {
		if (selectedUnit == null) {
			Cell cell = world.cellAt(x, y);
			if (cell != null) {
				selectedUnit = cell.getUnit();
				if (selectedUnit != null) {
					cell.removeUnit();
					changeUnitsCountBy(-1);
				}
			}
		}
		return true;
	}

	public boolean touchUp(float x, float y, int pointer, int button) {
		if (selectedUnit != null) {
			if (canPlaceUnit() && world.placeUnit(x, y, selectedUnit)) {
				changeUnitsCountBy(1);
			}
			selectedUnit = null;
		}
		return true;
	}

	public boolean touchDragged(float x, float y, int pointer) {
		System.out.println("touchdragged" + x + ", " + y);
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
