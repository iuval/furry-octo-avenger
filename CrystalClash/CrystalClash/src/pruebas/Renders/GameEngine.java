package pruebas.Renders;

import pruebas.Controllers.MenuMaster;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.World;
import pruebas.Enumerators.GameState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameEngine implements Screen {

	private InputMultiplexer inputManager;
	private SpriteBatch batch;
	private Stage stage;
	private OrthographicCamera camera;

	private GameState state;

	private MenuMaster menu;
	private MenuMasterRender menuRender;
	private World world;
	private WorldRender worldRender;

	@Override
	public void show() {
		inputManager = new InputMultiplexer();
		batch = new SpriteBatch();
		stage = new Stage();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		batch.setProjectionMatrix(camera.combined);
		stage.setCamera(camera);

		state = GameState.InMenu;

		menu = MenuMaster.getInstance();
		menuRender = new MenuMasterRender(this, menu);
		world = new World();
		worldRender = new WorldRender(this, world);

		inputManager.addProcessor(stage);
		inputManager.addProcessor(menu.getCurrentMenu().getRender());
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputManager);
		// Gdx.input.setInputProcessor(stage);
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

		switch (state) {
		case InGame:
			batch.begin();
			worldRender.render(dt, batch);
			batch.end();
			break;
		case InMenu:
			menuRender.render(dt, stage);
			stage.act(dt);
			stage.draw();
			break;
		default:
			break;
		}
	}

	public void setState(GameState state) {
		this.state = state;
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
}
