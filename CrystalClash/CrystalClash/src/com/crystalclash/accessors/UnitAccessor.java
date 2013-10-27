package com.crystalclash.accessors;

import aurelienribon.tweenengine.TweenAccessor;

import com.crystalclash.entities.Unit;
import com.crystalclash.renders.UnitRender.FACING;

public class UnitAccessor implements TweenAccessor<Unit> {

	public static final int X = 0, Y = 1, HP = 7;

	@Override
	public int getValues(Unit target, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case X:
			returnValues[0] = target.getX();
			return 1;
		case Y:
			returnValues[0] = target.getY();
			return 1;
		case HP:
			returnValues[0] = target.getHP();
			return 1;
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(Unit target, int tweenType, float[] newValues) {
		switch (tweenType) {
		case X:
			if (target.getX() > newValues[0])
				target.getRender().setFacing(FACING.left);
			else if (newValues[0] > target.getX())
				target.getRender().setFacing(FACING.right);

			target.setPosition(newValues[0], target.getY());
			break;
		case Y:
			target.setPosition(target.getX(), newValues[0]);
			break;
		case HP:
			target.setHPsoft((int) newValues[0]);
			break;
		default:
			assert false;
		}
	}

}
