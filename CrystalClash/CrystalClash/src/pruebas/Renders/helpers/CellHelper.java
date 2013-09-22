package pruebas.Renders.helpers;

import pruebas.Entities.Cell;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CellHelper {
	public final static float CELL_WIDTH = 122.0F;
	public final static float CELL_HEIGHT = 106.0F;

	public static final float CELL_CENTER_X = 61f;
	public static final float CELL_CENTER_Y = 53f;

	public static final float CELL_UNIT_X = 30f;
	public static final float CELL_UNIT_Y = 25f;

	TextureAtlas atlas;

	TextureRegion none;
	TextureRegion able_to_attack;
	TextureRegion able_to_move;
	TextureRegion able_to_place;

	TextureRegion attack_target_center;
	TextureRegion attack_target_radius;
	TextureRegion path;

	public void load() {
		atlas = new TextureAtlas("data/Images/InGame/cells.pack");
		Skin skin = new Skin(atlas);

		none = skin.getRegion("none");
		able_to_attack = skin.getRegion("able_to_attack");
		able_to_move = skin.getRegion("able_to_move");
		able_to_place = skin.getRegion("able_to_move");
		attack_target_center = skin.getRegion("attack_target_center");
		attack_target_radius = skin.getRegion("attack_target_center");
		path = skin.getRegion("path");
	}

	public void drawCellTextures(SpriteBatch batch, Cell cell) {
		if (cell.hasState(Cell.ABLE_TO_MOVE)) {
			batch.draw(able_to_move, cell.getX(), cell.getY());
		} else if (cell.hasState(Cell.ABLE_TO_ATTACK)) {
			batch.draw(able_to_attack, cell.getX(), cell.getY());
		} else if (cell.hasState(Cell.ABLE_TO_MOVE)) {
			batch.draw(able_to_move, cell.getX(), cell.getY());
		} else if (cell.hasState(Cell.ABLE_TO_PLACE)) {
			batch.draw(able_to_place, cell.getX(), cell.getY());
		} else {
			batch.draw(none, cell.getX(), cell.getY());
		}

		if (cell.hasState(Cell.MOVE_TARGET)) {
			batch.draw(path, cell.getX(), cell.getY());
		}
		if (cell.hasState(Cell.ATTACK_TARGET_CENTER)) {
			batch.draw(attack_target_center, cell.getX(), cell.getY());
		}
		if (cell.hasState(Cell.ATTACK_TARGET_RADIUS)) {
			batch.draw(attack_target_radius, cell.getX(), cell.getY());
		}
	}

	public static float getUnitX(Cell cell) {
		return cell.getX() + CellHelper.CELL_UNIT_X;
	}

	public static float getUnitY(Cell cell) {
		return cell.getY() + CellHelper.CELL_UNIT_Y;
	}
}
