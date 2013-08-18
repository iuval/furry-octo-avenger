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
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
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
	private static boolean loading = false;

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
	}

	private void load() {
		loadingTexture = new SuperAnimatedActor(FileUtil.getSuperAnimation("data/Images/Menu/Loading/loading"), true, FACING.right);
		stage.addActor(loadingTexture);

		Texture backgroundTexture = new Texture(
				Gdx.files.internal("data/Images/Menu/menu_background.jpg"));
		background = new Image(backgroundTexture);

		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Timeline.createParallel()
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
		case InMenuLogIn:
			menuLogInRender.update(dt);
			break;
		case InMenuGames:
			menuGamesRender.update(dt);
			break;
		default:
			break;
		}
		tweenManager.update(dt);
	}

	private void renderGame(float dt) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		switch (state) {
		case InGame:
			batch.begin();
			worldRender.render(dt, batch, stage);
			batch.end();
			break;
		case InMenuLogIn:
			menuLogInRender.update(dt);
			break;
		case InMenuGames:
			menuGamesRender.update(dt);
			break;
		default:
			break;
		}

		tweenManager.update(dt);
		stage.act(dt);
		stage.draw();
		if (loading) {
			loadingTexture.act(dt);
			batch.begin();
			loadingTexture.draw(batch, 1);
			batch.end();
		}
	}

	private void setState(GameState newState) {
		inputManager.clear();
		stage.clear();
		inputManager.addProcessor(stage);
		this.state = newState;
		switch (newState) {
		case InGame:
			inputManager.addProcessor(worldRender);
			break;
		case InMenuLogIn:
			stage.addActor(background);
			stage.addActor(menuLogInRender);
			menuLogInRender.enterAnimation();
			break;
		case InMenuGames:
			stage.addActor(background);
			stage.addActor(menuGamesRender);
			menuGamesRender.enterAnimation();
			break;
		}

	}

	public void openGame(JsonValue data, int turn) {
		if (worldRender == null) {
			world = new WorldController(data, turn);
			worldRender = world.getRender();
		}
		// TODO: borrar el menu cuando entras al juego
		// if (menuRender != null) {
		// menuRender.dispose();
		// menuRender = null;
		// }
		setState(GameState.InGame);
	}

	public void openMenuLogIn() {
		if (menuLogInRender == null) {
			menuLogInRender = MenuLogIn.getInstance().getRender();
		}
		if (worldRender != null) {
			worldRender.dispose();
			worldRender = null;
		}

		switch (state) {
		case InMenuGames:
			menuGamesRender.exitAnimation();
			break;
		}
		setState(GameState.InMenuLogIn);
	}

	public void openMenuGames() {
		if (menuGamesRender == null) {
			menuGamesRender = MenuGames.getInstance().getRender();
		}
		if (worldRender != null) {
			worldRender.dispose();
			worldRender = null;
		}
		switch (state) {
		case InMenuGames:
			menuLogInRender.exitAnimation();
			break;
		}
		setState(GameState.InMenuGames);
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
		loading = true;
	}

	public static void hideLoading() {
		loading = false;
	}
}
