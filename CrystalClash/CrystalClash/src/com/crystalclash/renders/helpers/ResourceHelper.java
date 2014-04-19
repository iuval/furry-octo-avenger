package com.crystalclash.renders.helpers;

import java.util.Hashtable;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.crystalclash.controllers.GameController;
import com.crystalclash.util.SuperAnimation;
import com.crystalclash.util.UnitAnimPrefReader;
import com.crystalclash.util.UnitAnimPrefReader.UnitPrefReaderData;

public class ResourceHelper {
	private static AssetManager manager;
	private static Hashtable<String, SuperAnimation> superAnimationsMap;

	private static TextureAtlas atlas;
	private static Skin skin;
	private static BitmapFont bigBorderFont;
	private static BitmapFont normalBorderFont;
	private static BitmapFont smallBorderFont;
	private static BitmapFont bigFont;
	private static BitmapFont normalFont;
	private static BitmapFont smallFont;
	private static BitmapFont damageFont;

	private static TextButtonStyle buttonStyle;
	private static TextButtonStyle outerButtonStyle;
	private static TextButtonStyle outerSmallButtonStyle;
	private static TextButtonStyle nextButtonStyle;

	public static void fastLoad() {
		manager = new AssetManager();
		superAnimationsMap = new Hashtable<String, SuperAnimation>();
	}

	public static void resume() {
		Texture.setAssetManager(manager);
	}

	public static void finishLoading() {
		manager.finishLoading();
	}

	public static void slowLoad() {
		loadBasicTextures();

		finishLoading();

		atlas = getTextureAtlas("buttons/buttons");
		skin = new Skin(atlas);
		bigBorderFont = manager.get("data/fonts/crystal_clash_border.fnt");
		bigBorderFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bigBorderFont.setScale(1.7f);
		normalBorderFont = manager.get("data/fonts/crystal_clash_border.fnt");
		normalBorderFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalBorderFont.setScale(1.4f);
		smallBorderFont = manager.get("data/fonts/crystal_clash_border.fnt");
		smallBorderFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		smallBorderFont.setScale(1.1f);

		damageFont = manager.get("data/fonts/crystal_clash_border.fnt");
		damageFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		damageFont.setColor(Color.RED);
		damageFont.setScale(1.3f);

		bigFont = manager.get("data/fonts/crystal_clash.fnt");
		bigFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bigFont.setScale(1.7f);
		normalFont = manager.get("data/fonts/crystal_clash.fnt");
		normalFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalFont.setScale(1.4f);
		smallFont = manager.get("data/fonts/crystal_clash.fnt");
		smallFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		smallFont.setScale(1.1f);

		buttonStyle = new TextButtonStyle(
				skin.getDrawable("button_orange"),
				skin.getDrawable("button_orange_pressed"), null, normalBorderFont);

		outerButtonStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_orange"),
				skin.getDrawable("outer_button_orange_pressed"), null, normalBorderFont);

		outerSmallButtonStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_small"),
				skin.getDrawable("outer_button_small_pressed"), null, normalBorderFont);

		nextButtonStyle = new TextButtonStyle(
				skin.getDrawable("next_button"),
				skin.getDrawable("next_button_pressed"), null, normalBorderFont);
	}

	private static void loadBasicTextures() {
		loadTextureAtlas("buttons/buttons");

		manager.load("data/fonts/crystal_clash_border.fnt", BitmapFont.class);
		manager.load("data/fonts/crystal_clash.fnt", BitmapFont.class);

		loadTexture("splash/splash_crystal");
		loadTexture("splash/splash_name");
		loadTexture("menu/message_box_background");
		loadTexture("menu/loading/background");

		EmblemHelper.loadEmblems();
	}

	public static TextureRegion getTexture(String path) {
		TextureRegion t = new TextureRegion(manager.get(String.format("data/images/%s.png", path), Texture.class));
		t.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return t;
	}

	public static SuperAnimation getSuperAnimation(String path) {
		if (superAnimationsMap.containsKey(path)) {
			return superAnimationsMap.get(path).clone();
		} else {
			String base_file_name = String.format("data/images/%s", path);
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

			superAnimationsMap.put(path, anim);
			return anim;
		}
	}

	public static Music getMusic(String path) {
		return manager.get(path);
	}

	public static Sound getUnitSFX(String unitName, String file) {
		return manager.get(String.format("data/audio/units/%s/%s/%s.mp3", GameController.getUnitElement(unitName), unitName, file));
	}

	public static Sound getSound(String file) {
		return manager.get(String.format("data/audio/sfx/%s.mp3", file));
	}

	public static void loadMusic(String path) {
		manager.load(path, Music.class);
	}

	public static void loadUnitSFX(String unitName, String file) {
		manager.load(String.format("data/audio/units/%s/%s/%s.mp3", GameController.getUnitElement(unitName), unitName, file), Sound.class);
	}

	public static void loadSound(String file) {
		manager.load(String.format("data/audio/sfx/%s.mp3", file), Sound.class);
	}

	public static void loadTexture(String path) {
		manager.load(String.format("data/images/%s.png", path), Texture.class);
	}

	public static void loadTextureAtlas(String path) {
		manager.load(String.format("data/images/%s.pack", path), TextureAtlas.class);
	}

	public static TextureRegion getUnitResourceTexture(String unitName, String resource) {
		return getTexture(String.format("units/%s/%s/%s", GameController.getUnitElement(unitName), unitName, resource));
	}

	public static SuperAnimation getUnitResourceSuperAnimation(String unitName, String name) {
		return getSuperAnimation(String.format("units/%s/%s/%s", GameController.getUnitElement(unitName),
				unitName,
				name));
	}

	public static TextureAtlas getTextureAtlas(String path) {
		return manager.get(String.format("data/images/%s.pack", path));
	}

	public static SuperAnimation getUnitSuperAnimation(String unitName, String action, boolean isEnemy) {
		return getSuperAnimation(String.format("units/%s/%s/%s/%s", GameController.getUnitElement(unitName),
				unitName,
				isEnemy ? "enemy" : "ally",
				action));
	}

	public static TextureRegion getUnitProfile(String unitName) {
		return getTexture(String.format("units/%s/%s/profile", GameController.getUnitElement(unitName), unitName));
	}

	public static TextureRegion getUnitSplash(String unitName) {
		return getTexture(String.format("units/%s/%s/splash", GameController.getUnitElement(unitName), unitName));
	}

	public static TextureRegion getElementIcon(String elementName) {
		return getTexture(String.format("units/%s/element", elementName));
	}

	public static TextureRegion getUnitElementIcon(String unitName) {
		return getElementIcon(GameController.getUnitElement(unitName));
	}

	public static Skin getCommonButtonsSkin() {
		return skin;
	}

	public static BitmapFont getBigBorderFont() {
		return bigBorderFont;
	}

	public static BitmapFont getNormalBorderFont() {
		return normalBorderFont;
	}

	public static BitmapFont getSmallBorderFont() {
		return smallBorderFont;
	}

	public static BitmapFont getBigFont() {
		return bigFont;
	}

	public static BitmapFont getNormalFont() {
		return normalFont;
	}

	public static BitmapFont getDamageFont() {
		return damageFont;
	}

	public static BitmapFont getSmallFont() {
		return smallFont;
	}

	public static TextButtonStyle getButtonStyle() {
		return buttonStyle;
	}

	public static TextButtonStyle getOuterButtonStyle() {
		return outerButtonStyle;
	}

	public static TextButtonStyle getOuterSmallButtonStyle() {
		return outerSmallButtonStyle;
	}

	public static TextButtonStyle getNextButtonStyle() {
		return nextButtonStyle;
	}
}
