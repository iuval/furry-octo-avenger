package pruebas.Entities;

import pruebas.Entities.helpers.PlaceUnitAction;
import pruebas.Entities.helpers.UnitAction;
import pruebas.Renders.CellRender;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.CellHelper;

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
			actionPlayer1.origin = this;
			((PlaceUnitAction) actionPlayer1).unitName = unit.getName();
		} else {
			actionPlayer2 = new PlaceUnitAction();
			actionPlayer2.origin = this;
			((PlaceUnitAction) actionPlayer2).unitName = unit.getName();
		}
	}

	public void setUnit(Unit unit, int player) {
		if (player == 1) {
			unitsPlayer1 = unit;
			unit.setPosition(getX() + CellHelper.UNIT_PLAYER_1_X, getY()
					+ CellHelper.UNIT_PLAYER_1_Y);
		} else {
			unitsPlayer2 = unit;
			unit.setPosition(getX() + CellHelper.UNIT_PLAYER_2_X, getY()
					+ CellHelper.UNIT_PLAYER_2_Y);
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
		action.origin = this;
		if (player == 1) {
			actionPlayer1 = action;
		} else {
			actionPlayer2 = action;
		}
	}

	public void addDataToJson(StringBuilder builder, int player) {
		if (player == 1 && unitsPlayer1 != null) {
			builder.append("\"unit\":{");
			addUnitStatsToJson(builder, unitsPlayer1);
			if (actionPlayer1 != null) {
				actionPlayer1.addDataToJson(builder);
			} else {
				builder.append(",\"action\":\"none\"");
			}
			builder.append("},");
		} else if (player == 2 && unitsPlayer2 != null) {
			builder.append("\"unit\":{");
			addUnitStatsToJson(builder, unitsPlayer2);
			if (actionPlayer2 != null) {
				actionPlayer2.addDataToJson(builder);
			} else {
				builder.append(",\"action\":\"none\"");
			}
			builder.append("},");
		}
	}

	private void addUnitStatsToJson(StringBuilder builder, Unit unit) {
		GridPos pos = getGridPosition();
		builder.append("\"unit_name\":\"").append(unit.getName())
				.append("\",\"unit_hp\":").append(unit.getHP())
				.append(",\"cell\":{").append("\"x\":").append(pos.getX())
				.append(",\"y\":").append(pos.getY()).append("}");

	}
	
	public boolean Equals(Object other){
		return this.getGridPosition().equals(((Cell)other).getGridPosition());
	}
}
