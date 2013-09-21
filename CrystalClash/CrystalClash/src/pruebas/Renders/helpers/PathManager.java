package pruebas.Renders.helpers;

import java.util.Enumeration;
import java.util.Hashtable;

import pruebas.Entities.Path;
import pruebas.Entities.Path.TYPE;
import pruebas.Entities.Unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PathManager {

	private Hashtable<Unit, Path> paths;

	public static final float POINT_CENTER_X = 19f;
	public static final float POINT_CENTER_Y = 19f;

	public static int STEP_COUNT_PER_SEGMENT;
	public static float BIG_DOT_TIME;
	public static Texture SMALL_DOT_MOVE_TEXTURE;
	public static Texture BIG_DOT_MOVE_TEXTURE;
	public static Texture SMALL_DOT_ATTACK_TEXTURE;
	public static Texture BIG_DOT_ATTACK_TEXTURE;

	public PathManager() {
		paths = new Hashtable<Unit, Path>();
	}

	public static void load() {
		STEP_COUNT_PER_SEGMENT = 3;
		BIG_DOT_TIME = 0.2f;

		SMALL_DOT_MOVE_TEXTURE = ResourceHelper.getTexture("data/Images/InGame/ActionDots/movement_small_dot.png");
		BIG_DOT_MOVE_TEXTURE = ResourceHelper.getTexture("data/Images/InGame/ActionDots/movement_big_dot.png");
		SMALL_DOT_ATTACK_TEXTURE = ResourceHelper.getTexture("data/Images/InGame/ActionDots/attack_small_dot.png");
		BIG_DOT_ATTACK_TEXTURE = ResourceHelper.getTexture("data/Images/InGame/ActionDots/attack_big_dot.png");
	}

	public void render(SpriteBatch batch, float dt, Path.TYPE type) {
		Enumeration<Path> en = paths.elements();
		Path p;
		while (en.hasMoreElements()) {
			p = en.nextElement();
			if (p.getType() == type)
				p.draw(batch, dt);
		}
	}

	public Path getOrCreatePath(Unit u, Path.TYPE type) {
		Path p = getPath(u);
		if (p == null) {
			p = new Path(type);
			paths.put(u, p);
		}
		return p;
	}

	public Path createOrResetPath(Unit u, Path.TYPE type) {
		Path p = getPath(u);
		if (p == null) {
			p = new Path(type);
			paths.put(u, p);
		} else {
			p.clear();
		}
		return p;
	}

	public Path getPath(Unit u) {
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
		double a = 0;
		double b = 0;
		double c = 0;
		double v = 2;

		float y = 0;
		float dx = Math.abs(iniX - endX) / 10;

		if (iniX < endX) {
			a = (endY - iniY - v * (endX - iniX)) / ((Math.pow(endX, 2) - Math.pow(iniX, 2)) - 2 * iniX * (endX - iniX));
			b = v - 2 * a * iniX;
			c = iniY - a * Math.pow(iniX, 2) - b * iniX;

			for (float x = iniX; x < endX; x += dx) {
				y = (float) (a * Math.pow(x, 2) + b * x + c);
				p.add(x, y);
			}
		} else {
			a = (iniY - endY - v * (iniX - endX)) / ((Math.pow(iniX, 2) - Math.pow(endX, 2)) - 2 * endX * (iniX - endX));
			b = v - 2 * a * endX;
			c = endY - a * Math.pow(endX, 2) - b * endX;

			for (float x = iniX; x > endX; x -= dx) {
				y = (float) (a * Math.pow(x, 2) + b * x + c);
				p.add(x, y);
			}
		}
	}

	public static Texture getSmallBallTexture(Path.TYPE type) {
		if (type == TYPE.ATTACK) {
			return PathManager.SMALL_DOT_ATTACK_TEXTURE;
		} else {
			return PathManager.SMALL_DOT_MOVE_TEXTURE;
		}
	}

	public static Texture getBigBallTexture(Path.TYPE type) {
		if (type == TYPE.ATTACK) {
			return PathManager.BIG_DOT_ATTACK_TEXTURE;
		} else {
			return PathManager.BIG_DOT_MOVE_TEXTURE;
		}
	}
}
