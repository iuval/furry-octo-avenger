package pruebas.Entities.helpers;

import pruebas.Entities.Cell;
import pruebas.Entities.GridPos;

public abstract class UnitAction {
	public Cell origin;

	public abstract String getData();

	protected void addOrigin(StringBuilder builder) {
		builder.append("\"cell\": {");
		GridPos pos = origin.getGridPosition();
		builder.append("\"x\": ");
		builder.append(pos.getX());
		builder.append(",\"y\": ");
		builder.append(pos.getY());
		builder.append("}");
	}
}
