package com.crystalclash.views;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.controllers.GameController;
import com.crystalclash.entities.User;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.EmblemList;

public class ProfileView extends InputView {
	private static ProfileView instance;

	private Label lblName;
	private Label lblVictories;
	private Label lblDraws;
	private Label lblLoses;
	private TextButton btnSave;

	private EmblemList list;

	private ProfileView() {
		load();
	}

	public static ProfileView getInstance() {
		if (instance == null)
			instance = new ProfileView();

		return instance;
	}

	@Override
	public void init() {
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t.push(Tween.to(list, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
				.target(0))
				.push(Tween.to(btnSave, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT - 60));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t.push(Tween.to(list, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
				.target(-640))
				.push(Tween.to(btnSave, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.WIDTH));
	}

	private void load() {
		GameController.loadSharedStats();

		btnSave = new TextButton("Save", ResourceHelper.getButtonStyle());
		btnSave.setSize(90, 50);
		btnSave.setPosition(CrystalClash.WIDTH - 100, CrystalClash.HEIGHT);
		btnSave.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameController.getUser().setEmblem(list.getSelectedEmblem());
				GameController.getUser().update();
			}
		});
		addActor(btnSave);

		User user = GameController.getUser();
		lblName = new Label("asdasd" + user.getName(), new LabelStyle(ResourceHelper.getBigBorderFont(), Color.WHITE));
		lblName.setWrap(true);
		lblName.setSize(CrystalClash.WIDTH - 60, 200);
		lblName.setPosition(30, CrystalClash.HEIGHT - 30);
		addActor(lblName);

		list = new EmblemList();
		list.setPosition(-640, 0);
		addActor(list);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void shown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK)
			GameEngine.getInstance().openMenuGames();
		return true;
	}
}
