package com.crystalclash.ai;

import com.crystalclash.controllers.WorldController;

public abstract class Bot {
	protected WorldController world;

	public Bot(WorldController controller) {
		world = controller;
	}

	public abstract int firstTurn();

	public abstract void normalTurn();

}
