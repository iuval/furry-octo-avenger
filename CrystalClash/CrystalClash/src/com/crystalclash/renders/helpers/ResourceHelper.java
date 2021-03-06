package com.crystalclash.renders.helpers;

import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
		texturesMap = new Hashtable<String, TextureRegion>();
		textureAtlasMap = new Hashtable<String, TextureAtlas>();
		superAnimationsMap = new Hashtable<String, SuperAnimation>();
	}

	public static void slowLoad() {
		atlas = getTextureAtlas("buttons/buttons.pack", false);
		skin = new Skin(atlas);
		bigBorderFont = new BitmapFont(Gdx.files.internal("data/fonts/crystal_clash_border.fnt"), false);
		bigBorderFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bigBorderFont.setScale(1.7f);
		normalBorderFont = new BitmapFont(Gdx.files.internal("data/fonts/crystal_clash_border.fnt"), false);
		normalBorderFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalBorderFont.setScale(1.4f);
		smallBorderFont = new BitmapFont(Gdx.files.internal("data/fonts/crystal_clash_border.fnt"), false);
		smallBorderFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		smallBorderFont.setScale(1.1f);

		damageFont = new BitmapFont(Gdx.files.internal("data/fonts/crystal_clash_border.fnt"), false);
		damageFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		damageFont.setColor(Color.RED);
		damageFont.setScale(1.3f);

		bigFont = new BitmapFont(Gdx.files.internal("data/fonts/crystal_clash.fnt"), false);
		bigFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bigFont.setScale(1.7f);
		normalFont = new BitmapFont(Gdx.files.internal("data/fonts/crystal_clash.fnt"), false);
		normalFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalFont.setScale(1.4f);
		smallFont = new BitmapFont(Gdx.files.internal("data/fonts/crystal_clash.fnt"), false);
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

	public static SuperAnimation getUnitResourceSuperAnimation(String unitName, String name) {
		return getSuperAnimation(String.format("units/%s/%s/%s", GameController.getUnitElement(unitName),
				unitName,
				name));
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
