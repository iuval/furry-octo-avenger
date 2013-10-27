package com.crystalclash.entities.helpers;

public class NoneUnitAction extends UnitAction {

	public NoneUnitAction() {
	}

	@Override
	public void addDataToJson(StringBuilder builder) {
		builder.append(",\"action\":\"none\"");
	}

	@Override
	public UnitActionType getActionType() {
		return UnitActionType.NONE;
	}

}
