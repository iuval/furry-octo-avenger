package pruebas.Entities;

import pruebas.Renders.CellRender;
import pruebas.Renders.helpers.CellHelper;

public class Cell extends GameObject {
	public enum State {
		NONE, ABLE_TO_ATTACK, ABLE_TO_MOVE, ABLE_TO_PLACE, ATTACK_TARGET_CENTER, ATTACK_TARGET_RADIUS, MOVE_TARGET
	}

	private Unit unitsPlayer1;
	private Unit unitsPlayer2;
	private State state = State.NONE;
	public int[][] neigbours;
	private CellRender render;

	public Cell() {
		render = new CellRender(this);
	}

	public CellRender getRender() {
		return render;
	}

	public State getState() {
		return state;
	}

	public void setState(State s) {
		state = s;
	}

	public void placeUnit(Unit unit, int player) {
		if (player == 1) {
			unitsPlayer1 = unit;
			unit.setPosition(getX(), getY());
		} else {
			unitsPlayer2 = unit;
			unit.setPosition(getX() + CellHelper.CELL_WIDTH / 2, getY());
		}
	}

	public Unit getUnit(int player) {
		if (player == 1) {
			return unitsPlayer1;
		} else {
			return unitsPlayer2;
		}
	}
}
