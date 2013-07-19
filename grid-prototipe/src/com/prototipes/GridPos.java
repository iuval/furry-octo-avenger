package com.prototipes;

public class GridPos {
	public int X;
	public int Y;

	public GridPos(int x, int y) {
		X = x;
		Y = y;
	}

	public boolean Equals(Object obj) {
		GridPos o = (GridPos) obj;
		return o.X == X && o.Y == Y;
	}
}
