package pruebas.Controllers;

import java.util.ArrayList;

import pruebas.Entities.Archer;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Util.Tuple;

import com.badlogic.gdx.math.Vector2;

public class WorldController {
	public Cell[][] cellGrid;
	private final float deltaX = 122.0F;
	private final float gridX = 70.0F;
	private final float gridY = 20.0F;
	private final float hexaHeight = 109.0F;
	private final float hexaWidth = 162.0F;
	private com.badlogic.gdx.utils.Array<Tuple<Unit, Vector2>> p1Units = new com.badlogic.gdx.utils.Array<Tuple<Unit, Vector2>>();
	private com.badlogic.gdx.utils.Array<Tuple<Unit, Vector2>> p2Units = new com.badlogic.gdx.utils.Array<Tuple<Unit, Vector2>>();

	public WorldController() {
		this.p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(
				40.0F, 95.0F)));
		this.p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(
				285.0F, 315.0F)));
		this.p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(
				410.0F, 40.0F)));
		Initialize();
		createMap();
	}

	private void createMap() {
		/*
		 * x x x o x x x
		 */
		int[][] oddNeighbours = { { 0, -1 }, { -1, 1 }, { 1, 0 }, { 1, 1 },
				{ 0, 1 }, { -1, 0 } };

		/*
		 * x x x o x x x
		 */
		int[][] evenNeighbours = { { -1, -1 }, { 0, -1 }, { 1, 0 }, { 0, 1 },
				{ 1, -1 }, { -1, 0 } };

		float yoffset = 0f;
		float dx = deltaX;// (float) ((3f / 4f) * hexaWidht);
		float dy = hexaHeight + 1;// (float) ((Math.sqrt(3f) / 2f) * hexaWidht);

		ArrayList<int[]> temp = new ArrayList<int[]>();
		for (int h = 0; h < 6; h++) {
			for (int v = 0; v < 9; v++) {
				Cell c = new Cell();
				c.setVisible(true);
				temp.clear();

				if (v % 2 == 0) {
					yoffset = dy / 2;

					for (int i = 0; i < 6; i++) {
						if (inMap(v + oddNeighbours[i][0], h
								+ oddNeighbours[i][1])) {
							temp.add(new int[] { v + oddNeighbours[i][0],
									h + oddNeighbours[i][1] });
						}
					}
				} else {
					yoffset = 0;

					for (int i = 0; i < 6; i++) {
						if (inMap(v + evenNeighbours[i][0], h
								+ evenNeighbours[i][1])) {
							temp.add(new int[] { v + evenNeighbours[i][0],
									h + evenNeighbours[i][1] });
						}
					}
				}
				c.neigbours = new int[temp.size()][2];
				for (int i = 0; i < temp.size(); i++) {
					c.neigbours[i] = temp.get(i);
				}
				c.setPosition(gridX + v * dx, gridY + yoffset + (h * dy));
				c.setGrisPosition(v, h);

				cellGrid[v][h] = c;
			}

		}
	}

	protected void Initialize() {
		this.cellGrid = ((Cell[][]) java.lang.reflect.Array.newInstance(
				Cell.class, new int[] { 9, 6 }));
	}

	public void addUnit(int paramInt, Unit paramUnit, Vector2 paramVector2) {
		if (paramInt == 1) {
			this.p1Units.add(new Tuple<Unit, Vector2>(paramUnit, paramVector2));
			return;
		}
		this.p2Units.add(new Tuple<Unit, Vector2>(paramUnit, paramVector2));
	}

	public Cell cellAt(float x, float y) {
		int cellX = 0, cellY = 0;
		Cell cell = this.cellGrid[cellX][cellY];

		while (cell.getX() < x && ++cellX < 9) {
			cell = this.cellGrid[cellX][cellY];
		}
		if (!inMap(--cellX, cellY))
			return null;

		cell = this.cellGrid[cellX][cellY];
		while (cell.getY() < y && ++cellY < 6) {
			cell = this.cellGrid[cellX][cellY];
		}
		if (!inMap(cellX, --cellY))
			return null;

		return this.cellGrid[cellX][cellY];
	}

	public com.badlogic.gdx.utils.Array<Tuple<Unit, Vector2>> getP1Units() {
		return this.p1Units;
	}

	public com.badlogic.gdx.utils.Array<Tuple<Unit, Vector2>> getP2Units() {
		return this.p2Units;
	}

	public boolean inMap(int x, int y) {
		return (x >= 0) && (x < 9) && (y >= 0) && (y < 6);
	}

	public void tap(float x, float y) {
		Cell cell = cellAt(x, y);
		if (cell != null)
			cell.setState(Cell.State.MOVE_TARGET);
	}

	public void update(float paramFloat) {
	}
}