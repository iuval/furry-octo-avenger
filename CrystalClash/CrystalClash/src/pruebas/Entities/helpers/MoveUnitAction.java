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
	public String getData() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"action\": {");

		addOrigin(builder);

		builder.append(", \"action\": \"move\"");

		builder.append(", \"target\": [");
		GridPos pos;
		for (int i = 0; i < moves.size; i++) {
			pos = moves.get(i).getGridPosition();
			if (i > 0)
				builder.append(",");
			builder.append("{\"x\": ");
			builder.append(pos.getX());
			builder.append(",\"y\": ");
			builder.append(pos.getY());
			builder.append("}");
		}
		builder.append("]");

		builder.append("}");
		return builder.toString();
	}

	@Override
	public UnitActionType getActionType() {
		return UnitActionType.MOVE;
	}
}
