package com.crystalclash.renders;

import aurelienribon.tweenengine.Timeline;

public interface Animated {

	Timeline pushEnterAnimation(Timeline t);

	Timeline pushExitAnimation(Timeline t);
}
