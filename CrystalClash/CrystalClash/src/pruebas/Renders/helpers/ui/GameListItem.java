package pruebas.Renders.helpers.ui;

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

		float w = 1189;
		float h = 187;
		setBounds(52, getY(), w, h);

		if (state.equals("play")) {
			TextButton buttonPlay = new TextButton(playerName, skin.get(
					"playStyle", TextButtonStyle.class));
			buttonPlay.setBounds(0, 0, w, h);
			buttonPlay.align(Align.center);
			buttonPlay.addListener(playListener);
			addActor(buttonPlay);
		} else {
			TextButton buttonWait = new TextButton(playerName, skin.get(
					"waitStyle", TextButtonStyle.class));
			buttonWait.setBounds(0, 0, w, h);
			buttonWait.align(Align.center);
			addActor(buttonWait);
		}

		Label labelTurn = new Label(turn, skin, "font", Color.WHITE);
		labelTurn.setPosition(14, 14);
		labelTurn.setSize(160, 160);
		labelTurn.setAlignment(Align.center);
		addActor(labelTurn);

		// surrender icon
		TextButton buttonSurrender = new TextButton("", skin.get(
				"surrenderStyle", TextButtonStyle.class));
		buttonSurrender.setBounds(961, 19, 210, 148);
		buttonSurrender.align(Align.center);
		addActor(buttonSurrender);
		buttonSurrender.addListener(surrenderListener);

		// set the group size to background size
		setBounds(getX(), getY(), w, h);
	}
}