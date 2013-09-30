package pruebas.Renders.helpers.ui;

import pruebas.Accessors.UnitAccessor;
import pruebas.Controllers.GameController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Unit;
import pruebas.Renders.GameEngine;
import pruebas.Renders.helpers.ResourceHelper;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UnitStatsPopup extends Group {

	private Label lblTypeOfAttack;
	private Label lblDamage;
	private Label lblHP;
	private Label lblSpeed;

	private TextureRegionDrawable txtFireIcon;
	private TextureRegionDrawable txtEarthIcon;
	private TextureRegionDrawable txtWaterIcon;
	private TextureRegionDrawable txtWindIcon;
	private TextureRegionDrawable txtDarkIcon;
	private Image imgElement;

	private TextureRegionDrawable txtSlayer;
	private TextureRegionDrawable txtRanged;
	private TextureRegionDrawable txtMage;
	private TextureRegionDrawable txtTank;
	private Image imgTypeOfAttack;

	private Image imgDamage;
	private Image imgHP;
	private Image imgSpeed;

	private Image imgBackground;

	private boolean isBot = false;
	private boolean visible = false;

	public UnitStatsPopup() {
		load();
	}

	private void load() {
		TextureAtlas atlas = ResourceHelper.getTextureAtlas("in_game/unit_stats_popup/unit_stats_popup.pack");

		imgBackground = new Image(atlas.findRegion("stats_popup"));
		imgBackground.setPosition(40, 1);
		addActor(imgBackground);

		LabelStyle style = new LabelStyle(ResourceHelper.getFont(), Color.WHITE);

		// Element
		txtFireIcon = new TextureRegionDrawable(ResourceHelper.getTexture("units/fire/icon_race_fire"));
		txtEarthIcon = new TextureRegionDrawable(ResourceHelper.getTexture("units/earth/icon_race_earth"));
		txtWaterIcon = new TextureRegionDrawable(ResourceHelper.getTexture("units/water/icon_race_water"));
		txtWindIcon = new TextureRegionDrawable(ResourceHelper.getTexture("units/wind/icon_race_wind"));
		txtDarkIcon = new TextureRegionDrawable(ResourceHelper.getTexture("units/darkness/icon_race_darkness"));

		imgElement = new Image(txtFireIcon);
		imgElement.setPosition(0, 0);
		addActor(imgElement);

		// Type
		txtSlayer = new TextureRegionDrawable(atlas.findRegion("icon_slayer"));
		txtMage = new TextureRegionDrawable(atlas.findRegion("icon_mage"));
		txtRanged = new TextureRegionDrawable(atlas.findRegion("icon_ranged"));
		txtTank = new TextureRegionDrawable(atlas.findRegion("icon_tank"));

		imgTypeOfAttack = new Image(txtSlayer);
		imgTypeOfAttack.setPosition(55, 10);
		addActor(imgTypeOfAttack);

		lblTypeOfAttack = new Label("", style);
		lblTypeOfAttack.setPosition(95, 30);
		addActor(lblTypeOfAttack);

		// Damage
		imgDamage = new Image(atlas.findRegion("icon_attack"));
		imgDamage.setPosition(229, 10);
		addActor(imgDamage);

		lblDamage = new Label("", style);
		lblDamage.setPosition(275, 30);
		addActor(lblDamage);

		// HP
		imgHP = new Image(atlas.findRegion("icon_life"));
		imgHP.setPosition(343, 10);
		addActor(imgHP);

		lblHP = new Label("", style);
		lblHP.setPosition(385, 30);
		addActor(lblHP);

		// Speed
		imgSpeed = new Image(atlas.findRegion("icon_speed"));
		imgSpeed.setPosition(540, 10);
		addActor(imgSpeed);

		lblSpeed = new Label("", style);
		lblSpeed.setPosition(585, 30);
		addActor(lblSpeed);

		setSize(632, 57);
		setPosition(CrystalClash.WIDTH / 2 - getWidth() / 2, -100);
	}

	public void show(Unit unit) {
		switch (GameController.getUnitElementIndex(unit.getName())) {
		case Unit.ELEMENT_FIRE:
			imgElement.setDrawable(txtFireIcon);
			break;
		case Unit.ELEMENT_EARTH:
			imgElement.setDrawable(txtEarthIcon);
			break;
		case Unit.ELEMENT_WIND:
			imgElement.setDrawable(txtWindIcon);
			break;
		case Unit.ELEMENT_WATER:
			imgElement.setDrawable(txtWaterIcon);
			break;
		case Unit.ELEMENT_DARKNESS:
			imgElement.setDrawable(txtDarkIcon);
			break;
		}

		switch (GameController.getUnitTypeIndex(unit.getName())) {
		case Unit.TYPE_slayer:
			imgTypeOfAttack.setDrawable(txtSlayer);
			lblTypeOfAttack.setText("Slayer");
			break;
		case Unit.TYPE_MAGE:
			imgTypeOfAttack.setDrawable(txtMage);
			lblTypeOfAttack.setText("Mage");
			break;
		case Unit.TYPE_RANGED:
			imgTypeOfAttack.setDrawable(txtRanged);
			lblTypeOfAttack.setText("Ranged");
			break;
		case Unit.TYPE_TANK:
			imgTypeOfAttack.setDrawable(txtTank);
			lblTypeOfAttack.setText("Tank");
			break;
		}

		lblDamage.setText("" + unit.getDamage());
		lblHP.setText(String.format("%s/%s", unit.getHP(), GameController.getUnitLife(unit.getName())));
		lblSpeed.setText("" + unit.getSpeed());

		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(this, UnitAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
						.target(0)));
		if (visible) {
			if (isBot == unit.getY() < 150) {
				setY(CrystalClash.HEIGHT - getHeight());
			} else if (!isBot == unit.getY() > 150) {
				setY(0);
			}
		} else {
			isBot = unit.getY() > 150;
			if (isBot) {
				setY(-getHeight());
				GameEngine.start(Timeline.createParallel()
						.push(Tween.to(this, UnitAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
								.target(0)));
			} else {
				setY(CrystalClash.HEIGHT + getHeight());
				GameEngine.start(Timeline.createParallel()
						.push(Tween.to(this, UnitAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
								.target(CrystalClash.HEIGHT - getHeight())));
			}
		}
		visible = true;
	}

	public void hide() {
		if (visible) {
			if (isBot) {
				GameEngine.start(Timeline.createParallel()
						.push(Tween.to(this, UnitAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
								.target(-getHeight())));
			} else {
				GameEngine.start(Timeline.createParallel()
						.push(Tween.to(this, UnitAccessor.Y, CrystalClash.FAST_ANIMATION_SPEED)
								.target(CrystalClash.HEIGHT)));
			}
			visible = false;
		}
	}
}
