package pruebas.Entities;

import pruebas.Renders.UnitRender;
import pruebas.Renders.helpers.UnitHelper;

public class Unit extends GameObject {

	private int hitPoints;
	private int totalHitPoints;

	private int damage;
	private int velicity;
	private int range;
	private String unitName;
	private boolean enemy;
	private UnitRender render;

	public Unit(String unitName, boolean enemy, int hp) {
		this.enemy = enemy;
		if (render == null) {
			this.render = UnitHelper.getUnitRender(unitName);
			this.render.setUnit(this);
		}
		this.setHP(hp);
		this.unitName = unitName;
	}

	public Unit(String unitName) {
		this(unitName, false, 0);
	}

	public UnitRender getRender() {
		return render;
	}

	public String getName() {
		return unitName;
	}

	public int getHP() {
		return hitPoints;
	}

	public int getTotalHP() {
		return totalHitPoints;
	}

	public void setHP(int hp) {
		totalHitPoints = hp;
		hitPoints = hp;
		getRender().updateHp();
	}

	public void damage(float damage) {
		hitPoints -= damage;
		getRender().updateHp();
	}

	public boolean isEnemy() {
		return enemy;
	}
}
