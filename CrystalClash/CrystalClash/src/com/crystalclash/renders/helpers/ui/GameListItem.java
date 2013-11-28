package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

		float w = 1047;
		float h = 340;
		setSize(w, h);

		Image bg = new Image(skin.get("background", TextureRegion.class));
		bg.setPosition(0, 0);
		addActor(bg);

		Button btnPlay = new Button(state.equals("play") ?
				skin.get("playStyle", TextButtonStyle.class) :
				skin.get("waitStyle", TextButtonStyle.class));
		if (state.equals("play"))
			btnPlay.addListener(playListener);
		btnPlay.setPosition(850, 0);
		addActor(btnPlay);

		Label lblName = new Label(playerName, skin, "font", Color.WHITE);
		lblName.setSize(550, 100);
		lblName.setPosition(200, 200);
		lblName.setAlignment(Align.center);
		addActor(lblName);

		Label labelV = new Label(victories, skin, "font", Color.WHITE);
		labelV.setSize(420, 100);
		labelV.setPosition(240, 90);
		labelV.setAlignment(Align.center);
		addActor(labelV);

		Label labelTurn = new Label(turn, skin, "font", Color.LIGHT_GRAY);
		labelTurn.setSize(560, 60);
		labelTurn.setPosition(700, 130);
		addActor(labelTurn);

		// surrender icon
		TextButton buttonSurrender = new TextButton("", skin.get("surrenderStyle", TextButtonStyle.class));
		buttonSurrender.setPosition(37, 253);
		addActor(buttonSurrender);

		buttonSurrender.addListener(surrenderListener);
	}

	public void dispose() {
		this.clear();
	}
}