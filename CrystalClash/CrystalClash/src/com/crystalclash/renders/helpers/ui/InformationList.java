package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.crystalclash.util.I18n;

public class InformationList extends Group {
	private Label text;
	private ScrollPane scrollPane;

	public InformationList(Skin skin) {
		text = new Label(I18n.t("information"), skin, "normal_font", Color.WHITE);
		text.setAlignment(Align.top | Align.center);
		
		scrollPane = new ScrollPane(text);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, true);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setForceScroll(false, true);
		scrollPane.setFillParent(true);
		scrollPane.invalidate();
		addActor(scrollPane);
	}

}
