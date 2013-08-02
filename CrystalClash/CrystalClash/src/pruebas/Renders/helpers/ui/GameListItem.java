package pruebas.Renders.helpers.ui;

import pruebas.CrystalClash.CrystalClash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class GameListItem extends Group {
	public final String gameId;

	public GameListItem(String gameId, String playerName, String turn,
			String state, Skin skin, EventListener surrenderListener,
			EventListener playListener) {
		this.gameId = gameId;

		float w = CrystalClash.WIDTH;
		float h = 250;
		setBounds(getX(), getY(), w, h);

		// background image
		Image background = new Image(skin.getDrawable("background"));
		background.setBounds(getX(), getY(), w, h);
		addActor(background);

		// surrender icon
		TextButton buttonSurrender = new TextButton("Surrender", skin.get(
				"buttonStyle", TextButtonStyle.class));
		buttonSurrender.setBounds(getX(), getY(), h, 150);
		buttonSurrender.align(Align.center);
		addActor(buttonSurrender);
		buttonSurrender.addListener(surrenderListener);

		Label labelName = new Label(playerName + " - " + turn, skin, "font",
				Color.WHITE);

		labelName.setPosition(getX() + 250, getY());
		labelName.setWidth(w - 500);
		labelName.setAlignment(Align.center);
		addActor(labelName);

		if (state.equals("play")) {
			TextButton buttonPlay = new TextButton("Play", skin.get(
					"buttonStyle", TextButtonStyle.class));
			buttonPlay.setBounds(w - 250, getY(), h, 150);
			buttonPlay.align(Align.center);
			addActor(buttonPlay);
			buttonPlay.addListener(playListener);
		}
		// set the group size to background size
		setBounds(getX(), getY(), background.getWidth(), background.getHeight());
	}

}