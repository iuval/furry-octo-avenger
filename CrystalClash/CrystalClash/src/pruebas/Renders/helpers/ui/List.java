package pruebas.Renders.helpers.ui;

public class List extends Container {

	public void addUnitItem(Panel panel) {
		if (panel != null) {
			add(panel);
			panel.setSize(74, getW() - 40);
			panel.setPosition(getX() + 20, getY() + getH() - paddingTop
					- components.size * 74);
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
