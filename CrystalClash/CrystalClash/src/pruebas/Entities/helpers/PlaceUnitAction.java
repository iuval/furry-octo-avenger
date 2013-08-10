package pruebas.Entities.helpers;

public class PlaceUnitAction extends UnitAction {

	public String unitName;

	public PlaceUnitAction() {
	}

	@Override
	public String getData() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"action\": {");

		addOrigin(builder);

		builder.append(", \"action\": \"place\"");
		builder.append(", \"unit_name\": \"");
		builder.append(unitName);
		builder.append("\"");

		builder.append("}");

		return builder.toString();
	}
}
