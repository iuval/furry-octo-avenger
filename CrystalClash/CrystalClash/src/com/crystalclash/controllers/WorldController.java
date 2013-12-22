package com.crystalclash.controllers;

import java.util.ArrayList;

import com.badlogic.gdx.utils.JsonValue;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;
import com.crystalclash.entities.helpers.AttackUnitAction;
import com.crystalclash.entities.helpers.DefendUnitAction;
import com.crystalclash.entities.helpers.MoveUnitAction;
import com.crystalclash.entities.helpers.NoneUnitAction;
import com.crystalclash.entities.helpers.PlaceUnitAction;
import com.crystalclash.entities.helpers.UnitAction;
import com.crystalclash.networking.ServerDriver;
import com.crystalclash.renders.GameEngine;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.UnitRender.STATE;
import com.crystalclash.renders.helpers.CellHelper;
import com.crystalclash.views.WorldView;

public class WorldController {

	private WorldView render;

	public Cell[][] cellGrid;
	public static final float deltaY = 105F;
	public static final float deltaX = CellHelper.CELL_WIDTH + 3;
	private final float cellsLeft = 134.0F;
	private final float cellsBot = 100.0F;
	private final float cellsRight = cellsLeft + 1005;
	private final float cellsTop = cellsBot + 660;

	public final int gridW = 8;
	public final int gridH = 6;

	public int player;
	private String gameId;
	public int gameTurn;
	public int enemiesCount;
	public int allysCount;
	public boolean gameEnded = false;

	public WorldController(JsonValue data) {
		render = new WorldView(this);
		render.load();
		init();

		if (data != null) {
			this.player = data.getInt("player");
			this.gameId = data.getString("game_id");

			if (data.get("data") != null && data.get("data").asString().equals("none")) {
				render.initFirstTurn();
			} else {
				readData(data);
				render.initTurnAnimations();
			}
		} else {
			render.initTutorial();
		}
	}

	private void readData(JsonValue values) {
		enemiesCount = 0;
		allysCount = 0;
		gameTurn = values.getInt("turn");
		readPlayerData(values.get("data1"), 1);
		readPlayerData(values.get("data2"), 2);
	}

	private void readPlayerData(JsonValue values, int playerNum) {
		JsonValue child;
		JsonValue temp;
		String action;
		int x, y;
		String strValues = values.asString();
		if (strValues.equals("ended")) {
			gameEnded = true;
		} else {
			values = ServerDriver.parseJson(strValues);
			boolean isEnemy = player != playerNum;

			for (int i = 0; i < values.size; i++) {
				child = values.get(i);

				temp = child.get("cell");
				x = temp.getInt("x");
				y = temp.getInt("y");

				Unit unit = new Unit(child.getString("unit_name"), playerNum, isEnemy, child.getInt("unit_hp"));

				UnitAction unitA;
				action = child.getString("action");

				if (action.equals("place")) {
					unitA = new PlaceUnitAction();
					((PlaceUnitAction) unitA).unitName = child.getString("unit_name");
				} else if (action.equals("attack")) {
					unitA = new AttackUnitAction(unit.getRange() == 1);
					JsonValue cells = child.get("target");
					int cellX = cells.getInt("x");
					int cellY = cells.getInt("y");

					((AttackUnitAction) unitA).target = cellGrid[cellX][cellY];
				} else if (action.equals("move")) {
					unitA = new MoveUnitAction();
					JsonValue cells = child.get("target");
					JsonValue pair = null;
					for (int c = 0; c < cells.size; c++) {
						pair = cells.get(c);
						int cellX = pair.getInt("x");
						int cellY = pair.getInt("y");

						((MoveUnitAction) unitA).moves.add(cellGrid[cellX][cellY]);
					}
				} else if (action.equals("defend")) {
					unitA = new DefendUnitAction();
				} else {
					unitA = new NoneUnitAction();
				}

				cellGrid[x][y].setUnit(unit);
				cellGrid[x][y].setAction(unitA);

				if (playerNum == player)
					allysCount++;
				else
					enemiesCount++;
			}
		}
	}

