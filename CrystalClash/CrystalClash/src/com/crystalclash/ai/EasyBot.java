package com.crystalclash.ai;

import com.crystalclash.controllers.WorldController;
import com.crystalclash.entities.Unit;
import com.crystalclash.entities.helpers.PlaceUnitAction;

public class EasyBot extends Bot {
	public EasyBot(WorldController controller) {
		super(controller);
	}

	@Override
	public int firstTurn() {
		world.cellGrid[5][3].setUnit(new Unit("wind_slayer", 2, true));
		world.cellGrid[5][3].setAction(new PlaceUnitAction());
		world.cellGrid[6][3].setUnit(new Unit("wind_slayer", 2, true));
		world.cellGrid[6][3].setAction(new PlaceUnitAction());
		
		return 2;
	}

	@Override
	public void normalTurn() {
		// TODO Auto-generated method stub

	}

}
