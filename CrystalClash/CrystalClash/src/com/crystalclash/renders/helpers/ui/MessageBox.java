package com.crystalclash.renders.helpers.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.renders.AnimatedGroup;
import com.crystalclash.renders.BlackOverlay;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.util.I18n;

public class MessageBox extends AnimatedGroup {
	public enum Buttons {
		None, One, Two
	}

	private static MessageBox instance;

	private Image imgWindowBackground;
	private Label lblMessage;
	private TextButton btnYes;
	private TextButton btnNo;
	private boolean visible = false;

	private MessageBoxCallback callback;
	private boolean hideOnAction = true;
	private Object userData;

	private MessageBox() {
		setSize(800, CrystalClash.HEIGHT / 2);
		setPosition(CrystalClash.WIDTH / 2 - 400, CrystalClash.HEIGHT + getHeight());

		imgWindowBackground = new Image(ResourceHelper.getTexture("menu/message_box_background"));
		imgWindowBackground.setSize(getWidth(), getHeight());
		imgWindowBackground.setPosition(0, 0);
		addActor(imgWindowBackground);

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getNormalFont(), Color.WHITE));
		lblMessage.setAlignment(Align.center);
		lblMessage.setWrap(true);
		addActor(lblMessage);

		btnYes = new TextButton("Yeah!!", ResourceHelper.getButtonStyle());
		btnYes.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(MessageBoxCallback.YES);
				if (hideOnAction)
					hide();
			}
		});

		btnNo = new TextButton("Meh", ResourceHelper.getButtonStyle());
		btnNo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(MessageBoxCallback.NO);
				if (hideOnAction)
					hide();
			}
		});
	}

	public static MessageBox build() {
		if (instance == null)
			instance = new MessageBox();
		instance.setHideOnAction(true);
		return instance;
	}

	public MessageBox setCallback(MessageBoxCallback callback) {
		this.callback = callback;
		return this;
	}

	public MessageBox setHideOnAction(boolean hide) {
		hideOnAction = hide;
		return this;
	}

	public MessageBox setMessage(String i18n_ref, Buttons btns) {
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

	public MessageBox setMessage(String i18n_ref, Buttons btns, String... args) {
		return setMessage(String.format(I18n.t(i18n_ref), args), btns);
	}

	public MessageBox setText(String text) {
		lblMessage.setText(text);
		oneButtonsLayout("ok");
		return this;
	}

	public MessageBox setUserData(Object data) {
		userData = data;
		return this;
	}

	private MessageBox twoButtonsLayout(String yes, String no) {
		btnYes.setText(yes);
		btnYes.setSize(360, 100);
		btnYes.setPosition(getWidth() - btnYes.getWidth() - 30, 30);
		addActor(btnYes);

		btnNo.setText(no);
		btnNo.setSize(360, 100);
		btnNo.setPosition(30, 30);
		addActor(btnNo);

		lblMessage.setSize(getWidth() - 100, getHeight() - 150);
		lblMessage.setPosition(50, 100);
		return this;
	}

	private MessageBox oneButtonsLayout(String yes) {
		btnYes.setText(yes);
		btnYes.setSize(500, 100);
		btnYes.setPosition(getWidth() / 2 - btnYes.getWidth() / 2, 30);
		addActor(btnYes);

		btnNo.remove();

		lblMessage.setSize(getWidth() - 100, getHeight() - 150);
		lblMessage.setPosition(50, 100);
		return this;
	}

	private MessageBox noButtonsLayout() {
		btnYes.remove();

		btnNo.remove();

		lblMessage.setSize(getWidth() - 100, getHeight() - 100);
		lblMessage.setPosition(50, 50);
		return this;
	}

	public static void show(Object userData) {
		build().setUserData(userData).show();
	}

	public void show() {
		setZIndex(99);
		GameEngine.start(BlackOverlay.build().show(this, pushEnterAnimation(Timeline.createParallel())));
		visible = true;
	}

	public void hide() {
		GameEngine.start(BlackOverlay.build().hide(pushExitAnimation(Timeline.createParallel())));
		visible = false;
	}

	protected void callCallback(int type) {
		if (callback != null)
			callback.onEvent(type, userData);
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t.push(Tween.to(this, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
				.target(CrystalClash.HEIGHT / 4));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t.push(Tween.to(this, ActorAccessor.Y, CrystalClash.NORMAL_ANIMATION_SPEED)
				.target(CrystalClash.HEIGHT + getHeight()));
	}

}
