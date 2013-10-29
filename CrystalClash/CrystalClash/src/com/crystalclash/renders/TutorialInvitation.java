package com.crystalclash.renders;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.controllers.GameController;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.renders.helpers.ui.MessageBox;
import com.crystalclash.renders.helpers.ui.MessageBoxCallback;

public class TutorialInvitation extends AnimatedGroup {

	private TextButton btnPlayTutorial;
	private TextButton btnSkipTutorial;
	private Image imgFireArcher;
	private Image imgBalloon;
	private Label lblMessage;

	public TutorialInvitation() {
		setBounds(0, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		imgFireArcher = new Image(ResourceHelper.getTexture("tutorial/fire_archer"));
		imgFireArcher.setPosition(-imgFireArcher.getWidth(), -10);
		addActor(imgFireArcher);

		GameEngine.start(Timeline.createParallel()
				.push(Tween.to(imgFireArcher, ActorAccessor.SCALE_Y, CrystalClash.REALLY_SLOW_ANIMATION_SPEED)
						.target(1.005f))
				.push(Tween.to(imgFireArcher, ActorAccessor.ROTATION, CrystalClash.REALLY_SLOW_ANIMATION_SPEED)
						.target(0.5f))
				.repeatYoyo(-1, 0));

		imgBalloon = new Image(ResourceHelper.getTexture("tutorial/message_balloon"));
		imgBalloon.setPosition(CrystalClash.WIDTH / 3, CrystalClash.HEIGHT + imgBalloon.getHeight());
		addActor(imgBalloon);

		lblMessage = new Label("Welcome " + GameController.getUser().getName() +
				"\n\nI can help you learn the\nbasics... Do you want me to?", new LabelStyle(ResourceHelper.getFont(), Color.BLACK));
		lblMessage.setPosition(imgBalloon.getX() + 145, imgBalloon.getTop() - 65);
		addActor(lblMessage);

		btnPlayTutorial = new TextButton("Lets Do It!", ResourceHelper.getOuterButtonStyle());
		btnPlayTutorial.setPosition(CrystalClash.WIDTH / 3 * 2 - btnPlayTutorial.getWidth() / 2 + 130, 0 - btnPlayTutorial.getHeight());
		btnPlayTutorial.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				lblMessage.setText("");
				GameEngine.getInstance().openTutorial();
			}
		});
		addActor(btnPlayTutorial);

		final MessageBoxCallback confirmation = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {
					GameController.setTutorialDone();
					GameEngine.start(BlackOverlay.build().hide(Timeline.createParallel()));
					MessageBox.build().hide();
				} else {
					lblMessage.setText("");
					GameEngine.getInstance().openTutorial();
				}
			}
		};
		btnSkipTutorial = new TextButton("No, thx", ResourceHelper.getOuterSmallButtonStyle());
		btnSkipTutorial.setPosition(CrystalClash.WIDTH / 3 * 2 - btnSkipTutorial.getWidth() / 2 + 130,
				0 - btnPlayTutorial.getHeight() - btnSkipTutorial.getHeight());
		btnSkipTutorial.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageBox.build()
						.setMessage("It's not safe going to the battle field without training...\nAre you sure you don't want to try?")
						.twoButtonsLayout("Yes, i'm sure", "Lets do it!")
						.setCallback(confirmation)
						.setHideOnAction(false)
						.show();
			}
		});
		addActor(btnSkipTutorial);
	}

	public void show() {
		GameEngine.start(BlackOverlay.build().show(this, Timeline.createParallel()));
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		// GameEngine.start(BlackOverlay.build().hide(Timeline.createParallel())
		// .push(Tween.to(imgFireArcher, ActorAccessor.X,
		// CrystalClash.SLOW_ANIMATION_SPEED)
		// .target(-imgFireArcher.getWidth()))
		// .push(Tween.to(imgBalloon, ActorAccessor.Y,
		// CrystalClash.SLOW_ANIMATION_SPEED)
		// .target(CrystalClash.HEIGHT + imgBalloon.getHeight()))
		// .push(Tween.to(btnPlayTutorial, ActorAccessor.Y,
		// CrystalClash.SLOW_ANIMATION_SPEED)
		// .target(0 - btnPlayTutorial.getHeight()))
		// .push(Tween.to(btnSkipTutorial, ActorAccessor.Y,
		// CrystalClash.SLOW_ANIMATION_SPEED)
		// .target(0 - btnSkipTutorial.getHeight()))
		// .setCallback(new TweenCallback() {
		// @Override
		// public void onEvent(int type, BaseTween<?> source) {
		// lblMessage.setPosition(imgBalloon.getX() + 50, imgBalloon.getTop() -
		// 150);
		// GameEngine.start(pushEnterAnimation(Timeline.createParallel()));
		// }
		// }));

		return t.push(Tween.to(imgFireArcher, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED)
				.target(-imgFireArcher.getWidth()))
				.push(Tween.to(imgBalloon, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT + imgBalloon.getHeight()))
				.push(Tween.to(btnPlayTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(0 - btnPlayTutorial.getHeight()))
				.push(Tween.to(btnSkipTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(0 - btnPlayTutorial.getHeight() - btnSkipTutorial.getHeight()));
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t.push(Tween.to(imgFireArcher, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED).target(0))
				.push(Tween.to(imgBalloon, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(CrystalClash.HEIGHT / 2))
				.push(Tween.to(btnPlayTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(CrystalClash.HEIGHT / 2 - btnPlayTutorial.getHeight()))
				.push(Tween.to(btnSkipTutorial, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(100))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						lblMessage.setPosition(imgBalloon.getX() + 145, imgBalloon.getTop() - 200);
					}
				});
	}
}