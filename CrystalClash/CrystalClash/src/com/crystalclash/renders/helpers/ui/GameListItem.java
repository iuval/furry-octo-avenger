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
import com.crystalclash.renders.helpers.EmblemHelper;

public class GameListItem extends Group {
	public final String gameId;
	public int turn;
	public int emblem;
	Button btnPlay;

	public GameListItem(String gameId, String playerName, String victories, String turn, int emblemNum,
			String state, Skin skin, EventListener surrenderListener,
			EventListener playListener) {

		this.gameId = gameId;
		this.turn = Integer.parseInt(turn);

		Image bg = new Image(skin.get("background", TextureRegion.class));
		bg.setPosition(0, 0);
		addActor(bg);

		float w = bg.getWidth();
		float h = bg.getHeight();
		setSize(w, h);

		btnPlay = new Button(state.equals("play") ?
				skin.get("playStyle", TextButtonStyle.class) :
				skin.get("waitStyle", TextButtonStyle.class));
		if (state.equals("play"))
			btnPlay.addListener(playListener);
		btnPlay.setPosition(850, 0);
		addActor(btnPlay);

		Label lblName = new Label(playerName, skin, "font", Color.WHITE);
		lblName.setSize(520, 50);
		lblName.setPosition(270, 210);
		lblName.setAlignment(Align.center);
		addActor(lblName);

		Label labelV = new Label(victories, skin, "font", Color.WHITE);
		labelV.setPosition(430, 120);
		labelV.setAlignment(Align.center);
		addActor(labelV);

		this.emblem = emblemNum;

		Label labelTurn = new Label(turn, skin, "font", Color.LIGHT_GRAY);
		labelTurn.setPosition(690, 110);
		labelTurn.setAlignment(Align.center);
		addActor(labelTurn);

		// surrender icon
		TextButton buttonSurrender = new TextButton("", skin.get("surrenderStyle", TextButtonStyle.class));
		buttonSurrender.setPosition(37, 253);
		addActor(buttonSurrender);

		buttonSurrender.addListener(surrenderListener);
	}

	public void loadEmblem() {
		Image imgEmblem = new Image(EmblemHelper.getEmblem(emblem));
		imgEmblem.setPosition(55, 60);
		imgEmblem.setSize(160, 160);
		addActor(imgEmblem);
	}

	public void dispose() {
		this.clear();
	}
}