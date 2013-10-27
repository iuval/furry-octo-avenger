package com.crystalclash.renders;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;

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
