package com.crystalclash.entities.helpers;

public class PlaceUnitAction extends UnitAction {

	public String unitName;

	public PlaceUnitAction() {
	}

	@Override
	public void addDataToJson(StringBuilder builder) {
		builder.append(",\"action\":\"place\"");
	}

	@Override
	public UnitActionType getActionType() {
		return UnitActionType.PLACE;
	}
}