	private void createMap() {
		int[][] oddNeighbours = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, -1 } };
		int[][] evenNeighbours = { { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, { 0, -1 }, { 1, 0 } };

		float xoffset = 0f;

		ArrayList<int[]> temp = new ArrayList<int[]>();
		for (int h = 0; h < gridH; h++) {
			for (int v = 0; v < gridW; v++) {
				if (h == 5) {
					int kk = 0;
					System.out.print(kk);
				}
				Cell c = new Cell();
				c.setVisible(true);
				temp.clear();

				if (h % 2 == 0) {
					xoffset = deltaX / 2;

					for (int i = 0; i < oddNeighbours.length; i++) {
						if (inCellsGrid(v + oddNeighbours[i][0], h + oddNeighbours[i][1])) {
							temp.add(new int[] { v + oddNeighbours[i][0], h + oddNeighbours[i][1] });
						}
					}
				} else {
					xoffset = 0;

					for (int i = 0; i < evenNeighbours.length; i++) {
						if (inCellsGrid(v + evenNeighbours[i][0], h + evenNeighbours[i][1])) {
							temp.add(new int[] { v + evenNeighbours[i][0], h + evenNeighbours[i][1] });
						}
					}
				}
				c.neigbours = new int[temp.size()][2];
				for (int i = 0; i < temp.size(); i++) {
					c.neigbours[i] = temp.get(i);
				}
				c.setPosition(cellsLeft + xoffset + (v * deltaX), cellsBot + (h * deltaY));
				c.setGrisPosition(v, h);

				cellGrid[v][h] = c;
			}

		}
	}

	protected void init() {
		this.cellGrid = new Cell[gridW][gridH];

		createMap();
	}

	public boolean addUnit(Unit unit, int x, int y) {
		Cell cell = cellAt(x, y);
		if (cell != null) {
			cell.placeUnit(unit);
			return true;
		}
		return false;
	}

	public Cell cellAt(float x, float y) {
		if (inCells(x, y)) {
			int cellX = 0, cellY = 0;
			Cell cell = this.cellGrid[cellX][cellY];

			while (cell.getY() < y && ++cellY < gridH) {
				cell = this.cellGrid[cellX][cellY];
			}
			if (!inCellsGrid(cellX, --cellY))
				return null;

			cell = this.cellGrid[cellX][cellY];
			while (cell.getX() < x && ++cellX < gridW) {
				cell = this.cellGrid[cellX][cellY];
			}
			if (!inCellsGrid(--cellX, cellY))
				return null;

			return this.cellGrid[cellX][cellY];
		}
		return null;
	}

	public boolean inCellsGrid(int x, int y) {
		return (x >= 0) && (x < gridW) && (y >= 0) && (y < gridH);
	}

	public boolean inCells(float x, float y) {
		return (x >= cellsLeft) && (x < cellsRight) && (y >= cellsBot) && (y < cellsTop);
	}

	public boolean placeUnit(float x, float y, Unit unit) {
		unit.setPlayerNumber(player);
		Cell cell = cellAt(x, y);
		if (cell != null && cell.hasState(Cell.ABLE_TO_PLACE) && cell.getUnit() == null) {
			cell.placeUnit(unit);
			return true;
		}
		return false;
	}

	public void update(float paramFloat) {
	}

	// -------------Para poder poner una unidad para probar
	public void setCellState(float x, float y, int state) {
		Cell cell = cellAt(x, y);
		if (cell != null) {
			cell.state = state;
		}
	}

	public Cell cellAtByGrid(int x, int y) {
		return cellGrid[x][y];
	}

	public void assignFirstTurnAvailablePlaces() {
		int xIni = player == 1 ? 0 : gridW - 3;
		int xEnd = player == 1 ? 3 : gridW;
		for (int i = xIni; i < xEnd; i++) {
			for (int j = 0; j < gridH; j++) {
				cellGrid[i][j].state = Cell.ABLE_TO_PLACE;
			}
		}
	}

	public void deleteAllUnits() {
		for (int h = 0; h < gridH; h++) {
			for (int v = 0; v < gridW; v++) {
				cellGrid[v][h].removeUnit();
			}
		}
	}

	public void removeAllDeadUnits() {
		for (int h = 0; h < gridH; h++) {
			for (int v = 0; v < gridW; v++) {
				cellGrid[v][h].removeDeadUnits();
			}
		}
	}

	public void sendTurn() {
		GameEngine.showLoading();
		String data = null;
		String result = null;
		if (gameEnded) {
			if (enemiesCount == 0 && allysCount > 0)
				result = "victory";
			else if (enemiesCount > 0 && allysCount == 0)
				result = "defeat";
			else if (enemiesCount == 0 && allysCount == 0)
				result = "draw";
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("{");

			Cell cell;
			Unit unit;
			for (int h = 0; h < gridH; h++) {
				for (int v = 0; v < gridW; v++) {
					cell = cellGrid[v][h];
					unit = cellGrid[v][h].getUnit();
					if (unit != null && unit.getRender().getState() != STATE.ghost && !unit.isEnemy())
						cell.addDataToJson(builder);
				}
			}
			// Delete the last comma
			builder.deleteCharAt(builder.length() - 1);

			builder.append("}");
			data = builder.toString();
		}

		ServerDriver.sendGameTurn(GameController.getUser().getId(),
				gameId, data, result);
	}

	public void surrenderCurrentGame() {
		ServerDriver.sendGameTurn(GameController.getUser().getId(),
				gameId, "ended", "defeat");
	}

	public WorldView getRender() {
		return render;
	}

	public void initNormalTurn() {
		render.initNormalTurn();
	}

	public void leaveGame() {
		GameEngine.getInstance().openMenuGames();
	}
}