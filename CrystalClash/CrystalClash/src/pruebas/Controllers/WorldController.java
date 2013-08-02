package pruebas.Controllers;

import java.util.ArrayList;

import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Renders.UnitRender;
import pruebas.Renders.helpers.CellHelper;

import com.badlogic.gdx.utils.Array;

public class WorldController {
	public Cell[][] cellGrid;
	private final float deltaX = 122.0F;
	private final float gridX = 70.0F;
	private final float gridY = 20.0F;

	private Array<Unit> p1Units = new Array<Unit>();
	private Array<Unit> p2Units = new Array<Unit>();

	public WorldController() {
		// this.p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(
		// 40.0F, 95.0F)));
		// this.p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(
		// 285.0F, 315.0F)));
		// this.p1Units.add(new Tuple<Unit, Vector2>(new Archer(), new Vector2(
		// 410.0F, 40.0F)));
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
		float dy = CellHelper.CELL_HEIGHT + 1;// (float) ((Math.sqrt(3f) / 2f) *
												// hexaWidht);

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
		p1Units = new Array<Unit>();
		p2Units = new Array<Unit>();
	}

	public void addUnit(int player, Unit unit, int x, int y) {
		Cell cell = cellAt(x, y);
		cell.placeUnit(unit, player);
		// if (player == 1) {
		// this.p1Units.add(new Tuple<Unit, Vector2>(unit, position));
		// } else {
		// this.p2Units.add(new Tuple<Unit, Vector2>(unit, position));
		// }
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

	public Array<Unit> getP1Units() {
		return this.p1Units;
	}

	public Array<Unit> getP2Units() {
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

	public void placeUnit(float x, float y) {
		Cell cell = cellAt(x, y);
		if (cell != null && cell.getState() == Cell.State.ABLE_TO_PLACE) {
			Unit fireArcher = new Unit("fire_archer");
			cell.placeUnit(fireArcher, 1);
		}
	}

	public void update(float paramFloat) {
	}

	public void assignFirstTurnAvailablePlaces(int player) {
		if (player == 1) {
			cellGrid[0][5].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[0][4].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[0][3].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[0][2].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[0][1].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[0][0].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[1][5].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[1][4].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[1][3].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[1][3].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[1][2].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[1][1].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[2][1].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[2][2].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[2][3].setState(Cell.State.ABLE_TO_PLACE);
			cellGrid[2][4].setState(Cell.State.ABLE_TO_PLACE);
		} else {

		}
	}

}