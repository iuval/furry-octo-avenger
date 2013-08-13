package pruebas.Entities.helpers;

import pruebas.Entities.Cell;

public abstract class UnitAction {
	public enum UnitActionType {
		NONE, PLACE, ATTACK, MOVE, DEFENSE
	}

	public Cell origin;

	public abstract void addDataToJson(StringBuilder builder);

	public abstract UnitActionType getActionType();
	
	public boolean equals(Object other){
		UnitAction action = (UnitAction) other;
		return this.origin == action.origin;
	}
}
