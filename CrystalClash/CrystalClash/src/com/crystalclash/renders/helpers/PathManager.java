package com.crystalclash.renders.helpers;

import java.util.Enumeration;
import java.util.Hashtable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.crystalclash.entities.Path;
import com.crystalclash.entities.Unit;
import com.crystalclash.renders.PathRender;
import com.crystalclash.renders.PathRender.TYPE;

public class PathManager {

	private Hashtable<Unit, PathRender> paths;

	public static final float POINT_CENTER_X = 19f;
	public static final float POINT_CENTER_Y = 19f;

	public static int STEP_COUNT_PER_SEGMENT;
	public static float BIG_DOT_TIME;
	public static TextureRegion SMALL_DOT_MOVE_TEXTURE;
	public static TextureRegion BIG_DOT_MOVE_TEXTURE;
	public static TextureRegion SMALL_DOT_ATTACK_TEXTURE;
	public static TextureRegion BIG_DOT_ATTACK_TEXTURE;

	public PathManager() {
		paths = new Hashtable<Unit, PathRender>();
	}

	public static void load() {
		STEP_COUNT_PER_SEGMENT = 3;
		BIG_DOT_TIME = 0.2f;

		SMALL_DOT_MOVE_TEXTURE = ResourceHelper.getTexture("in_game/action_dots/movement_small_dot");
		BIG_DOT_MOVE_TEXTURE = ResourceHelper.getTexture("in_game/action_dots/movement_big_dot");
		SMALL_DOT_ATTACK_TEXTURE = ResourceHelper.getTexture("in_game/action_dots/attack_small_dot");
		BIG_DOT_ATTACK_TEXTURE = ResourceHelper.getTexture("in_game/action_dots/attack_big_dot");
	}

	public void render(SpriteBatch batch, float dt, PathRender.TYPE type) {
		Enumeration<PathRender> en = paths.elements();
		PathRender p;
		while (en.hasMoreElements()) {
			p = en.nextElement();
			if (p.getType() == type)
				p.draw(batch, dt);
		}
	}

	public PathRender getOrCreatePath(Unit u, PathRender.TYPE type) {
		PathRender p = getPath(u);
		if (p == null) {
			p = new PathRender(type);
			paths.put(u, p);
		}
		return p;
	}

	public PathRender createOrResetPath(Unit u, PathRender.TYPE type) {
		PathRender p = getPath(u);
		if (p == null) {
			p = new PathRender(type);
			paths.put(u, p);
		} else {
			p.clear();
		}
		return p;
	}

	public PathRender getPath(Unit u) {
		return paths.get(u);
	}

	public void removePath(Unit u) {
		paths.remove(u);
	}

	public static void addLine(Path p, float iniX, float iniY, float endX, float endY) {
		float dx = 0;
		float dy = 0;
		if (iniX == endX) {
			dx = 0;
			if (endY > iniY) {
				dy = 36;
			} else {
				dy = -36;
			}
		} else {
			if (endX > iniX) {
				if (endY > iniY) {
					dx = 31;
					dy = 18;
				} else {
					dx = 31;
					dy = -18;
				}
			} else {
				if (endY > iniY) {
					dx = -31;
					dy = 18;
				} else {
					dx = -31;
					dy = -18;
				}
			}
		}
		while (Math.abs(endY - iniY) > 5 || Math.abs(endX - iniX) > 5) {
			p.add(iniX - POINT_CENTER_X, iniY - POINT_CENTER_Y);
			iniX += dx;
			iniY += dy;
		}
	}

	public static void addArc(Path p, float iniX, float iniY, float endX, float endY) {
		if (iniX == endX)
			addLine(p, iniX, iniY, endX, endY);
		else {
			double a = 0;
			double b = 0;
			double c = 0;
			double v = iniX < endX ? 2 : -2;

			float y = 0;
			float dx = Math.abs(iniX - endX) / 10;

			a = (endY - iniY - v * (endX - iniX)) / ((Math.pow(endX, 2) - Math.pow(iniX, 2)) - 2 * iniX * (endX - iniX));
			b = v - 2 * a * iniX;
			c = iniY - a * Math.pow(iniX, 2) - b * iniX;

			if (iniX < endX) {
				for (float x = iniX; x < endX; x += dx) {
					y = (float) (a * Math.pow(x, 2) + b * x + c);
					p.add(x, y);
				}
			} else {
				for (float x = iniX; x > endX; x -= dx) {
					y = (float) (a * Math.pow(x, 2) + b * x + c);
					p.add(x, y);
				}
			}
		}
	}

	public static TextureRegion getSmallBallTexture(PathRender.TYPE type) {
		if (type == TYPE.ATTACK) {
			return PathManager.SMALL_DOT_ATTACK_TEXTURE;
		} else {
			return PathManager.SMALL_DOT_MOVE_TEXTURE;
		}
	}

	public static TextureRegion getBigBallTexture(PathRender.TYPE type) {
		if (type == TYPE.ATTACK) {
			return PathManager.BIG_DOT_ATTACK_TEXTURE;
		} else {
			return PathManager.BIG_DOT_MOVE_TEXTURE;
		}
	}
}
