package pruebas.Renders.helpers;

import java.util.Hashtable;

import pruebas.Renders.UnitRender;
import pruebas.Util.FileUtil;
import pruebas.Util.SuperAnimation;
import pruebas.Util.UnitAnimPrefReader;
import pruebas.Util.UnitAnimPrefReader.UnitPrefReaderData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UnitHelper {
	public static final float WIDTH = 135;
	public static final float HEIGHT = 150;

	public static final int HP_BAR_HEIGHT = 10;
	public static final int HP_BAR_WIDTH = 50;
	public static final int HP_BAR_Y = 130;
	public static final int HP_BAR_X = 10;

	public static Hashtable<String, SuperAnimation> renderMap;
	public static Texture enemyHPBar;
	public static Texture allyHPBar;

	public static void init() {
		renderMap = new Hashtable<String, SuperAnimation>();
	}

	public static UnitRender getUnitRender(String unitName) {
		UnitRender render = new UnitRender();
		render.idleAnim = getUnitSuperAnimation(unitName, "idle");
		render.idleAnim.randomCurrentFrame();
		render.fightAnim = getUnitSuperAnimation(unitName, "attack");
		render.walkAnim = getUnitSuperAnimation(unitName, "run");
		
		render.dieAnim = getUnitSuperAnimation(unitName, "die");
		render.shieldAnim = getSuperAnimation("data/Units/defensive_shield");
		render.setState(UnitRender.STATE.idle);
		return render;
	}

	public static SuperAnimation getUnitSuperAnimation(String unitName, String action) {
		return getSuperAnimation(String.format("data/Units/%s/%s", unitName, action));
	}

	public static SuperAnimation getSuperAnimation(String path) {
		if (renderMap.contains(path)) {
			return renderMap.get(path).clone();
		}
		return FileUtil.getSuperAnimation(path);
	}

	public static Texture getEnemyHPBar() {
		if (enemyHPBar == null) {
			enemyHPBar = createBar(1, 0, 0, 1);
		}
		return enemyHPBar;
	}

	public static Texture getAllyHPBar() {
		if (allyHPBar == null) {
			allyHPBar = createBar(0, 1, 0, 1);
		}
		return allyHPBar;
	}

	private static Texture createBar(int r, int g, int b, int a) {
		Pixmap pixmap = new Pixmap(UnitHelper.HP_BAR_WIDTH,
				UnitHelper.HP_BAR_HEIGHT, Format.RGBA4444); // or RGBA8888
		pixmap.setColor(r, g, b, a);
		pixmap.fill();
		Texture text = new Texture(pixmap); // must be manually disposed
		pixmap.dispose();
		return text;
	}
}
