package pruebas.Renders;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SelectUnitsRender extends GameRender {

	private int player;
	private Unit selectedUnit = null;
	private TabContainer tabs;

	public SelectUnitsRender(WorldController world, int player) {
		super(world);
		this.player = player;
		world.assignFirstTurnAvailablePlaces(player);

		init();
	}

	public void init() {

		GameController.getInstancia().loadUnitsStats();

		TextureAtlas atlas = new TextureAtlas(
				"data/Images/InGame/FirstTurn/unit_select.pack");
		Skin skin = new Skin(atlas);
		TextureAtlas portraitsAtlas = new TextureAtlas(
				"data/Units/unit_portraits.pack");

		tabs = new TabContainer(null);
		tabs.setPosition(0, 0);
		tabs.setSize(CrystalClash.HEIGHT, CrystalClash.WIDTH / 2);

		// Fire
		List listFire = new List(skin.getRegion("fire_background"));
		listFire.setPaddingTop(160);
		ToggleButton headerFire = new ToggleButton();
		headerFire.setTextureRegion(skin.getRegion("fire_tab"),
				skin.getRegion("fire_tab_selected"));
		tabs.addTab(headerFire, listFire);

		// Fire items
		UnitListItem item_fire_archer = new UnitListItem("fire_archer",
				portraitsAtlas.findRegion("portrait_fire_archer"), skin);
		listFire.addUnitItem(item_fire_archer);

		// Wind
		List listWind = new List(skin.getRegion("wind_background"));
		listWind.setPaddingTop(160);
		ToggleButton headerWind = new ToggleButton();
		headerWind.setTextureRegion(skin.getRegion("wind_tab"),
				skin.getRegion("wind_tab_selected"));
		tabs.addTab(headerWind, listWind);

		// Wind items
		UnitListItem item_wind_assassin = new UnitListItem("wind_assassin",
				portraitsAtlas.findRegion("portrait_wind_assassin"), skin);
		listWind.addUnitItem(item_wind_assassin);

		// Earth
		List listEarth = new List(skin.getRegion("earth_background"));
		listEarth.setPaddingTop(160);
		ToggleButton headerEarth = new ToggleButton();
		headerEarth.setTextureRegion(skin.getRegion("earth_tab"),
				skin.getRegion("earth_tab_selected"));
		tabs.addTab(headerEarth, listEarth);

		// Earth items
		UnitListItem item_earth_tank = new UnitListItem("earth_tank",
				portraitsAtlas.findRegion("portrait_earth_tank"), skin);
		listEarth.addUnitItem(item_earth_tank);

		// Water
		List listWater = new List(skin.getRegion("water_background"));
		listWater.setPaddingTop(160);
		ToggleButton headerWater = new ToggleButton();
		headerWater.setTextureRegion(skin.getRegion("water_tab"),
				skin.getRegion("water_tab_selected"));
		tabs.addTab(headerWater, listWater);

		// Water items

		// Darkness
		List listDarkness = new List(skin.getRegion("darkness_background"));
		listDarkness.setPaddingTop(160);
		ToggleButton headerDarkness = new ToggleButton();
		headerDarkness.setTextureRegion(skin.getRegion("darkness_tab"),
				skin.getRegion("darkness_tab_selected"));
		tabs.addTab(headerDarkness, listDarkness);

		// Wind items
		UnitListItem item_darkness_mage = new UnitListItem("darkness_mage",
				portraitsAtlas.findRegion("portrait_darkness_mage"), skin);
		listDarkness.addUnitItem(item_darkness_mage);

	}

	public void render(float dt, SpriteBatch batch) {
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
					Unit u = new Unit(item.getUnitName());
					if (player == 2)
						u.getRender().setFacing(FACING.left);
					selectedUnit = u;
					selectedUnit.setPosition(x, y);
				}
			} else {
				Cell cell = world.cellAt(x, y);
				if (cell != null) {
					selectedUnit = cell.getUnit(player);
					cell.removeUnit(player);
				}
			}
		}
		return true;
	}

	public boolean touchUp(float x, float y, int pointer, int button) {
		if (selectedUnit != null) {
			world.placeUnit(x, y, selectedUnit, player);
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
