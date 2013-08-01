package pruebas.Entities;

import pruebas.Renders.UnitRender;
import pruebas.Renders.helpers.UnitHelper;

public class Unit extends GameObject {

	private int lifePoints;
	private int damage;
	private int velicity;
	private int range;
	private UnitRender render;

	public Unit(String unitName) {
		if (render == null) {
			this.render = UnitHelper.getUnitRender(unitName);
			this.render.unit = this;
		}
	}

	public UnitRender getRender() {
		return render;
	}
}
