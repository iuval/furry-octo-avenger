package com.crystalclash.renders.helpers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.crystalclash.controllers.GameController;

public class EmblemHelper {
	public static final float WIDTH = 100;
	public static final float HEIGHT = 100;

	public static void loadEmblems() {
		for (int i = 0; i < GameController.EMBLEM_COUNT; i++) {
			ResourceHelper.loadTexture(String.format("emblems/%s", i));
		}
	}

	public static TextureRegion getEmblem(int number) {
		return ResourceHelper.getTexture(String.format("emblems/%s", number));
	}

	public static TextureRegion getSelectedBorder() {
		return ResourceHelper.getTexture(String.format("emblems/item_selected"));
	}
}
