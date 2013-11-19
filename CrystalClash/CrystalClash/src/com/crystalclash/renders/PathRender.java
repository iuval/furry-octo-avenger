package com.crystalclash.renders;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.crystalclash.entities.Path;
import com.crystalclash.renders.helpers.PathManager;

public class PathRender extends Path {
	public enum TYPE {
		MOVE, ATTACK
	}

	public static final float DOT_CENTER_X = 19f;
	public static final float DOT_CENTER_Y = 19f;

	public float time;
	public int bigDotIndex;
	private TextureRegion smallBall;
	private TextureRegion bigBall;
	private TYPE type;

	public PathRender(TYPE type) {
		this.type = type;
		smallBall = PathManager.getSmallBallTexture(type);
		bigBall = PathManager.getBigBallTexture(type);
		bigDotIndex = 0;
	}

	public TYPE getType() {
		return type;
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
