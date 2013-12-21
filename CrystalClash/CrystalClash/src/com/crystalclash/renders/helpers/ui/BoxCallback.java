package com.crystalclash.renders.helpers.ui;

public interface BoxCallback {
	public static final int YES = 0x01;
	public static final int NO = 0x02;

	public void onEvent(int type, Object data);
}
