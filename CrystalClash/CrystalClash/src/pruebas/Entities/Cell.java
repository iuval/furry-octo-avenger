package pruebas.Entities;

import pruebas.Entities.helpers.PlaceUnitAction;
import pruebas.Entities.helpers.UnitAction;
import pruebas.Renders.CellRender;
import pruebas.Renders.helpers.CellHelper;

public class Cell extends GameObject {
	public enum State {
		NONE, ABLE_TO_ATTACK, ABLE_TO_MOVE, ABLE_TO_PLACE, ATTACK_TARGET_CENTER, ATTACK_TARGET_RADIUS, MOVE_TARGET
	}

	private Unit unit;
	private UnitAction action;
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

	public void placeUnit(Unit unit) {
		setUnit(unit);
		action = new PlaceUnitAction();
		action.origin = this;
		((PlaceUnitAction) action).unitName = unit.getName();
	}

	public void setUnit(Unit unit) {
		unit = unit;
		unit.setPosition(getX() + CellHelper.UNIT_PLAYER_1_X, getY()
				+ CellHelper.UNIT_PLAYER_1_Y);

	}

	public Unit getUnit() {
		return unit;
	}

	public void removeUnit() {
		unit = null;
	}

	public void removeDeadUnits() {
		if (unit != null && !unit.isAlive())
			unit = null;
	}

	public UnitAction getAction() {
		return action;
	}

	public void setAction(UnitAction action) {
		action.origin = this;
		action = action;
	}

	public void addDataToJson(StringBuilder builder) {
		if (unit != null) {
			builder.append("\"unit\":{");
			addUnitStatsToJson(builder, unit);
			if (action != null) {
				action.addDataToJson(builder);
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

	public boolean Equals(Object other) {
		return this.getGridPosition().equals(((Cell) other).getGridPosition());
	}
}
