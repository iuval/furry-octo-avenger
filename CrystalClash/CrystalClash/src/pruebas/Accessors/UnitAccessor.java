package pruebas.Accessors;

import pruebas.Entities.Unit;
import aurelienribon.tweenengine.TweenAccessor;

public class UnitAccessor implements TweenAccessor<Unit> {

	public static final int X = 0, Y = 1;

	@Override
	public int getValues(Unit target, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case X:
			returnValues[0] = target.getX();
			return 1;
		case Y:
			returnValues[0] = target.getY();
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
			target.setPosition(newValues[0], target.getY());
			break;
		case Y:
			target.setPosition(target.getX(), newValues[0]);
			break;
		default:
			assert false;
		}
	}

}
