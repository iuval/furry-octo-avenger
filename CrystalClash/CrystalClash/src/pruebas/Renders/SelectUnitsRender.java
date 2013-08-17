package pruebas.Renders;

import java.util.Enumeration;

import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.ui.List;
import pruebas.Renders.helpers.ui.TabContainer;
import pruebas.Renders.helpers.ui.ToggleButton;
import pruebas.Renders.helpers.ui.UnitListItem;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SelectUnitsRender extends GameRender {

	private Unit selectedUnit = null;
	private TabContainer tabs;

	public SelectUnitsRender(WorldController world) {
		super(world);
		world.assignFirstTurnAvailablePlaces();

		init();
		GameEngine.hideLoading();
	}

	public void init() {

		GameController.getInstancia().loadUnitsStats();

		TextureAtlas atlas = new TextureAtlas(
				"data/Images/InGame/FirstTurn/unit_select.pack");
		Skin skin = new Skin(atlas);
		TextureAtlas portraitsAtlas = new TextureAtlas(
				"data/Units/unit_portraits.pack");

		tabs = new TabContainer(null);
		if (world.player == 1) {
			tabs.setPosition(CrystalClash.WIDTH / 2, 0);
		} else {
			tabs.setPosition(0, 0);
		}
		tabs.setSize(CrystalClash.HEIGHT, CrystalClash.WIDTH / 2);

		// Fire
		List listFire = new List(skin.getRegion("fire_background"));
		listFire.setPaddingTop(160);
		ToggleButton headerFire = new ToggleButton();
		headerFire.setTextureRegion(skin.getRegion("fire_tab"),
				skin.getRegion("fire_tab_selected"));
		tabs.addTab(headerFire, listFire);

		// Wind
		List listWind = new List(skin.getRegion("wind_background"));
		listWind.setPaddingTop(160);
		ToggleButton headerWind = new ToggleButton();
		headerWind.setTextureRegion(skin.getRegion("wind_tab"),
				skin.getRegion("wind_tab_selected"));
		tabs.addTab(headerWind, listWind);

		// Earth
		List listEarth = new List(skin.getRegion("earth_background"));
		listEarth.setPaddingTop(160);
		ToggleButton headerEarth = new ToggleButton();
		headerEarth.setTextureRegion(skin.getRegion("earth_tab"),
				skin.getRegion("earth_tab_selected"));
		tabs.addTab(headerEarth, listEarth);

		// Water
		List listWater = new List(skin.getRegion("water_background"));
		listWater.setPaddingTop(160);
		ToggleButton headerWater = new ToggleButton();
		headerWater.setTextureRegion(skin.getRegion("water_tab"),
				skin.getRegion("water_tab_selected"));
		tabs.addTab(headerWater, listWater);

		// Darkness
		List listDarkness = new List(skin.getRegion("darkness_background"));
		listDarkness.setPaddingTop(160);
		ToggleButton headerDarkness = new ToggleButton();
		headerDarkness.setTextureRegion(skin.getRegion("darkness_tab"),
				skin.getRegion("darkness_tab_selected"));
		tabs.addTab(headerDarkness, listDarkness);

		// List items
		Enumeration<String> unit_names = GameController.getInstancia().getUnitNames();
		String unit_name;
		while (unit_names.hasMoreElements()) {
			unit_name = unit_names.nextElement();
			UnitListItem item = new UnitListItem(unit_name,
					portraitsAtlas.findRegion("portrait_" + unit_name), skin);
			switch (GameController.getInstancia().getUnitElement(unit_name)) {
			case Unit.ELEMENT_FIRE:
				listFire.addUnitItem(item);
				break;
			case Unit.ELEMENT_EARTH:
				listEarth.addUnitItem(item);
				break;
			case Unit.ELEMENT_WIND:
				listWind.addUnitItem(item);
				break;
			case Unit.ELEMENT_WATER:
				listWater.addUnitItem(item);
				break;
			case Unit.ELEMENT_DARKNESS:
				listDarkness.addUnitItem(item);
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void clearAllMoves() {
		// TODO Quitar todas las unidades colocadas en el mapa.

	}

	public void render(float dt, SpriteBatch batch, Stage stage) {
		tabs.draw(dt, batch);
		if (selectedUnit != null) {
			selectedUnit.getRender().draw(batch, dt);
		}
	}

	public boolean touchDown(float x, float y, int pointer, int button) {
		if (selectedUnit == null) {
			if (tabs.hit(x, y)) {
				UnitListItem item = (UnitListItem) ((List) tabs
						.getCurrentPanel()).getItemAt(x, y);
				if (item != null) {
					Unit u = new Unit(item.getUnitName(), false, GameController
							.getInstancia().getUnitLife(item.getUnitName()));
					if (world.player == 2)
						u.getRender().setFacing(FACING.left);
					selectedUnit = u;
					selectedUnit.setPosition(x, y);
				}
			} else {
				Cell cell = world.cellAt(x, y);
				if (cell != null) {
					selectedUnit = cell.getUnit(world.player);
					cell.removeUnit(world.player);
				}
			}
		}
		return true;
	}

	public boolean touchUp(float x, float y, int pointer, int button) {
		if (selectedUnit != null) {
			world.placeUnit(x, y, selectedUnit);
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
