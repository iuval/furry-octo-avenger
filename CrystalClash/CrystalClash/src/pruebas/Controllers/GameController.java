package pruebas.Controllers;

import java.util.Enumeration;
import java.util.Hashtable;

import pruebas.Entities.User;
import pruebas.Renders.GameEngine;
import pruebas.Util.UnitSharedDataPrefReader;
import pruebas.Util.UnitStatsPrefReader;

public class GameController {

	private static GameController instancia;

	public static GameController getInstancia() {
		if (instancia == null)
			instancia = new GameController();
		return instancia;
	}

	private boolean dataLoaded = false;
	private User currentUser;
	private Hashtable<String, int[]> unitValues;

	public float unitMaxLife = 0;
	public float unitMaxAttack = 0;
	public float unitMaxSpeed = 0;
	public int unitsPerPlayer = 0;

	public void setUser(User user) {
		currentUser = user;
	}

	public User getUser() {
		return currentUser;
	}

	public void loadUnitsStats() {
		if (!dataLoaded) {
			unitValues = UnitStatsPrefReader.load("data/Units/stats.pref");
			int[] shared = UnitSharedDataPrefReader.load("data/Units/shared.pref");
			unitMaxLife = shared[0];
			unitMaxAttack = shared[1];
			unitMaxSpeed = shared[2];
			unitsPerPlayer = shared[3];

			dataLoaded = true;
		}
	}

	public int getUnitElement(String unitName) {
		return unitValues.get(unitName)[0];
	}

	public int getUnitLife(String unitName) {
		return unitValues.get(unitName)[1];
	}

	public int getUnitLifeInScale(String unitName) {
		return (int) ((getUnitLife(unitName) * 10) / unitMaxLife);
	}

	public int getUnitAttack(String unitName) {
		return unitValues.get(unitName)[2];
	}

	public int getUnitAttackInScale(String unitName) {
		return (int) ((getUnitAttack(unitName) * 10) / unitMaxAttack);
	}

	public int getUnitSpeed(String unitName) {
		return unitValues.get(unitName)[3];
	}

	public int getUnitSpeedInScale(String unitName) {
		return (int) ((getUnitSpeed(unitName) * 10) / unitMaxSpeed);
	}

	public int getUnitRange(String unitName) {
		return unitValues.get(unitName)[4];
	}

	public Enumeration<String> getUnitNames() {
		return unitValues.keys();
	}

	public void logOut() {
		this.currentUser = null;
		GameEngine.getInstance().openMenuLogIn();
	}
}
