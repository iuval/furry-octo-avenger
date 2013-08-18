package pruebas.Renders;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Group;

public abstract class MenuRender extends Group implements InputProcessor {

	public MenuRender() {
	}

	public abstract Timeline pushEnterAnimation(Timeline t);

	public abstract Timeline pushExitAnimation(Timeline t);
}
