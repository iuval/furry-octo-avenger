package pruebas.Entities;

import pruebas.Controllers.GameController;
import pruebas.renders.helpers.UnitHelper;
import pruebas.renders.UnitRender;
import pruebas.renders.UnitRender.FACING;

public class Unit extends GameObject {

	public static final int ELEMENT_FIRE = 0;
	public static final int ELEMENT_EARTH = 1;
	public static final int ELEMENT_WIND = 2;
	public static final int ELEMENT_WATER = 3;
	public static final int ELEMENT_DARKNESS = 4;

	public static final int TYPE_SLAYER = 0;
	public static final int TYPE_TANK = 1;
	public static final int TYPE_MAGE = 2;
	public static final int TYPE_RANGED = 3;

	private int hitPoints;
	private int totalHitPoints;
	private int damage;
	private int speed;
	private int range;

	private String unitName;
	private boolean enemy;
	private int playerNumber;

	private boolean inDefensePosition;
	private UnitRender render;

	public Unit(String unitName, boolean enemy) {
		this(unitName, -1, enemy, GameController.getUnitLife(unitName));
	}

	public Unit(String unitName, int num, boolean enemy, int hp) {
		this.enemy = enemy;
		this.playerNumber = num;
		this.unitName = unitName;

		GameController.loadUnitsStats();
		this.hitPoints = hp;
		this.totalHitPoints = GameController.getUnitLife(unitName);
		this.damage = GameController.getUnitDamage(unitName);
		this.speed = GameController.getUnitSpeed(unitName);
		this.range = GameController.getUnitRange(unitName);
		inDefensePosition = false;

		if (render == null) {
			this.render = UnitHelper.getUnitRender(unitName);
			this.render.setUnit(this);
			this.render.updateHp();

			if (isPlayerNumber(2))
				this.render.setFacing(FACING.left);
		}
	}

	public Unit(String unitName) {
		this(unitName, 1, false, 0);
	}

	public Unit(String unitName, int num) {
		this(unitName, num, false, 0);
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

	public void setHPsoft(int hp) {
		totalHitPoints = hp;
		hitPoints = hp;
	}

	public void softDamage(float damage) {
		if (inDefensePosition)
			damage /= 2;

		hitPoints -= damage;
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

	public int getSpeed() {
		return speed;
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
