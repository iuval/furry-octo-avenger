package pruebas.Renders;

import pruebas.Controllers.MenuMaster;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Enumerators.GameState;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.ui.SuperAnimatedActor;
import pruebas.Util.FileUtil;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

	private GameState state;

	private MenuMaster menu;
	private MenuMasterRender menuRender;
	private WorldController world;
	private WorldRender worldRender;

	private static TweenManager tweenManager;
	private static SuperAnimatedActor loadingTexture;
	private static boolean loading = false;

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
		openMenu();
	}

	private void load() {
		loadingTexture = new SuperAnimatedActor(FileUtil.getSuperAnimation("data/Images/Menu/Loading/loading"), true, FACING.right);
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
		case InMenu:
			menu.update(dt);
			break;
		default:
			break;
		}
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
		case InMenu:
			menuRender.update(dt, stage);
			break;
		default:
			break;
		}
		
		tweenManager.update(dt);
		stage.act(dt);
		stage.draw();
		if (loading) {
			stage.addActor(loadingTexture);
			loadingTexture.act(dt);
			batch.begin();
			loadingTexture.draw(batch, 1);
			batch.end();
		}
	}

	private void setState(GameState state) {
		this.state = state;
		inputManager.clear();
		stage.clear();
		switch (state) {
		case InGame:
			inputManager.addProcessor(stage);
			inputManager.addProcessor(worldRender);
			break;
		case InMenu:
			inputManager.addProcessor(stage);
			inputManager.addProcessor(menu.getCurrentMenu().getRender());
			break;
		default:
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

	public void openMenu() {
		if (menuRender == null) {
			menu = MenuMaster.getInstance();
			menuRender = new MenuMasterRender(menu);
		}
		if (worldRender != null) {
			worldRender.dispose();
			worldRender = null;
		}
		setState(GameState.InMenu);
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
		menuRender.dispose();
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
