package pruebas.Entities;

import pruebas.Controllers.GameController;
import pruebas.Renders.UnitRender;
import pruebas.Renders.helpers.UnitHelper;

public class Unit extends GameObject {

	public static final int ELEMENT_FIRE = 0;
	public static final int ELEMENT_EARTH = 1;
	public static final int ELEMENT_WIND = 2;
	public static final int ELEMENT_WATER = 3;
	public static final int ELEMENT_DARKNESS = 4;

	private int hitPoints;
	private int totalHitPoints;
	private int damage;
	private int movement;
	private int range;

	private String unitName;
	private boolean enemy;
	private int playerNumber;

	private boolean inDefensePosition;
	private UnitRender render;

	public Unit(String unitName, boolean enemy) {
		this(unitName, 1, enemy, GameController.getInstance().getUnitLife(unitName));
	}

	public Unit(String unitName, int num, boolean enemy, int hp) {
		this.enemy = enemy;
		this.playerNumber = num;
		this.unitName = unitName;

		GameController.getInstance().loadUnitsStats();
		this.hitPoints = hp;
		this.totalHitPoints = GameController.getInstance().getUnitLife(unitName);
		this.damage = GameController.getInstance().getUnitAttack(unitName);
		this.movement = GameController.getInstance().getUnitSpeed(unitName);
		this.range = GameController.getInstance().getUnitRange(unitName);
		inDefensePosition = false;

		if (render == null) {
			this.render = UnitHelper.getUnitRender(unitName);
			this.render.setUnit(this);
			this.render.updateHp();
		}
	}

	public Unit(String unitName) {
		this(unitName, 1, false, 0);
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
		if (inDefensePosition)
			damage /= 2;

		hitPoints -= damage;
		getRender().updateHp();
	}

	public boolean isEnemy() {
		return enemy;
	}

	public int getRange() {
		return range;
	}

	public boolean isMelee() {
		return range == 1;
	}

	public boolean isInDefensePosition() {
		return inDefensePosition;
	}

	public void setDefendingPosition(boolean position) {
		inDefensePosition = position;
	}

	public int getDamage() {
		return damage;
	}

	public boolean isAlive() {
		return hitPoints > 0;
	}

	public void setPlayerNumber(int num) {
		this.playerNumber = num;
	}

	public boolean isPlayerNumber(int num) {
		return this.playerNumber == num;
	}

	public boolean isPlayerOne() {
		return this.playerNumber == 1;
	}
}
