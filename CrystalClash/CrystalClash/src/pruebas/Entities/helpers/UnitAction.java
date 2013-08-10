package pruebas.Entities.helpers;

import pruebas.Entities.Cell;
import pruebas.Entities.GridPos;

public abstract class UnitAction {
	public enum UnitActionType{
		NONE, PLACE, ATTACK, MOVE, DEFENSE
	}
	
	public Cell origin;

	public abstract String getData();
	
	public abstract UnitActionType getActionType();

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
