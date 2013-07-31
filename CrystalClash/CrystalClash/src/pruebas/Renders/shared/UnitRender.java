package pruebas.Renders.shared;

import pruebas.Entities.Unit;
import pruebas.Util.SuperAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class UnitRender extends Actor {
	public enum ANIM {
		idle, fight, walk
	}

	public Unit unit;

	public static final float WIDTH = 135;
	public static final float HEIGHT = 150;

	public SuperAnimation idleAnim;
	public SuperAnimation fightAnim;
	public SuperAnimation walkAnim;
	public SuperAnimation currnetAnim;

	public UnitRender() {
		unit = new Unit();
	}

	public void setAnimation(ANIM anim) {
		switch (anim) {
		case idle: {
			currnetAnim = idleAnim;
		}
			break;
		case fight: {
			currnetAnim = fightAnim;
		}
			break;
		case walk: {
			currnetAnim = walkAnim;
		}
			break;
		}
	}

	@Override
	public void act(float delta) {
		currnetAnim.update(delta, true);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(currnetAnim.current, getX(), getY());

	}

	public static UnitRender getUnitRender(String unitName) {
		UnitRender render = new UnitRender();
		if (unitName.equals("fire_archer")) {

			render.idleAnim = getUnitSuperAnimation(unitName, "idle");
			render.setAnimation(ANIM.idle);
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
		times[15] = 0.10f;
		times[16] = 0.101667f;
		times[17] = 0.105833f;
		times[18] = 0.108333f;
		times[19] = 0.113333f;
		times[20] = 0.115f;
		times[21] = 0.118333f;
		times[22] = 0.126667f;
		times[23] = 0.128333f;

		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		return new SuperAnimation(times, frames);
	}
}
