package pruebas.Renders.helpers.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class GameListItem extends Group {
	public final String gameId;
	public int turn;

	public GameListItem(String gameId, String playerName, String victories, String turn,
			String state, Skin skin, EventListener surrenderListener,
			EventListener playListener) {
		this.gameId = gameId;
		this.turn = Integer.parseInt(turn);

		float w = 1189;
		float h = 187;
		setBounds(52, getY(), w, h);

		TextButton button;
		if (state.equals("play")) {
			button = new TextButton("", skin.get("playStyle", TextButtonStyle.class));
			button.addListener(playListener);
		} else {
			button = new TextButton("", skin.get("waitStyle", TextButtonStyle.class));
		}
		button.setText(playerName);
		button.setBounds(0, 0, w, h);
		button.align(Align.center);
		addActor(button);

		Label labelvictories = new Label(victories + " victories" , skin, "font", Color.GREEN);
		labelvictories.setSize(160, 70);
		labelvictories.setPosition(w / 2 - labelvictories.getWidth() / 2, 20);
		labelvictories.setAlignment(Align.center);
		addActor(labelvictories);

		Label labelTurn = new Label(turn, skin, "font", Color.WHITE);
		labelTurn.setPosition(14, 14);
		labelTurn.setSize(160, 160);
		labelTurn.setAlignment(Align.center);
		addActor(labelTurn);

		// surrender icon
		TextButton buttonSurrender = new TextButton("", skin.get("surrenderStyle", TextButtonStyle.class));
		buttonSurrender.setBounds(961, 19, 210, 148);
		buttonSurrender.align(Align.center);
		addActor(buttonSurrender);
		buttonSurrender.addListener(surrenderListener);

		// set the group size to background size
		setBounds(getX(), getY(), w, h);
	}

	public void dispose() {
		this.clear();
	}
}