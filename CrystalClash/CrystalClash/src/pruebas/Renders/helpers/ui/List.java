package pruebas.Renders.helpers.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class List extends Container {

	public List(TextureRegion bg) {
		super(bg);
	}

	public void addUnitItem(Panel panel) {
		if (panel != null) {
			add(panel);
			panel.setPosition(getX() + 40, getY() + getH() - paddingTop
					- components.size * panel.getH());
		}
	}

	public Panel getItemAt(float x, float y) {
		for (int i = 0; i < components.size; i++) {
			if (components.get(i).hit(x, y)) {
				return components.get(i);
			}
		}
		return null;
	}
}
