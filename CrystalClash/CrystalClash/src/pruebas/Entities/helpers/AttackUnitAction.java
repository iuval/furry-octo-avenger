package pruebas.Entities.helpers;

import pruebas.Entities.Cell;
import pruebas.Entities.GridPos;

public class AttackUnitAction extends UnitAction {

	public Cell target;

	public AttackUnitAction() {
	}

	@Override
	public String getData() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"action\": {");

		addOrigin(builder);

		builder.append(", \"action\": \"attack\"");

		builder.append(", \"target\": {");
		GridPos pos = target.getGridPosition();
		builder.append("\"x\": ");
		builder.append(pos.getX());
		builder.append(",\"y\": ");
		builder.append(pos.getY());
		builder.append("}");

		builder.append("}");
		return builder.toString();
	}
}
