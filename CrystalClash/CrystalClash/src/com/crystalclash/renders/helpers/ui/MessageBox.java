package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.crystalclash.CrystalClash;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.util.I18n;

public class MessageBox extends BaseBox {
	private static MessageBox instance;

	private Label lblMessage;

	private MessageBox() {
		super(new Label("", new LabelStyle(ResourceHelper.getNormalFont(), Color.WHITE)));
		setSize(800, CrystalClash.HEIGHT / 2);
		setPosition(CrystalClash.WIDTH / 2 - 400, CrystalClash.HEIGHT + getHeight());

		lblMessage = (Label) widget;
		lblMessage.setAlignment(Align.center);
		lblMessage.setWrap(true);
		addActor(lblMessage);
	}

	public static MessageBox build() {
		if (instance == null)
			instance = new MessageBox();
		instance.setHideOnAction(true);
		return instance;
	}

	public MessageBox setMessage(String i18n_ref, BoxButtons btns) {
		lblMessage.setText(I18n.t(i18n_ref));
		switch (btns) {
		case One:
			oneButtonsLayout(I18n.t(i18n_ref + "_ok"));
			break;
		case Two:
			twoButtonsLayout(I18n.t(i18n_ref + "_yes"), I18n.t(i18n_ref + "_no"));
			break;
		case None:
			noButtonsLayout();
			break;
		}
		return this;
	}

	public MessageBox setMessage(String i18n_ref, BoxButtons btns, String... args) {
		return setMessage(String.format(I18n.t(i18n_ref), args), btns);
	}

	public MessageBox setText(String text) {
		lblMessage.setText(text);
		oneButtonsLayout("ok");
		return this;
	}

	public static void show(Object userData) {
		build().setUserData(userData).show();
	}

	@Override
	public MessageBox setUserData(Object data) {
		super.setUserData(data);
		return this;
	}
}
