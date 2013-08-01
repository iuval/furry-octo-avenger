package pruebas.Renders.helpers;

import pruebas.Renders.UnitRender;
import pruebas.Util.SuperAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UnitHelper {
	public static final float WIDTH = 135;
	public static final float HEIGHT = 150;

	public static UnitRender getUnitRender(String unitName) {
		UnitRender render = new UnitRender();
		if (unitName.equals("fire_archer")) {

			render.idleAnim = getUnitSuperAnimation(unitName, "idle");
			render.setAnimation(UnitRender.ANIM.idle);
		}
		return render;
	}

	public static SuperAnimation getUnitSuperAnimation(String unitName,
			String action) {
		int FRAME_COLS = 6;
		int FRAME_ROWS = 4;
		Texture sheet = new Texture(Gdx.files.internal(String.format(
				"data/Units/%s_%s_sheet.png", unitName, action)));
		TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth()
				/ FRAME_COLS, sheet.getHeight() / FRAME_ROWS);
		TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		float[] times = new float[FRAME_COLS * FRAME_ROWS];

		times[0] = 0.0f;
		times[1] = 0.23f;
		times[2] = 0.33f;
		times[3] = 0.49f;
		times[4] = 0.57f;
		times[5] = 0.583f;
		times[6] = 0.59167f;
		times[7] = 0.6f;
		times[8] = 0.61667f;
		times[9] = 0.65f;
		times[10] = 0.65833f;
		times[11] = 0.7f;
		times[12] = 0.71667f;
		times[13] = 0.79167f;
		times[14] = 0.86667f;
		times[15] = 1.0f;
		times[16] = 1.01667f;
		times[17] = 1.05833f;
		times[18] = 1.08333f;
		times[19] = 1.13333f;
		times[20] = 1.15f;
		times[21] = 1.18333f;
		times[22] = 1.26667f;
		times[23] = 1.28333f;

		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		return new SuperAnimation(times, frames);
	}
}
