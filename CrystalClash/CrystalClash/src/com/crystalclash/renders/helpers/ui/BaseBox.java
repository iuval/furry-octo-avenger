package com.crystalclash.renders.helpers.ui;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.renders.AnimatedGroup;
import com.crystalclash.renders.BlackOverlay;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ResourceHelper;

public class BaseBox extends AnimatedGroup {
	public enum BoxButtons {
		None, One, Two
	}

	private Image imgWindowBackground;
	protected Actor widget;
	protected TextButton btnYes;
	protected TextButton btnNo;
	private boolean visible = false;

	private boolean hideOnAction = true;
	private Object userData;

	private BoxCallback callback;

	protected BaseBox(Actor widget) {
		this.widget = widget;
		setSize(800, CrystalClash.HEIGHT / 2);
		setPosition(CrystalClash.WIDTH / 2 - 400, CrystalClash.HEIGHT + getHeight());

		imgWindowBackground = new Image(ResourceHelper.getTexture("menu/message_box_background"));
		imgWindowBackground.setSize(getWidth(), getHeight());
		imgWindowBackground.setPosition(0, 0);
		addActor(imgWindowBackground);

		btnYes = new TextButton("Yeah!!", ResourceHelper.getButtonStyle());
		btnYes.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(BoxCallback.YES);
				if (hideOnAction)
					hide();
			}
		});

		btnNo = new TextButton("Meh", ResourceHelper.getButtonStyle());
		btnNo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				callCallback(BoxCallback.NO);
				if (hideOnAction)
					hide();
			}
		});
	}

	public BaseBox setCallback(BoxCallback callback) {
		this.callback = callback;
		return this;
	}

	public BaseBox setHideOnAction(boolean hide) {
		hideOnAction = hide;
		return this;
	}

	public BaseBox setUserData(Object data) {
		userData = data;
		return this;
	}

	protected BaseBox twoButtonsLayout(String yes, String no) {
		btnYes.setText(yes);
		btnYes.setSize(360, 100);
		btnYes.setPosition(getWidth() - btnYes.getWidth() - 30, 30);
		addActor(btnYes);

		btnNo.setText(no);
		btnNo.setSize(360, 100);
		btnNo.setPosition(30, 30);
		addActor(btnNo);

		widget.setSize(getWidth() - 100, getHeight() - 150);
		widget.setPosition(50, 100);
		return this;
	}

	protected BaseBox oneButtonsLayout(String yes) {
		btnYes.setText(yes);
		btnYes.setSize(500, 100);
		btnYes.setPosition(getWidth() / 2 - btnYes.getWidth() / 2, 30);
		addActor(btnYes);

		btnNo.remove();

		widget.setSize(getWidth() - 100, getHeight() - 150);
		widget.setPosition(50, 100);
		return this;
	}

	protected BaseBox noButtonsLayout() {
		btnYes.remove();

		btnNo.remove();

		widget.setSize(getWidth() - 100, getHeight() - 100);
		widget.setPosition(50, 50);
		return this;
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
