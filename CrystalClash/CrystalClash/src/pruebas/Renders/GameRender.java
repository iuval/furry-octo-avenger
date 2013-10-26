package pruebas.Renders;

import pruebas.Controllers.WorldController;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameRender extends ViewRender {
	protected WorldController world;

	public GameRender(WorldController world) {
		this.world = world;
	}

	public abstract boolean canSend();

	public abstract void onSend();

	public abstract void clearAllChanges();

	public abstract void renderInTheBack(float dt, SpriteBatch batch);

	public abstract void renderInTheFront(float dt, SpriteBatch batch);

	public abstract boolean touchDown(float x, float y, int pointer, int button);

	public abstract boolean touchUp(float screenX, float screenY, int pointer, int button);

	public abstract boolean touchDragged(float screenX, float screenY, int pointer);

	public abstract boolean pan(float x, float y, float deltaX, float deltaY);

	public abstract void pause();

	public abstract void resume();

	public abstract void onAttackAction();

	public abstract void onDefendAction();

	public abstract void onMoveAction();

	public abstract void onUndoAction();
}
