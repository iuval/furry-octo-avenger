package pruebas.Entities.helpers;

public class DefendUnitAction extends UnitAction {

	public DefendUnitAction() {
	}

	@Override
	public void addDataToJson(StringBuilder builder) {
		builder.append(",\"action\":\"defend\"");
	}

	@Override
	public UnitActionType getActionType() {
		return UnitActionType.DEFENSE;
	}
}
