package pruebas.Util;

import pruebas.Util.UnitAnimPrefReader.UnitPrefReaderData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FileUtil {

	public static SuperAnimation getSuperAnimation(String base_file_name) {
		UnitPrefReaderData data = UnitAnimPrefReader.load(base_file_name
				+ ".pref");

		Texture sheet = getTexture(base_file_name + ".png");
		TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth()
				/ data.cols, sheet.getHeight() / data.rows);
		TextureRegion[] frames = new TextureRegion[data.image_count];

		int index = 0;
		for (int i = 0; i < data.rows; i++) {
			for (int j = 0; j < data.cols; j++) {
				if (index < data.image_count) {
					frames[index++] = tmp[i][j];
				}
			}
		}

		SuperAnimation anim = new SuperAnimation(data.total_time, data.image_times, frames);
		anim.handle_x = data.handle_x;
		anim.handle_y = data.handle_x;
		return anim;
	}

	public static Texture getTexture(String path) {
		Texture t = new Texture(Gdx.files.internal(path));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return t;
	}

	public static TextureRegion getTextureRegion(String path) {
		return new TextureRegion(getTexture(path));
	}
}
