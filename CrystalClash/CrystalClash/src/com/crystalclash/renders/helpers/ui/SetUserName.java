package com.crystalclash.renders.helpers.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.util.I18n;

public class SetUserName extends Group {
	private Label lblName;
	private TextField txtName;

	public SetUserName() {
		lblName = new Label(I18n.t("profile_name"), new LabelStyle(ResourceHelper.getNormalBorderFont(), Color.WHITE));
		lblName.setFillParent(true);
		lblName.setPosition(130, 100);
		addActor(lblName);

		Image imgText = new Image(ResourceHelper.getTexture("text_field_background"));
		imgText.setSize(620, 50);
		imgText.setPosition(65, 100);
		addActor(imgText);

		Skin textFieldSkin = new Skin();
		textFieldSkin.add("textFieldCursor", ResourceHelper.getTexture("menu/cursor_1"));

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = ResourceHelper.getNormalBorderFont();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = textFieldSkin.getDrawable("textFieldCursor");

		txtName = new TextField("", textFieldStyle);
		txtName.setMessageText(I18n.t("profile_name_placeholder"));
		txtName.setSize(620, 50);
		txtName.setPosition(70, 105);
		txtName.setMaxLength(30);
		txtName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				adjustToKeyboard(true);
			}
		});

		addActor(txtName);
	}

	public String getUserName() {
		return txtName.getText();
	}

	private void adjustToKeyboard(boolean up) {
		Gdx.input.setOnscreenKeyboardVisible(up);
		// true mueve hacia arriba, false mueve hacia abajo
		float jump = 0;
		if (up)
			jump = 413;
		else
			jump = 213;

		float speed = CrystalClash.SLOW_ANIMATION_SPEED;
		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(this.getParent(), ActorAccessor.Y, speed).target(jump)));
	}
}
