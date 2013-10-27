package pruebas.renders.views;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.scenes.scene2d.Group;

public abstract class ViewRender extends Group {

	public ViewRender() {
	}

	public abstract void init();

	public abstract Timeline pushEnterAnimation(Timeline t);

	public abstract Timeline pushExitAnimation(Timeline t);

	public abstract void shown();

	public abstract void closed();

	public abstract void dispose();
}
