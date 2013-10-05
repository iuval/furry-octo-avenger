package pruebas.Renders.helpers.ui;

import pruebas.Renders.helpers.ResourceHelper;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class UnitListItem extends Group {
	private Image sprBackground;
	private Image sprProfile;
	private Image sprElement;
	private TextButton btnSplash;
	private String unitName;

	public UnitListItem(String unitName) {
		this.unitName = unitName;

		sprBackground = new Image(ResourceHelper.getTexture("in_game/first_turn/item_border"));
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

	public String getUnitName() {
		return unitName;
	}
}
