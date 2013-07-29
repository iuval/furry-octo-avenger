package pruebas.Renders.shared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

		// background image
		// Image background = new Image(Assets.white);
		// background.setBounds(getX(), getY(), 800, 86);
		// addActor(background);s

		float w = Gdx.graphics.getWidth();
		float h = 250;

		// surrender icon
		TextButton buttonSurrender = new TextButton("Surrender", skin.get(
				"buttonStyle", TextButtonStyle.class));
		buttonSurrender.setBounds(getX(), getY(), 250, 150);
		buttonSurrender.align(Align.center);
		addActor(buttonSurrender);
		buttonSurrender.addListener(surrenderListener);

		Label labelName = new Label(playerName + " - " + turn, skin, "font",
				Color.WHITE);
		labelName.setPosition(getX() + buttonSurrender.getWidth() + 50, getY());
		labelName.setWidth(100);
		labelName.setAlignment(Align.center);
		addActor(labelName);

		if (state == "play") {
			TextButton buttonPlay = new TextButton("Play", skin.get(
					"buttonStyle", TextButtonStyle.class));
			buttonPlay.setBounds(w - 250, getY(), 250, 150);
			buttonPlay.align(Align.center);
			addActor(buttonPlay);
			buttonPlay.addListener(playListener);
		}
		// set the group size to background size
		setBounds(getX(), getY(), w, h);
	}

}