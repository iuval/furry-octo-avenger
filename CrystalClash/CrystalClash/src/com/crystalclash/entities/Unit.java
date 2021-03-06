package com.crystalclash.entities;

import com.crystalclash.controllers.GameController;
import com.crystalclash.renders.UnitRender;
import com.crystalclash.renders.UnitRender.FACING;
import com.crystalclash.renders.helpers.UnitHelper;

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
	private int originGridPosX;
	private int originGridPosY;

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

		this.hitPoints = hp;
		this.totalHitPoints = GameController.getUnitLife(unitName);
		this.damage = GameController.getUnitDamage(unitName);
		this.speed = GameController.getUnitSpeed(unitName);
		this.range = GameController.getUnitRange(unitName);
		inDefensePosition = false;

		if (render == null) {
			this.render = UnitHelper.getUnitRender(unitName, enemy);
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
		hitPoints = hp;
		getRender().updateHp();
	}

	public void setTotalHP(int hp) {
		totalHitPoints = hp;
		if (hitPoints > totalHitPoints)
			hitPoints = totalHitPoints;
		getRender().updateHp();
	}

	public void setHPsoft(int hp) {
		hitPoints = hp;
	}

	public void softDamage(float damage) {
		if (inDefensePosition)
			damage /= 2;

		hitPoints -= damage;
	}

	public float damage(float damage) {
		if (inDefensePosition)
			damage /= 2;

		hitPoints -= damage;
		getRender().updateHp();
		return damage;
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

	public int getPlayerNumber() {
		return this.playerNumber;
	}

	public void move(GridPos target) {
		this.setGridPosition(target.getX(), target.getY());
	}

	public void setOriginGridPosX(int x) {
		this.originGridPosX = x;
	}

	public void setOriginGridPosY(int y) {
		this.originGridPosY = y;
	}

	public int getOriginGridPosX() {
		return originGridPosX;
	}

	public int getOriginGridPosY() {
		return originGridPosY;
	}

	public boolean isPlayerNumber(int num) {
		return this.playerNumber == num;
	}

	public boolean isPlayerOne() {
		return this.playerNumber == 1;
	}
}
