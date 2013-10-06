package pruebas.Renders.helpers.ui;

import pruebas.Renders.helpers.ResourceHelper;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UnitThumb extends Group {
	private TextureRegionDrawable txtSelected;
	private TextureRegionDrawable txtNotSelected;
	private Image sprBackground;
	private Image sprProfile;
	private Image sprElement;
	private TextButton btnSplash;
	private String unitName;

	public UnitThumb(String unitName) {
		this.unitName = unitName;

		txtSelected = new TextureRegionDrawable(ResourceHelper.getTexture("in_game/first_turn/item_border"));
		txtNotSelected = new TextureRegionDrawable(ResourceHelper.getTexture("in_game/first_turn/item_border"));
		sprBackground = new Image(txtNotSelected);
		sprBackground.setPosition(0, 0);
		addActor(sprBackground);

		sprProfile = new Image(ResourceHelper.getUnitProfile(unitName));
		sprProfile.setPosition(0, 0);
		addActor(sprProfile);

		sprElement = new Image(ResourceHelper.getUnitElementIcon(unitName));
		sprElement.setPosition(150, 200);
		addActor(sprElement);

		btnSplash = new TextButton("View", ResourceHelper.getButtonStyle());
		btnSplash.setSize(190, 50);
		btnSplash.setPosition(4, 6);
		addActor(btnSplash);
	}

	public void select() {
		sprBackground.setDrawable(txtSelected);
	}

	public void desselect() {
		sprBackground.setDrawable(txtNotSelected);
	}

	public String getUnitName() {
		return unitName;
	}
}
