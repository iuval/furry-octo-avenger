package pruebas.Renders.helpers;

import java.util.Enumeration;
import java.util.Hashtable;

import pruebas.Entities.Path;
import pruebas.Entities.Unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PathManager {

	private Hashtable<Unit, Path> paths;

	public static int STEP_COUNT_PER_SEGMENT;
	public static float BIG_DOT_TIME;
	public static Texture SMALL_DOT_TEXTURE;
	public static Texture BIG_DOT_TEXTURE;

	public PathManager() {
		paths = new Hashtable<Unit, Path>();
	}

	public static void load() {
		STEP_COUNT_PER_SEGMENT = 3;
		BIG_DOT_TIME = 0.2f;

		SMALL_DOT_TEXTURE = ResourceHelper.getTexture("data/Images/InGame/ActionDots/movement_small_dot.png");
		BIG_DOT_TEXTURE = ResourceHelper.getTexture("data/Images/InGame/ActionDots/movement_big_dot.png");
	}

	public void render(SpriteBatch batch, float dt) {
		Enumeration<Path> en = paths.elements();
		while (en.hasMoreElements())
			en.nextElement().draw(batch, dt);
	}

	public Path getOrCreatePath(Unit u) {
		Path p = getPath(u);
		if (p == null) {
			p = new Path();
			paths.put(u, p);
		}
		return p;
	}

	public Path getPath(Unit u) {
		return paths.get(u);
	}

	public void removePath(Unit u) {
		paths.remove(u);
	}

	public void addRect(Path p, float iniX, float iniY, float endX, float endY) {
		p.add(endX - CellHelper.CELL_CENTER_X, endY - CellHelper.CELL_CENTER_Y);
		// double angle = Math.atan2(endY - iniY, endX - iniX);
		// float stepLenght = 0;
		// if(iniY == endY)
		// stepLenght = 36.3f;
		// else
		// stepLenght = 35.9f;
		//
		// float dx = (float) (stepLenght * Math.cos(angle));
		// float dy = (float) (stepLenght * Math.sin(angle));
		// while (Math.abs(endY - iniY) > 10) {
		// p.add(iniX, iniY);
		// iniX += dx;
		// iniY += dy;
		// }
	}
}
