package pruebas.Entities.helpers;

public abstract class UnitAction {
	public enum UnitActionType {
		NONE, PLACE, ATTACK, MOVE, DEFENSE
	}

	public abstract void addDataToJson(StringBuilder builder);

	public abstract UnitActionType getActionType();
}
