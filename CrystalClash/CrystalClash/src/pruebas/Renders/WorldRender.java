package pruebas.Renders;

import java.util.Iterator;

import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Unit;
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
	private CellRender cellRender;

	private Texture backgroundTexture;
	private Image background;

	private Texture archerTexture;
	private Image archer;

	private WorldController world;
	private Array<Tuple<Unit, Vector2>> p1Units;
	private Array<Tuple<Unit, Vector2>> p2Units;
	private Iterator<Tuple<Unit, Vector2>> unitIterator;
	private Tuple<Unit, Vector2> aux;

	SelectUnitsRender firstTurn;

	// private SuperAnimation animation;

	public WorldRender(GameEngine engine, WorldController world) {
		this.engine = engine;
		this.world = world;

		cellRender = new CellRender();
		cellRender.load();

		backgroundTexture = new Texture(
				Gdx.files.internal("data/Images/InGame/terrain.jpg"));
		background = new Image(backgroundTexture);
		background.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);

		// archerTexture = new Texture(
		// Gdx.files.internal("data/Images/Units/fire_archer_f.png"));
		// archer = new Image(archerTexture);
		// archer.setSize(150, 150);
		//
		//
		firstTurn = new SelectUnitsRender(engine);
	}

	public void render(float dt, SpriteBatch batch, Stage stage) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		p1Units = world.getP1Units();
		p2Units = world.getP2Units();

		background.draw(batch, 1);
		for (int i = 0; i < world.cellGrid.length; i++) {
			for (int j = 0; j < world.cellGrid[i].length; j++) {
				batch.draw(cellRender.getCellTexture(world.cellGrid[i][j]),
						world.cellGrid[i][j].getX(),
						world.cellGrid[i][j].getY());
			}
		}
		firstTurn.render(dt, batch, stage);
		// unitIterator = p1Units.iterator();
		// while (unitIterator.hasNext()) {
		// aux = unitIterator.next();
		// archer.setPosition(aux.getSecond().x, aux.getSecond().y);
		// archer.draw(batch, 1);
		// // stage.addActor(archer);
		// }
		//
		// unitIterator = p2Units.iterator();
		// while (unitIterator.hasNext()) {
		// aux = unitIterator.next();
		// archer.setPosition(aux.getSecond().x, aux.getSecond().y);
		// archer.draw(batch, 1);
		// // stage.addActor(archer);
		// }

		// TextureRegion currentFrame = animation.getKeyFrame(dt, true); // #16
		// batch.draw(currentFrame, 50, 50); // #17

		// batch.draw(cellRender.able_to_attack, 100, 100);
	}

	public void dispose() {
		backgroundTexture.dispose();
		archerTexture.dispose();
	}

	private void load() {
		cellRender.load();
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
		world.tap(vec.x, vec.y);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
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
