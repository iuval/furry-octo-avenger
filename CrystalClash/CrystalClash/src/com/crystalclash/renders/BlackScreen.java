package com.crystalclash.renders;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.crystalclash.CrystalClash;
import com.crystalclash.accessors.ActorAccessor;
import com.crystalclash.renders.helpers.ResourceHelper;

public class BlackScreen extends Group {
	private static BlackScreen instance;
	private Image txrBlackScreen;
	private int callsCount = 0;
	private AnimatedGroup onTop;

	public BlackScreen() {
		txrBlackScreen = new Image(ResourceHelper.getTexture("menu/loading/background"));
		txrBlackScreen.setColor(txrBlackScreen.getColor().r, txrBlackScreen.getColor().g, txrBlackScreen.getColor().b, 0);
		addActor(txrBlackScreen);
		setVisible(false);
	}

	public static BlackScreen build() {
		if (instance == null)
			instance = new BlackScreen();
		return instance;
	}

	public Timeline show(AnimatedGroup onTop, Timeline t) {
		clearChildren();
		addActor(txrBlackScreen);
		addActor(onTop);

		return pushShow(onTop.pushEnterAnimation(t));
	}

	public Timeline show(AnimatedGroup onTop) {
		return show(onTop, Timeline.createParallel());
	}

	public Timeline hide(Timeline t) {
		if (onTop != null) {
			return pushHide(onTop.pushExitAnimation(Timeline.createParallel()));
		} else {
			return pushHide(t);
		}
	}

	public Timeline hide() {
		return hide(Timeline.createParallel());
	}

	private Timeline pushShow(Timeline t) {
		callsCount++;
		// Only animate if it's the first one
		if (callsCount == 1) {
			setVisible(true);
			return t.push(Tween.to(txrBlackScreen, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(1));
		} else {
			return t;
		}
	}

	private Timeline pushHide(Timeline t) {
		callsCount--;
		if (callsCount == 0) {
			return t.push(Tween.to(txrBlackScreen, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(0))
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							setVisible(false);
						}
					});
		} else {
			return t;
		}
	}
}
