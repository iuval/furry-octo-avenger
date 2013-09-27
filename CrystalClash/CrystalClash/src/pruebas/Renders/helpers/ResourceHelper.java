package pruebas.Renders.helpers;

import java.util.Hashtable;

import pruebas.Util.FileUtil;
import pruebas.Util.SuperAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class ResourceHelper {
	private static Hashtable<String, Texture> textureMap;

	private static TextureAtlas atlas;
	private static Skin skin;
	private static BitmapFont font;

	private static TextButtonStyle buttonStyle;
	private static TextButtonStyle outerButtonStyle;
	private static TextButtonStyle outerSmallButtonStyle;
	private static TextButtonStyle nextButtonStyle;

	public static void fastLoad() {
		textureMap = new Hashtable<String, Texture>();
	}

	public static void slowLoad() {
		atlas = new TextureAtlas("data/Images/Buttons/buttons.pack");
		skin = new Skin(atlas);
		font = new BitmapFont(Gdx.files.internal("data/Fonts/font.fnt"), false);

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
		if (textureMap.containsKey(path)) {
			return textureMap.get(path);
		} else {
			Texture t = FileUtil.getTexture(path);
			textureMap.put(path, t);
			return t;
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
