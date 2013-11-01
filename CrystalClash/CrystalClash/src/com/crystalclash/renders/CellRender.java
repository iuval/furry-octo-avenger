package com.crystalclash.renders;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.crystalclash.entities.Cell;
import com.crystalclash.entities.Unit;
import com.crystalclash.renders.helpers.ResourceHelper;
import com.crystalclash.views.WorldView;

public class CellRender {
	private Cell cell;

	public CellRender(Cell cell) {
		this.cell = cell;
	}

	BitmapFont font = ResourceHelper.getFont();

	public void draw(float dt, SpriteBatch batch) {
		WorldView.cellHelper.drawCellTextures(batch, cell);
		font.draw(batch, cell.getGridPosition().getX() + ", " + cell.getGridPosition().getY(), cell.getX(), cell.getCenterY());
	}

	public void drawUnits(float dt, SpriteBatch batch) {
		Unit u = cell.getUnit();
		if (u != null) {
			u.getRender().draw(batch, dt);
		}
	}
}
