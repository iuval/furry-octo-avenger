package pruebas.Renders.helpers;

import pruebas.Entities.Cell;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CellHelper {

	public final static float CELL_HEIGHT = 106.0F;
	public final static float CELL_WIDTH = 122.0F;

	public static final float UNIT_PLAYER_1_X = 30f;
	public static final float UNIT_PLAYER_1_Y = 25f;
	public static final float UNIT_PLAYER_1_CENTER_X = 61f;

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

	public TextureRegion getCellTexture(Cell cell) {
		switch (cell.getState()) {
		case NONE: {
			return none;
		}
		case ABLE_TO_ATTACK: {
			return able_to_attack;
		}
		case ABLE_TO_MOVE: {
			return able_to_move;
		}
		case ABLE_TO_PLACE: {
			return able_to_place;
		}
		case ATTACK_TARGET_CENTER: {
			return attack_target_center;
		}
		case ATTACK_TARGET_RADIUS: {
			return attack_target_radius;
		}
		case MOVE_TARGET: {
			return path;
		}
		default: {
			return none;
		}
		}
	}

	public static float getUnitX(Cell cell) {
		return cell.getX() + CellHelper.UNIT_PLAYER_1_X;
	}

	public static float getUnitCenterX(Cell cell) {
		return cell.getX() + CellHelper.UNIT_PLAYER_1_CENTER_X;
	}

	public static float getUnitY(Cell cell) {
		return cell.getY() + CellHelper.UNIT_PLAYER_1_Y;
	}
}
