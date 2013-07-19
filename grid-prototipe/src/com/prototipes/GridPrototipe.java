package com.prototipes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GridPrototipe extends Game {
    // constant useful for logging
    public static final String LOG = GridPrototipe.class.getSimpleName();
    // a libgdx helper class that logs the current FPS each second
    private FPSLogger fpsLogger;
    
	private OrthographicCamera camera;
	private SpriteBatch batch;

	public static float GAMSE_SPEED = 500;

	enum GameState {
		Nothing, Play, MakingPath
	};
	private GameState state;

	public void create() {
        Gdx.app.log( GridPrototipe.LOG, "Creating game" );
        fpsLogger = new FPSLogger();
        setScreen( new GameScreen(this));
	}


	@Override
	public void dispose() {
        super.dispose();
	}

	@Override
	public void render() {
        super.render();

        // output the current FPS
        fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
        super.resize( width, height );
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
