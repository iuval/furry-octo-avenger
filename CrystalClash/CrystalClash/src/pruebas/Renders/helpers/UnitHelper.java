package pruebas.Renders.helpers;

import pruebas.Renders.UnitRender;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

public class UnitHelper {
	public static final float WIDTH = 135;
	public static final float HEIGHT = 150;

	public static final int HP_BAR_BACK_HEIGHT = 14;
	public static final int HP_BAR_BACK_WIDTH = 54;
	public static final int HP_BAR_HEIGHT = 10;
	public static final int HP_BAR_WIDTH = 50;
	public static final int HP_BAR_BACK_Y = 128;
	public static final int HP_BAR_BACK_X = 8;
	public static final int HP_BAR_Y = 130;
	public static final int HP_BAR_X = 10;

	public static Texture backHPBar;
	public static Texture enemyHPBar;
	public static Texture allyHPBar;

	public static void init() {
		backHPBar = createBar(0, 0, 0, 1, UnitHelper.HP_BAR_BACK_WIDTH, UnitHelper.HP_BAR_BACK_HEIGHT);
	}

	public static UnitRender getUnitRender(String unitName) {
		UnitRender render = new UnitRender();
		render.idleAnim = ResourceHelper.getUnitSuperAnimation(unitName, "idle");
		render.idleAnim.randomCurrentFrame();
		render.fightAnim = ResourceHelper.getUnitSuperAnimation(unitName, "attack");
		render.walkAnim = ResourceHelper.getUnitSuperAnimation(unitName, "run");
		render.dieAnim = ResourceHelper.getUnitSuperAnimation(unitName, "die");
		render.dieAnim.setLooping(false);
		render.shieldAnim = ResourceHelper.getSuperAnimation("Units/defensive_shield");
		render.setState(UnitRender.STATE.idle);
		return render;
	}

	public static Texture getEnemyHPBar() {
		if (enemyHPBar == null) {
			enemyHPBar = createBar(1, 0, 0, 1, UnitHelper.HP_BAR_WIDTH,
					UnitHelper.HP_BAR_HEIGHT);
		}
		return enemyHPBar;
	}

	public static Texture getAllyHPBar() {
		if (allyHPBar == null) {
			allyHPBar = createBar(0, 1, 0, 1, UnitHelper.HP_BAR_WIDTH,
					UnitHelper.HP_BAR_HEIGHT);
		}
		return allyHPBar;
	}

	private static Texture createBar(int r, int g, int b, int a, int h, int w) {
		Pixmap pixmap = new Pixmap(w, h, Format.RGBA4444); // or RGBA8888
		pixmap.setColor(r, g, b, a);
		pixmap.fill();
		Texture text = new Texture(pixmap); // must be manually disposed
		pixmap.dispose();
		return text;
	}
}
