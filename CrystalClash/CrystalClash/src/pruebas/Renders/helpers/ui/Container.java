package pruebas.Renders.helpers.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Container extends Panel {
	protected Array<Panel> components;
	protected float paddingTop;

	public Container() {
		components = new Array<Panel>();
	}

	public void add(Panel child) {
		components.add(child);
	}

	public void setPaddingTop(float p) {
		paddingTop = p;
	}

	@Override
	public void draw(float dt, SpriteBatch batch) {
		super.draw(dt, batch);
		for (int i = 0; i < components.size; i++) {
			components.get(i).draw(dt, batch);
		}
	}
}
