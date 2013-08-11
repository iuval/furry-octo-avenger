package pruebas.Entities.helpers;

import pruebas.Entities.Cell;
import pruebas.Entities.GridPos;

public class AttackUnitAction extends UnitAction {

	public Cell target;

	public AttackUnitAction() {
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
