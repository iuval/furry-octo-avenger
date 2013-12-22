package com.crystalclash.renders.helpers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EmblemHelper {
	public static final float WIDTH = 100;
	public static final float HEIGHT = 100;

	public static TextureRegion getEmblem(int number) {
		return ResourceHelper.getTexture(String.format("emblems/%s", number));
	}

	public static TextureRegion getSelectedBorder() {
		return ResourceHelper.getTexture(String.format("emblems/item_selected"));
	}
}
