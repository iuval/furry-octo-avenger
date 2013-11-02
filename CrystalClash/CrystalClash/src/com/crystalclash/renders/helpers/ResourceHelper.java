package com.crystalclash.renders.helpers;

import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.crystalclash.controllers.GameController;
import com.crystalclash.util.FileUtil;
import com.crystalclash.util.SuperAnimation;

public class ResourceHelper {
	private static Hashtable<String, TextureRegion> texturesMap;
	private static Hashtable<String, TextureAtlas> textureAtlasMap;
	private static Hashtable<String, SuperAnimation> superAnimationsMap;

	private static TextureAtlas atlas;
	private static Skin skin;
	private static BitmapFont bigFont;
	private static BitmapFont smallFont;

	private static TextButtonStyle buttonStyle;
	private static TextButtonStyle outerButtonStyle;
	private static TextButtonStyle outerSmallButtonStyle;
	private static TextButtonStyle nextButtonStyle;

	public static void fastLoad() {
		texturesMap = new Hashtable<String, TextureRegion>();
		textureAtlasMap = new Hashtable<String, TextureAtlas>();
		superAnimationsMap = new Hashtable<String, SuperAnimation>();
	}

	public static void slowLoad() {
		atlas = getTextureAtlas("buttons/buttons.pack", false);
		skin = new Skin(atlas);
		bigFont = new BitmapFont(Gdx.files.internal("data/fonts/action_man.fnt"), false);
		bigFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bigFont.setScale(0.9f);
		smallFont = new BitmapFont(Gdx.files.internal("data/fonts/action_man.fnt"), false);
		smallFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		smallFont.setScale(0.7f);
		buttonStyle = new TextButtonStyle(
				skin.getDrawable("button_orange"),
				skin.getDrawable("button_orange_pressed"), null, bigFont);

		outerButtonStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_orange"),
				skin.getDrawable("outer_button_orange_pressed"), null, bigFont);

		outerSmallButtonStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_small"),
				skin.getDrawable("outer_button_small_pressed"), null, bigFont);

		nextButtonStyle = new TextButtonStyle(
				skin.getDrawable("next_button"),
				skin.getDrawable("next_button_pressed"), null, bigFont);
	}

	public static TextureRegion getTexture(String path) {
		if (texturesMap.containsKey(path)) {
			return texturesMap.get(path);
		} else {
			TextureRegion t = FileUtil.getTexture(String.format("data/images/%s.png", path));
			texturesMap.put(path, t);
			return t;
		}
	}

	public static TextureRegion getTexture(String path, boolean persistent) {
		if (persistent) {
			return getTexture(path);
		} else {
			return FileUtil.getTexture(String.format("data/images/%s.png", path));
		}
	}

	public static TextureRegion getUnitResourceTexture(String unitName, String resource) {
		return getTexture(String.format("units/%s/%s/%s", GameController.getUnitElement(unitName), unitName, resource));
	}

	public static TextureAtlas getTextureAtlas(String path) {
		if (textureAtlasMap.containsKey(path)) {
			return textureAtlasMap.get(path);
		} else {
			TextureAtlas t = new TextureAtlas(String.format("data/images/%s", path));
			textureAtlasMap.put(path, t);
			return t;
		}
	}

	public static TextureAtlas getTextureAtlas(String path, boolean persistent) {
		if (persistent) {
			return getTextureAtlas(path);
		} else {
			return new TextureAtlas(String.format("data/images/%s", path));
		}
	}

	public static SuperAnimation getUnitSuperAnimation(String unitName, String action) {
		return getSuperAnimation(String.format("units/%s/%s/%s", GameController.getUnitElement(unitName), unitName, action));
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

	public static SuperAnimation getSuperAnimation(String path) {
		if (superAnimationsMap.containsKey(path)) {
			return superAnimationsMap.get(path).clone();
		} else {
			SuperAnimation s = FileUtil.getSuperAnimation(String.format("data/images/%s", path));
			superAnimationsMap.put(path, s);
			return s;
		}
	}

	public static Skin getCommonButtonsSkin() {
		return skin;
	}

	public static BitmapFont getBigFont() {
		return bigFont;
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
