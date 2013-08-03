package pruebas.Renders;

import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Renders.helpers.ui.UnitList;
import pruebas.Renders.helpers.ui.UnitListItem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SelectUnitsRender extends GameRender {

	private Unit selectedUnit = null;
	private UnitList list;

	public SelectUnitsRender(GameEngine e, WorldController world) {
		super(e, world);
		world.assignFirstTurnAvailablePlaces(1);
		list = new UnitList();
		list.setPosition(CrystalClash.WIDTH / 2, 80);
		list.setSize(CrystalClash.WIDTH / 2, CrystalClash.HEIGHT - 160);
		init();
	}

	public void init() {
		BitmapFont unitForn = new BitmapFont(
				Gdx.files.internal("data/Fonts/font.fnt"), false);
		TextureAtlas listItemButtonAtlas = new TextureAtlas(
				"data/Units/units_icons.pack");
		UnitListItem item_fire_archer = new UnitListItem("fire_archer",
				listItemButtonAtlas.findRegion("fire_archer"), unitForn);
		list.addUnitItem(item_fire_archer);

		UnitListItem item_earth_tank = new UnitListItem("earth_tank",
				listItemButtonAtlas.findRegion("fire_archer"), unitForn);
		list.addUnitItem(item_earth_tank);

		UnitListItem item_wind_assassin = new UnitListItem("wind_assassin",
				listItemButtonAtlas.findRegion("fire_archer"), unitForn);
		list.addUnitItem(item_wind_assassin);

		UnitListItem item_darkness_mage = new UnitListItem("darkness_mage",
				listItemButtonAtlas.findRegion("fire_archer"), unitForn);
		list.addUnitItem(item_darkness_mage);

	}

	public void render(float dt, SpriteBatch batch) {
		if (selectedUnit != null) {
			selectedUnit.getRender().draw(batch, dt);
		}
		list.draw(batch, dt);
	}

	public boolean touchDown(float x, float y, int pointer, int button) {
		if (selectedUnit == null) {
			if (list.hit(x, y)) {
				UnitListItem item = list.getItemAt(x, y);
				if (item != null) {
					Unit u = new Unit(item.getUnitName());
					selectedUnit = u;
					selectedUnit.setPosition(x, y);
				}
			} else {
				Cell cell = world.cellAt(x, y);
				if (cell != null) {
					selectedUnit = cell.getUnit(1);
					cell.removeUnit(1);
				}
			}
		}
		return true;
	}

	public boolean touchUp(float x, float y, int pointer, int button) {
		if (selectedUnit != null) {
			world.placeUnit(x, y, selectedUnit, 1);
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

}
