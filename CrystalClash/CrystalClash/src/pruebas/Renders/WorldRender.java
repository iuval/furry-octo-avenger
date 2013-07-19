package pruebas.Renders;

import java.util.Iterator;

import pruebas.CrystalClash.CrystalClash;
import pruebas.CrystalClash.GameEngine;
import pruebas.Entities.Unit;
import pruebas.Entities.World;
import pruebasUtil.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	public WorldRender(GameEngine engine, World world) {
		this.engine = engine;
		this.world = world;
		
		backgroundTexture = new Texture(Gdx.files.internal("data/Images/InGame/terrain_distribution_huds.jpg"));
		background = new Image(backgroundTexture);
		background.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);

		archerTexture = new Texture(Gdx.files.internal("data/Images/Units/fire_archer_f.png"));
		archer = new Image(archerTexture);
		archer.setSize(150, 150);
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
			archer.setPosition(aux.value.x, aux.value.y);
			archer.draw(batch, 1);
			//stage.addActor(archer);
		}

		unitIterator = p2Units.iterator();
		while (unitIterator.hasNext()) {
			aux = unitIterator.next();
			archer.setPosition(aux.value.x, aux.value.y);
			archer.draw(batch, 1);
			//stage.addActor(archer);
		}
	}
	
	public void dispose(){
		backgroundTexture.dispose();
		archerTexture.dispose();
	}
}
