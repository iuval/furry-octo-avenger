package pruebas.Renders;

import java.util.Iterator;

import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Unit;
import pruebas.Entities.World;
import pruebas.Util.SuperAnimation;
import pruebas.Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class WorldRender {

	private GameEngine engine;
	
	private Texture backgroundTexture;
	private Image background;

	private Texture archerTexture;
	private Image archer;

	private World world;
	private Array<Tuple<Unit, Vector2>> p1Units;
	private Array<Tuple<Unit, Vector2>> p2Units;
	private Iterator<Tuple<Unit, Vector2>> unitIterator;
	private Tuple<Unit, Vector2> aux;


	private SuperAnimation animation;
	
	public WorldRender(GameEngine engine, World world) {
		this.engine = engine;
		this.world = world;
		
		backgroundTexture = new Texture(Gdx.files.internal("data/Images/InGame/terrain.jpg"));
		background = new Image(backgroundTexture);
		background.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);

		archerTexture = new Texture(Gdx.files.internal("data/Images/Units/fire_archer_f.png"));
		archer = new Image(archerTexture);
		archer.setSize(150, 150);

		int FRAME_COLS = 6;
		int FRAME_ROWS = 1;
		Texture walkSheet = new Texture(
				Gdx.files.internal("data/Units/fire_archer_attack_sheet.png"));
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS, walkSheet.getHeight()
						/ FRAME_ROWS);
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		float[] times = new float[FRAME_COLS * FRAME_ROWS];
		times[0] = 0.15f;
		times[1] = 0.15f;
		times[2] = 0.5f;
		times[3] = 0.15f;
		times[4] = 0.15f;
		times[5] = 0.15f;
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		animation = new SuperAnimation(times, walkFrames);

	}

	public void render(float dt, SpriteBatch batch) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		p1Units = world.getP1Units();
		p2Units = world.getP2Units();
		
		background.draw(batch, 1);
		//stage.addActor(background);
		
		unitIterator = p1Units.iterator();
		while (unitIterator.hasNext()) {
			aux = unitIterator.next();
			archer.setPosition(aux.getSecond().x, aux.getSecond().y);
			archer.draw(batch, 1);
			//stage.addActor(archer);
		}

		unitIterator = p2Units.iterator();
		while (unitIterator.hasNext()) {
			aux = unitIterator.next();
			archer.setPosition(aux.getSecond().x, aux.getSecond().y);
			archer.draw(batch, 1);
			//stage.addActor(archer);
		}

		TextureRegion currentFrame = animation.getKeyFrame(dt, true); // #16
		batch.draw(currentFrame, 50, 50); // #17
	}
	
	public void dispose(){
		backgroundTexture.dispose();
		archerTexture.dispose();
	}
}
