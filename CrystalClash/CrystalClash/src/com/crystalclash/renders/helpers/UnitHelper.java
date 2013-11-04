package com.crystalclash.renders.helpers;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.crystalclash.renders.UnitRender;

public class UnitHelper {
	public static final float WIDTH = 135;
	public static final float HEIGHT = 150;

	public static final int HP_BAR_BACK_HEIGHT = 8;
	public static final int HP_BAR_BACK_WIDTH = 50;
	public static final int HP_BAR_HEIGHT = 4;
	public static final int HP_BAR_WIDTH = 46;
	public static final int HP_BAR_BACK_Y = 134;
	public static final int HP_BAR_BACK_X = 14;
	public static final int HP_BAR_Y = 136;
	public static final int HP_BAR_X = 16;

	public static Texture backHPBar;
	public static Texture enemyHPBar;
	public static Texture allyHPBar;

	public static void init() {
		backHPBar = createBar(0, 0, 0, 0.9f, UnitHelper.HP_BAR_BACK_WIDTH, UnitHelper.HP_BAR_BACK_HEIGHT);
	}

	public static UnitRender getUnitRender(String unitName, boolean isEnemy) {
		UnitRender render = new UnitRender();
		render.idleAnim = ResourceHelper.getUnitSuperAnimation(unitName, "idle", isEnemy);
		render.idleAnim.randomCurrentFrame();
		render.fightAnim = ResourceHelper.getUnitSuperAnimation(unitName, "attack", isEnemy);
		render.fightAnim.setLooping(false);
		render.walkAnim = ResourceHelper.getUnitSuperAnimation(unitName, "run", isEnemy);
		render.dieAnim = ResourceHelper.getUnitSuperAnimation(unitName, "die", isEnemy);
		render.dieAnim.setLooping(false);
		render.shieldAnim = ResourceHelper.getSuperAnimation("units/defensive_shield");
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
			allyHPBar = createBar(0, 0.5f, 1, 1, UnitHelper.HP_BAR_WIDTH,
					UnitHelper.HP_BAR_HEIGHT);
		}
		return allyHPBar;
	}

	private static Texture createBar(float r, float g, float b, float a, int h, int w) {
		Pixmap pixmap = new Pixmap(w, h, Format.RGBA4444); // or RGBA8888
		pixmap.setColor(r, g, b, a);
		pixmap.fill();
		Texture text = new Texture(pixmap); // must be manually disposed
		pixmap.dispose();
		return text;
	}
}
