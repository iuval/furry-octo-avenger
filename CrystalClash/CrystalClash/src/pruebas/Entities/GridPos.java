package pruebas.Entities;

public class GridPos {
	private int X = 0;
	private int Y = 0;

	public GridPos(int x, int y) {
		X = x;
		Y = y;
	}

	public boolean Equals(Object obj) {
		GridPos o = (GridPos) obj;
		return o.X == X && o.Y == Y;
	}

	public void setX(int x) {
		this.X = x;
	}

	public void setY(int y) {
		this.Y = y;
	}

	public float getX() {
		return X;
	}

	public float getY() {
		return Y;
	}
}
