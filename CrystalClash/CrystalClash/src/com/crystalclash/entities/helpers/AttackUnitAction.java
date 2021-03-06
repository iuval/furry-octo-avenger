package com.crystalclash.entities.helpers;

import com.crystalclash.entities.Cell;
import com.crystalclash.entities.GridPos;

public class AttackUnitAction extends UnitAction {

	public Cell target;
	public boolean meleeAttack;

	public AttackUnitAction(boolean meleeAttack) {
		this.meleeAttack = meleeAttack;
	}

	@Override
	public void addDataToJson(StringBuilder builder) {
		GridPos pos = target.getGridPosition();
		builder.append(",\"action\":\"attack\"")
		.append(",\"target\":{")
			.append("\"x\":").append(pos.getX())
			.append(",\"y\":").append(pos.getY())
			.append("}");
	}

	@Override
	public UnitActionType getActionType() {
		return UnitActionType.ATTACK;
	}
}
