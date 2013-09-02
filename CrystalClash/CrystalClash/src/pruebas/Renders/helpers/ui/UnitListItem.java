package pruebas.Renders.helpers.ui;

import pruebas.Controllers.GameController;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UnitListItem extends Panel {
	private Sprite unitItem;
	private Sprite attackIcon;
	private Sprite speedIcon;
	private Sprite lifeIcon;
	private Sprite attackValueIcon;
	private Sprite speedValueIcon;
	private Sprite lifeValueIcon;
	private String unitName;

	public UnitListItem(String unitName, TextureRegion icon, Skin skin) {
		super(skin.getRegion("unit_stats_holder"));
		this.unitName = unitName;

		this.setSize(background.getRegionHeight(), background.getRegionWidth());
		this.unitItem = new Sprite(icon);
		unitItem.setPosition(33, 33);

		lifeIcon = new Sprite(skin.getRegion("icon_life"));
		lifeIcon.setPosition(203, 90);
		attackIcon = new Sprite(skin.getRegion("icon_attack"));
		attackIcon.setPosition(333, 90);
		speedIcon = new Sprite(skin.getRegion("icon_speed"));
		speedIcon.setPosition(463, 90);

		lifeValueIcon = new Sprite(skin.getRegion("stat_bar_"
				+ GameController.getInstance().getUnitLifeInScale(unitName)));
		lifeValueIcon.setPosition(170, 40);
		attackValueIcon = new Sprite(skin.getRegion("stat_bar_"
				+ GameController.getInstance().getUnitAttackInScale(unitName)));
		attackValueIcon.setPosition(300, 40);
		speedValueIcon = new Sprite(skin.getRegion("stat_bar_"
				+ GameController.getInstance().getUnitSpeedInScale(unitName)));
		speedValueIcon.setPosition(430, 40);
	}

	@Override
	public void draw(float dt, SpriteBatch batch) {
		super.draw(dt, batch);
		batch.draw(this.unitItem, getX() + unitItem.getX(),
				getY() + unitItem.getY());

		batch.draw(this.lifeIcon, getX() + lifeIcon.getX(),
				getY() + lifeIcon.getY());
		batch.draw(this.attackIcon, getX() + attackIcon.getX(), getY()
				+ attackIcon.getY());
		batch.draw(this.speedIcon, getX() + speedIcon.getX(), getY()
				+ speedIcon.getY());

		batch.draw(this.lifeValueIcon, getX() + lifeValueIcon.getX(), getY()
				+ lifeValueIcon.getY());
		batch.draw(this.attackValueIcon, getX() + attackValueIcon.getX(),
				getY() + attackValueIcon.getY());
		batch.draw(this.speedValueIcon, getX() + speedValueIcon.getX(), getY()
				+ speedValueIcon.getY());
	}

	public String getUnitName() {
		return unitName;
	}
}
