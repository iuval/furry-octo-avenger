package pruebas.Entities;

import pruebas.Renders.helpers.PathManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Path {
	public enum TYPE {
		MOVE, ATTACK
	}

	public float time;
	public Array<Vector2> dots;
	public int bigDotIndex;
	private TextureRegion smallBall;
	private TextureRegion bigBall;
	private TYPE type;

	public Path(TYPE type) {
		this.type = type;
		smallBall = PathManager.getSmallBallTexture(type);
		bigBall = PathManager.getBigBallTexture(type);

		dots = new Array<Vector2>();
		bigDotIndex = 0;
	}

	public TYPE getType() {
		return type;
	}

	public void add(float x, float y) {
		dots.add(new Vector2(x, y));
	}

	public void clear() {
		dots.clear();
	}

	public void draw(SpriteBatch batch, float dt) {
		if (dots.size > 0) {
			time += dt;
			if (time >= PathManager.BIG_DOT_TIME) {
				time = 0;
				bigDotIndex = (bigDotIndex + 1) % dots.size;
			}
			Vector2 v;
			for (int i = 0; i < dots.size; i++) {
				v = dots.get(i);
				if (i == bigDotIndex)
					batch.draw(bigBall, v.x, v.y);
				else
					batch.draw(smallBall, v.x, v.y);
			}
		}
	}
}
