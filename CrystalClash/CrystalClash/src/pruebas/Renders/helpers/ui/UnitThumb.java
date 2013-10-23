package pruebas.Renders.helpers.ui;

import pruebas.Renders.helpers.ResourceHelper;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UnitThumb extends Group {
	private Image sprBackground;
	private Image sprSelectedBorder;
	private Image sprProfile;
	private Image sprElement;
	private ImageButton btnSplash;
	private String unitName;

	public UnitThumb(String unitName, ClickListener clickListener, ClickListener splashListener) {
		this.unitName = unitName;

		sprProfile = new Image(ResourceHelper.getUnitProfile(unitName));
		sprProfile.setPosition(0, 0);
		addActor(sprProfile);

		sprSelectedBorder = new Image(ResourceHelper.getTexture("in_game/first_turn/item_selected"));
		sprSelectedBorder.setPosition(0, 0);
		sprBackground = new Image(ResourceHelper.getTexture("in_game/first_turn/item_border"));
		sprBackground.setPosition(0, 0);
		addActor(sprBackground);

		sprElement = new Image(ResourceHelper.getUnitElementIcon(unitName));
		sprElement.setPosition(150, 200);
		addActor(sprElement);

		btnSplash = new ImageButton(new TextureRegionDrawable(ResourceHelper.getTexture("in_game/first_turn/inspect_button")),
				new TextureRegionDrawable(ResourceHelper.getTexture("in_game/first_turn/inspect_button_pressed")));
		btnSplash.setSize(189, 64);
		btnSplash.setPosition(4, 6);
		addActor(btnSplash);

		sprSelectedBorder.setTouchable(Touchable.disabled);
		sprBackground.setTouchable(Touchable.disabled);
		sprProfile.addListener(clickListener);

		btnSplash.addListener(splashListener);
	}

	public void select() {
		addActor(sprSelectedBorder);
	}

	public void desselect() {
		sprSelectedBorder.remove();
	}

	public String getUnitName() {
		return unitName;
	}
}
