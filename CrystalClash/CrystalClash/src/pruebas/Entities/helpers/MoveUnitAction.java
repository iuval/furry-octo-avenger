package pruebas.Entities.helpers;

import pruebas.Entities.Cell;
import pruebas.Entities.GridPos;

import com.badlogic.gdx.utils.Array;

public class MoveUnitAction extends UnitAction {

	public Array<Cell> moves;

	public MoveUnitAction() {
		moves = new Array<Cell>();
	}

	@Override
	public void addDataToJson(StringBuilder builder) {
		builder.append(",\"action\":\"move\"").append(",\"target\":{");
		GridPos pos;
		for (int i = 0; i < moves.size; i++) {
			pos = moves.get(i).getGridPosition();
			builder.append("\"pos\":{\"x\":").append(pos.getX()).append(",\"y\": ").append(pos.getY()).append("},");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("}");
	}

	@Override
	public UnitActionType getActionType() {
		return UnitActionType.MOVE;
	}
}
