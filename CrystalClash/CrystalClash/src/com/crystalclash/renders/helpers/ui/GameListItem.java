package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.crystalclash.renders.helpers.EmblemHelper;

public class GameListItem extends Group {
	public final String gameId;
	public int turn;
	public int emblem;
	public String playerName;
	Button btnPlay;

	public GameListItem(String gameId, String playerName, String victories, String turn, int emblemNum,
			String state, boolean surrender,
			Skin skin, EventListener surrenderListener, EventListener ackSurrenderListener,
			EventListener playListener) {

		this.gameId = gameId;
		this.turn = Integer.parseInt(turn);
		this.emblem = emblemNum;
		this.playerName = playerName;

		Image bg = new Image(skin.get("background", TextureRegion.class));
		bg.setPosition(0, 0);
		addActor(bg);

		float w = bg.getWidth();
		float h = bg.getHeight();
		setSize(w, h);

		ButtonStyle style = skin.get("waitStyle", ButtonStyle.class);
		if(surrender)
			style = skin.get("surrendedStyle", ButtonStyle.class);
		else if(state.equals("play"))
			style = skin.get("playStyle", ButtonStyle.class);
		
		btnPlay = new Button(style);
		if (!surrender && state.equals("play"))
			btnPlay.addListener(playListener);
		btnPlay.setPosition(850, 0);
		addActor(btnPlay);

		Label lblName = new Label(playerName, skin, "big_font", Color.BLACK);
		lblName.setSize(520, 50);
		lblName.setPosition(270, 210);
		lblName.setAlignment(Align.center);
		addActor(lblName);

		Label labelV = new Label(victories, skin, "big_font", Color.BLACK);
		labelV.setPosition(425, 100);
		Label labelVTittle = new Label("Victories", skin, "small_font", Color.BLACK);
		labelVTittle.setPosition(390, 148);
		addActor(labelV);
		addActor(labelVTittle);

		Label labelTurn = new Label(turn, skin, "big_font", Color.BLACK);
		labelTurn.setPosition(615, 110);
		Label labelTurnTittle = new Label("Turn", skin, "small_font", Color.BLACK);
		labelTurnTittle.setPosition(600, 148);
		addActor(labelTurn);
		addActor(labelTurnTittle);

		// surrender icon
		Button buttonSurrender = new Button(skin.get("surrenderStyle", ButtonStyle.class));
		buttonSurrender.setPosition(35, 260);
		addActor(buttonSurrender);
		if (surrender) {
			buttonSurrender.addListener(ackSurrenderListener);
		} else {
			buttonSurrender.addListener(surrenderListener);
		}
	}

	public void loadEmblem() {
		Image imgEmblem = new Image(EmblemHelper.getEmblem(emblem));
		imgEmblem.setPosition(50, 65);
		imgEmblem.setSize(160, 160);
		addActor(imgEmblem);
	}

	public void dispose() {
		this.clear();
	}
}