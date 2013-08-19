package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.MenuGames;
import pruebas.Controllers.MenuLogIn;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Enumerators.GameState;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.ui.SuperAnimatedActor;
import pruebas.Util.FileUtil;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.JsonValue;

public class GameEngine implements Screen {
	private static GameEngine instance;

	public static GameEngine getInstance() {
		if (instance == null)
			instance = new GameEngine();
		return instance;
	}

	private InputMultiplexer inputManager;
	private SpriteBatch batch;
	private Stage stage;
	private static OrthographicCamera camera;

	private GameState state = GameState.InMenuLogIn;

	private MenuLogInRender menuLogInRender;
	private MenuGamesRender menuGamesRender;
	private WorldController world;
	private WorldRender worldRender;

	private static TweenManager tweenManager;
	private static SuperAnimatedActor loadingTexture;

	private Image background;

	@Override
	public void show() {
		inputManager = new InputMultiplexer();
		batch = new SpriteBatch();
		stage = new Stage();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		batch.setProjectionMatrix(camera.combined);
		stage.setCamera(camera);

		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputManager);

		tweenManager = new TweenManager();

		load();
		openMenuLogIn();
		hideLoading();
	}

	private void load() {
		loadingTexture = new SuperAnimatedActor(FileUtil.getSuperAnimation("data/Images/Menu/Loading/loading"), true, FACING.right);

		Texture backgroundTexture = new Texture(
				Gdx.files.internal("data/Images/Menu/menu_background.jpg"));
		background = new Image(backgroundTexture);

		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Timeline.createSequence()
				.push(Tween.set(background, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(background, ActorAccessor.ALPHA, 2).target(1))
				.start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
	}

	@Override
	public void render(float delta) {
		updateGame(delta);
		renderGame(delta);
	}

	private void updateGame(float dt) {
		switch (state) {
		case InGame:
			world.update(dt);
			break;
		}
		tweenManager.update(dt);
		stage.act(dt);
	}

	private void renderGame(float dt) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (state == GameState.InGame || state == GameState.InTranstionMenuGamesAndGame) {
			batch.begin();
			worldRender.render(dt, batch, stage);
			batch.end();
		}
		stage.draw();
	}

	private void setState(GameState newState) {
		state = newState;
		inputManager.clear();
		stage.clear();
		stage.addActor(background);
		inputManager.addProcessor(stage);
		switch (newState) {
		case InGame:
			inputManager.addProcessor(worldRender);
			break;
		case InMenuLogIn:
			stage.addActor(menuLogInRender);
			break;
		case InMenuGames:
			stage.addActor(background);
			stage.addActor(menuGamesRender);
			if (worldRender != null) {
				worldRender.dispose();
				worldRender = null;
			}
			break;
		case InTranstionMenuLogInAndMenuGames:
			stage.addActor(menuLogInRender);
			stage.addActor(menuGamesRender);
			break;
		case InTranstionMenuGamesAndGame:
			stage.addActor(menuGamesRender);
			break;
		}
		stage.addActor(loadingTexture);
	}

	public void openGame(final JsonValue data, final int turn) {
		Timeline t = Timeline.createSequence();
		menuGamesRender.pushExitAnimation(t);
		t.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (worldRender == null) {
					world = new WorldController(data, turn);
					worldRender = world.getRender();
				}
				setState(GameState.InTranstionMenuGamesAndGame);
				Timeline.createSequence()
						.push(Tween.to(background, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED).target(0))
						.setCallback(new TweenCallback() {
							@Override
							public void onEvent(int type, BaseTween<?> source) {
								setState(GameState.InGame);
							};
						}).start(tweenManager);
			};
		});
		t.start(tweenManager);
	}

	public void openMenuLogIn() {
		if (menuLogInRender == null) {
			menuLogInRender = MenuLogIn.getInstance().getRender();
		}

		if (state == GameState.InMenuGames) {
			setState(GameState.InTranstionMenuLogInAndMenuGames);
			Timeline t = Timeline.createSequence();
			menuGamesRender.pushExitAnimation(t);
			menuLogInRender.pushEnterAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					setState(GameState.InMenuLogIn);
				}
			});
			t.start(tweenManager);
		} else {
			setState(GameState.InMenuLogIn);
		}
	}

	public void openMenuGames() {
		Timeline t = Timeline.createSequence();
		if (state == GameState.InMenuLogIn) {
			menuLogInRender.pushExitAnimation(t);
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
					}
					MenuGames.getInstance().listGames();
					setState(GameState.InTranstionMenuLogInAndMenuGames);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
								}
							}).start(tweenManager);
				}
			});
		} else if (state == GameState.InGame) {
			t.push(Tween.set(background, ActorAccessor.ALPHA).target(0));
			t.push(Tween.to(background, ActorAccessor.ALPHA, CrystalClash.ANIMATION_SPEED).target(1));
			t.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (menuGamesRender == null) {
						menuGamesRender = MenuGames.getInstance().getRender();
					}
					MenuGames.getInstance().listGames();
					setState(GameState.InTranstionMenuGamesAndGame);
					menuGamesRender.pushEnterAnimation(Timeline.createSequence())
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									setState(GameState.InMenuGames);
								}
							}).start(tweenManager);
				}
			});
		}
		t.start(tweenManager);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
		menuLogInRender.dispose();
		worldRender.dispose();
	}

	public static Vector2 getRealPosition(float x, float y) {
		Vector3 vec = new Vector3(x, y, 0);
		camera.unproject(vec);
		return new Vector2(vec.x, vec.y);
	}

	public static void showLoading() {
		loadingTexture.setVisible(true);
	}

	public static void hideLoading() {
		loadingTexture.setVisible(false);
	}
}
