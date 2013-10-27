package pruebas.renders.helpers.ui;


public class Component {
	protected float x;
	protected float y;
	protected float h;
	protected float w;

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getW() {
		return w;
	}

	public void setH(float h) {
		this.h = h;
	}

	public float getH() {
		return h;
	}

	public void setSize(float h, float w) {
		this.h = h;
		this.w = w;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public boolean hit(float x, float y) {
		return x >= getX() && x <= getX() + getW() && y >= getY()
				&& y <= getY() + getH();
	}
}
