package pruebas.Renders;

import pruebas.Controllers.WorldController;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameRender {
	protected GameEngine engine;
	protected WorldController world;

	public GameRender(GameEngine e, WorldController world) {
		engine = e;
		this.world = world;
	}

	public abstract void render(float dt, SpriteBatch batch);

	public abstract boolean touchDown(float x, float y, int pointer, int button);

	public abstract boolean touchUp(float screenX, float screenY, int pointer,
			int button);

	public abstract boolean touchDragged(float screenX, float screenY,
			int pointer);

	public abstract boolean pan(float x, float y, float deltaX, float deltaY);

}
