package pruebas.Entities;

import pruebas.Entities.helpers.PlaceUnitAction;
import pruebas.Entities.helpers.UnitAction;
import pruebas.Renders.CellRender;
import pruebas.Renders.UnitRender.FACING;

public class Cell extends GameObject {
	public enum State {
		NONE, ABLE_TO_ATTACK, ABLE_TO_MOVE, ABLE_TO_PLACE, ATTACK_TARGET_CENTER, ATTACK_TARGET_RADIUS, MOVE_TARGET
	}

	private Unit unitsPlayer1;
	private Unit unitsPlayer2;
	private UnitAction actionPlayer1;
	private UnitAction actionPlayer2;
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
		setUnit(unit, player);
		if (player == 1) {
			actionPlayer1 = new PlaceUnitAction();
			((PlaceUnitAction) actionPlayer1).origin = this;
			((PlaceUnitAction) actionPlayer1).unitName = unit.getName();
		} else {
			actionPlayer2 = new PlaceUnitAction();
			((PlaceUnitAction) actionPlayer2).origin = this;
			((PlaceUnitAction) actionPlayer2).unitName = unit.getName();
		}
	}

	public void setUnit(Unit unit, int player) {
		if (player == 1) {
			unitsPlayer1 = unit;
			unit.setPosition(getX() + 10f, getY() + 50f);
		} else {
			unitsPlayer2 = unit;
			unit.setPosition(getX() + 71f, getY() + 50f);
			unit.getRender().setFacing(FACING.left);
		}
	}

	public Unit getUnit(int player) {
		if (player == 1) {
			return unitsPlayer1;
		} else {
			return unitsPlayer2;
		}
	}

	public void removeUnit(int player) {
		if (player == 1) {
			unitsPlayer1 = null;
		} else {
			unitsPlayer2 = null;
		}
	}

	public UnitAction getAction(int player) {
		if (player == 1) {
			return actionPlayer1;
		} else {
			return actionPlayer2;
		}
	}

	public void setAction(UnitAction action, int player) {
		if (player == 1) {
			actionPlayer1 = action;
		} else {
			actionPlayer2 = action;
		}
	}
}
