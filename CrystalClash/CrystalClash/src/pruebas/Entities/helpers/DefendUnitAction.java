package pruebas.Entities.helpers;

public class DefendUnitAction extends UnitAction {

	public DefendUnitAction() {
	}

	@Override
	public String getData() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"action\": {");

		addOrigin(builder);

		builder.append(", \"action\": \"defend\"");

		builder.append("}");
		return builder.toString();
	}
}
