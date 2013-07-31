package pruebas.Renders.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class UnitTableItem extends Group {
	public String unitName;

	public UnitTableItem(String unitName, Skin skin) {
		this.unitName = unitName;
		float w = 156;
		float h = 78;
		setBounds(getX(), getY(), w, h);

		// // background image
		// Image background = new Image();
		// background.setBounds(getX(), getY(), w, h);
		// addActor(background);

		// surrender icon
		ImageButton button = new ImageButton(skin.getDrawable(unitName));
		button.setBounds(getX(), getY(), 78, 78);
		button.align(Align.center);
		addActor(button);

		Label labelName = new Label(unitName, skin, "font", Color.WHITE);

		labelName.setPosition(getX() + 78, getY());
		labelName.setWidth(w - 500);
		labelName.setAlignment(Align.center);
		addActor(labelName);

		// set the group size to background size
		setBounds(getX(), getY(), w, h);
	}
}
