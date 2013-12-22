package com.crystalclash.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.crystalclash.controllers.GameController;
import com.crystalclash.util.UnitAnimPrefReader.UnitPrefReaderData;

public class FileUtil {

	public static SuperAnimation getSuperAnimation(String base_file_name) {
		UnitPrefReaderData data = UnitAnimPrefReader.load(base_file_name + ".pref");

		TextureRegion sheet = getTexture(base_file_name + ".png");
		TextureRegion[][] tmp = sheet.split(sheet.getRegionWidth()
				/ data.cols, sheet.getRegionHeight() / data.rows);
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
		anim.handle_y = data.handle_y;
		return anim;
	}

	public static TextureRegion getTextureRegion(String path) {
		return new TextureRegion(getTexture(path));
	}

	public static TextureRegion getTexture(String path) {
		Texture t = new Texture(Gdx.files.internal(path));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return new TextureRegion(t);
	}

	public static Music getMusic(String path) {
		return Gdx.audio.newMusic(Gdx.files.internal(path));
	}

	public static Sound getUnitSFX(String unitName, String file) {
		return Gdx.audio.newSound(Gdx.files.internal(String.format("data/audio/units/%s/%s/%s.mp3", GameController.getUnitElement(unitName), unitName, file)));
	}

	public static Sound getSound(String file) {
		return Gdx.audio.newSound(Gdx.files.internal(String.format("data/audio/sfx/%s.mp3", file)));
	}
}
