package pruebas.renders;

import pruebas.Entities.Cell;
import pruebas.Entities.Unit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CellRender {
	private Cell cell;

	public CellRender(Cell cell) {
		this.cell = cell;
	}

	public void draw(float dt, SpriteBatch batch) {
		WorldRender.cellHelper.drawCellTextures(batch, cell);
	}

	public void drawUnits(float dt, SpriteBatch batch) {
		Unit u = cell.getUnit();
		if (u != null) {
			u.getRender().draw(batch, dt);
		}
	}
}
