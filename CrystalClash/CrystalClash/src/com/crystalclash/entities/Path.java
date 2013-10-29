package com.crystalclash.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Path {
	public Array<Vector2> dots;

	public Path() {
		dots = new Array<Vector2>();
	}

	public void add(float x, float y) {
		dots.add(new Vector2(x, y));
	}

	public void clear() {
		dots.clear();
	}
}
