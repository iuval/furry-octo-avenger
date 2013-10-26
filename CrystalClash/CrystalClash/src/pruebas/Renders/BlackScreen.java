package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Renders.helpers.ResourceHelper;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BlackScreen {
	private static Image txrBlackScreen;
	private static int callsCount = 0;

	public static void load() {
		txrBlackScreen = new Image(ResourceHelper.getTexture("menu/loading/background"));
		txrBlackScreen.setColor(txrBlackScreen.getColor().r, txrBlackScreen.getColor().g, txrBlackScreen.getColor().b, 0);
		txrBlackScreen.setVisible(false);
	}

	public static Image getImage() {
		return txrBlackScreen;
	}

	public static Timeline pushShow(Timeline t) {
		callsCount++;
		txrBlackScreen.setVisible(true);
		return t.push(Tween.to(txrBlackScreen, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED)
				.target(1));
	}

	public static Timeline pushHide(Timeline t) {
		callsCount--;
		if (callsCount == 0) {
			return t.push(Tween.to(txrBlackScreen, ActorAccessor.ALPHA, CrystalClash.NORMAL_ANIMATION_SPEED)
					.target(0))
					.setCallback(new TweenCallback() {

						@Override
						public void onEvent(int type, BaseTween<?> source) {
							txrBlackScreen.setVisible(false);
						}
					});
		} else {
			return t;
		}
	}
}
