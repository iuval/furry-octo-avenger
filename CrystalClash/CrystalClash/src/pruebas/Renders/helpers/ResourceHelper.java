package pruebas.Renders.helpers;

import java.util.Hashtable;

import pruebas.Controllers.GameController;
import pruebas.Util.FileUtil;
import pruebas.Util.SuperAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class ResourceHelper {
	private static Hashtable<String, Texture> texturesMap;
	private static Hashtable<String, TextureAtlas> textureAtlasMap;
	private static Hashtable<String, SuperAnimation> superAnimationsMap;

	private static TextureAtlas atlas;
	private static Skin skin;
	private static BitmapFont font;

	private static TextButtonStyle buttonStyle;
	private static TextButtonStyle outerButtonStyle;
	private static TextButtonStyle outerSmallButtonStyle;
	private static TextButtonStyle nextButtonStyle;

	public static void fastLoad() {
		texturesMap = new Hashtable<String, Texture>();
		textureAtlasMap = new Hashtable<String, TextureAtlas>();
		superAnimationsMap = new Hashtable<String, SuperAnimation>();
	}

	public static void slowLoad() {
		atlas = getTextureAtlas("buttons/buttons.pack", false);
		skin = new Skin(atlas);
		font = new BitmapFont(Gdx.files.internal("data/fonts/font.fnt"), false);

		buttonStyle = new TextButtonStyle(
				skin.getDrawable("button_orange"),
				skin.getDrawable("button_orange_pressed"), null, font);

		outerButtonStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_orange"),
				skin.getDrawable("outer_button_orange_pressed"), null, font);

		outerSmallButtonStyle = new TextButtonStyle(
				skin.getDrawable("outer_button_small"),
				skin.getDrawable("outer_button_small_pressed"), null, font);

		nextButtonStyle = new TextButtonStyle(
				skin.getDrawable("next_button"),
				skin.getDrawable("next_button_pressed"), null, font);
	}

	public static Texture getTexture(String path) {
		if (texturesMap.containsKey(path)) {
			return texturesMap.get(path);
		} else {
			Texture t = FileUtil.getTexture(String.format("data/images/%s", path));
			texturesMap.put(path, t);
			return t;
		}
	}

	public static Texture getTexture(String path, boolean persistent) {
		if (persistent) {
			return getTexture(path);
		} else {
			return FileUtil.getTexture(String.format("data/images/%s", path));
		}
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

	public static BitmapFont getFont() {
		return font;
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
