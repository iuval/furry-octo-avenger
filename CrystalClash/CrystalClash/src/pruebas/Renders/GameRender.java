package pruebas.Renders;

import pruebas.Controllers.WorldController;
import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class GameRender extends Group {
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

	public abstract Timeline pushEnterAnimation(Timeline t);

	public abstract Timeline pushExitAnimation(Timeline t);

	public abstract void pause();

	public abstract void resume();
	
	public abstract ClickListener attackListener();
	
	public abstract ClickListener defendListener();
	
	public abstract ClickListener moveListener();
	
	public abstract ClickListener undoListener();
}
