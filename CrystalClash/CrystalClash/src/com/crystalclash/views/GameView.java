package com.crystalclash.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.crystalclash.controllers.WorldController;

public abstract class GameView extends BaseView {
	protected WorldController world;

	public GameView(WorldController world) {
		this.world = world;
	}

	public abstract boolean canSend();

	public abstract void onSend();

	public void onExit() {
	}

	public String getExitMessage() {
		return "world_back_to_menu";
	}

	public boolean getShowSurrenderOption() {
		return true;
	}

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
