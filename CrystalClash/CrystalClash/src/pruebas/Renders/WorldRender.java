package pruebas.Renders;

import java.util.Iterator;

import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Unit;
import pruebas.Renders.helpers.CellHelper;
import pruebas.Renders.helpers.UnitHelper;
import pruebas.Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class WorldRender implements InputProcessor {

	private GameEngine engine;
	public static CellHelper cellHelper;

	private Texture backgroundTexture;
	private Image background;

	private Texture archerTexture;
	private Image archer;

	private WorldController world;
	GameRender gameRender;

	// private SuperAnimation animation;

	public WorldRender(GameEngine engine, WorldController world) {
		this.engine = engine;
		this.world = world;

		cellHelper = new CellHelper();
		cellHelper.load();

		UnitHelper.init();

		backgroundTexture = new Texture(
				Gdx.files.internal("data/Images/InGame/terrain.jpg"));
		background = new Image(backgroundTexture);
		background.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);

		gameRender = new SelectUnitsRender(engine, world, 2);
	}

	public void render(float dt, SpriteBatch batch, Stage stage) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		background.draw(batch, 1);
		for (int i = 0; i < world.cellGrid.length; i++) {
			for (int j = world.cellGrid[i].length - 1; j >= 0; j--) {
				world.cellGrid[i][j].getRender().draw(dt, batch);
			}
		}

		for (int i = 0; i < world.cellGrid.length; i++) {
			for (int j = world.cellGrid[i].length - 1; j >= 0; j--) {
				world.cellGrid[i][j].getRender().drawUnits(dt, batch);
			}
		}

		gameRender.render(dt, batch);
	}

	public void dispose() {
		backgroundTexture.dispose();
		archerTexture.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 vec = engine.getRealPosition(screenX, screenY);
		gameRender.touchDown(vec.x, vec.y, pointer, button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector2 vec = engine.getRealPosition(screenX, screenY);
		gameRender.touchUp(vec.x, vec.y, pointer, button);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector2 vec = engine.getRealPosition(screenX, screenY);
		gameRender.touchDragged(vec.x, vec.y, pointer);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
