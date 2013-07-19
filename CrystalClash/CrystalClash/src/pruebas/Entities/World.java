package pruebas.Entities;

import pruebasUtil.Tuple;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class World {

	private Array<Tuple<Unit, Vector2>> p1Units;
	private Array<Tuple<Unit, Vector2>> p2Units;

	public World() {
		p1Units = new Array<Tuple<Unit, Vector2>>();
		p2Units = new Array<Tuple<Unit, Vector2>>();
		
		//--------Prueba--------
		p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(40,95)));
		p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(285,315)));
		p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(410,40)));
	}

	public void update(float delta) {
	}

	public void addUnit(int player, Unit u, Vector2 v) {
		if (player == 1)
			p1Units.add(new Tuple<Unit, Vector2>(u, v));
		else
			p2Units.add(new Tuple<Unit, Vector2>(u, v));
	}

	public Array<Tuple<Unit, Vector2>> getP1Units() {
		return p1Units;
	}

	public Array<Tuple<Unit, Vector2>> getP2Units() {
		return p2Units;
	}
}
