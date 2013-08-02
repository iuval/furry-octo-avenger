package pruebas.Renders.helpers.ui;

import java.util.ArrayList;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UnitList extends Sprite {

	ArrayList<UnitListItem> units;

	public UnitList() {
		units = new ArrayList<UnitListItem>();
	}

	public void addUnitItem(UnitListItem u) {
		if (u != null) {
			units.add(u);
			u.setPosition(getX(), getY() + units.size() * 74);
		}
	}

	public void draw(SpriteBatch batch, float dt) {
		for (int i = 0; i < units.size(); i++) {
			units.get(i).draw(batch, dt);

		}
	}

	public UnitListItem getItemAt(float x, float y) {
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).hit(x, y)) {
				return units.get(i);
			}
		}
		return null;
	}

	public boolean hit(float x, float y) {
		return x >= getX() && x <= getX() + getWidth() && y >= getY()
				&& y <= getY() + getHeight();
	}
}
