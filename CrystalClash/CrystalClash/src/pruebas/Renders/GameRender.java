package pruebas.Renders;

import pruebas.Controllers.WorldController;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class GameRender {
	protected WorldController world;

	public GameRender(WorldController world) {
		this.world = world;
	}

	public abstract void clearAllChanges();

	public abstract void render(float dt, SpriteBatch batch, Stage stage);

	public abstract boolean touchDown(float x, float y, int pointer, int button);

	public abstract boolean touchUp(float screenX, float screenY, int pointer,
			int button);

	public abstract boolean touchDragged(float screenX, float screenY,
			int pointer);

	public abstract boolean pan(float x, float y, float deltaX, float deltaY);

	public abstract Timeline pushEnterAnimation(Timeline t);

	public abstract Timeline pushExitAnimation(Timeline t);
}
