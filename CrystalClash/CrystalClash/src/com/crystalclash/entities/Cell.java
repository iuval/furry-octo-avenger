package com.crystalclash.entities;

import com.crystalclash.entities.helpers.PlaceUnitAction;
import com.crystalclash.entities.helpers.UnitAction;
import com.crystalclash.renders.CellRender;
import com.crystalclash.renders.helpers.CellHelper;

public class Cell extends GameObject {
	public final static int NONE = 0; // 0
	public final static int ABLE_TO_ATTACK = 1 << 0; // 1
	public final static int ABLE_TO_MOVE = 1 << 1; // 2
	public final static int ABLE_TO_PLACE = 1 << 2; // 4
	public final static int ATTACK_TARGET_CENTER = 1 << 3; // 8
	public final static int ATTACK_TARGET_RADIUS = 1 << 4; // 16
	public final static int MOVE_TARGET = 1 << 5; // 32
	public final static int SELECTED = 1 << 6; // 64
	public final static int NOT_ABLE_TO_ATTACK = 1 << 7; // 128

	private Unit unit;
	private UnitAction action;
	public int state = NONE;
	public int[][] neigbours;
	private CellRender render;
	private int incomingAttacksCount = 0;

	public Cell() {
		render = new CellRender(this);
	}

	public float getCenterX() {
		return getX() + CellHelper.CELL_CENTER_X;
	}

	public float getCenterY() {
		return getY() + CellHelper.CELL_CENTER_Y;
	}

	public boolean hasState(int state) {
		return (this.state & state) == state;
	}

	public void addState(int state) {
		this.state |= state;
		if ((state & ATTACK_TARGET_CENTER) == ATTACK_TARGET_CENTER)
			incomingAttacksCount++;
	}

	public void removeState(int state) {
		if (incomingAttacksCount > 0 && (state & ATTACK_TARGET_CENTER) == ATTACK_TARGET_CENTER) {
			incomingAttacksCount--;
			// If there are more attacks targeting this cell, don't remove the
			// atack target state
			if (incomingAttacksCount > 0)
				return;
		}
		this.state &= ~state;
	}

	public CellRender getRender() {
		return render;
	}

	public void placeUnit(Unit unit) {
		setUnit(unit);
		action = new PlaceUnitAction();
		action.origin = this;
		((PlaceUnitAction) action).unitName = unit.getName();
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
		unit.setPosition(getX() + CellHelper.CELL_UNIT_X, getY()
				+ CellHelper.CELL_UNIT_Y);
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
		if (action != null) {
			action.origin = this;
		}
		this.action = action;
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
