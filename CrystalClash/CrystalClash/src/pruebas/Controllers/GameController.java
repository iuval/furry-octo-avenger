package pruebas.Controllers;

import java.util.Enumeration;
import java.util.Hashtable;

import pruebas.Entities.Unit;
import pruebas.Entities.User;
import pruebas.Networking.ServerDriver;
import pruebas.Renders.GameEngine;
import pruebas.Renders.helpers.ui.MessageBox;
import pruebas.Util.Profile;
import pruebas.Util.ProfileService;
import pruebas.Util.UnitSharedDataPrefReader;
import pruebas.Util.UnitStatsPrefReader;

public class GameController {

	private static boolean dataLoaded = false;
	private static User currentUser;
	public static ProfileService profileService = new ProfileService();
	private static Hashtable<String, int[]> unitValues;

	public static float unitMaxLife = 0;
	public static float unitMaxAttack = 0;
	public static float unitMaxSpeed = 0;
	public static int unitsPerPlayer = 0;

	public static void setUser(User user) {
		currentUser = user;
	}

	public static User getUser() {
		return currentUser;
	}

	public static void loadUnitsStats() {
		if (!dataLoaded) {
			unitValues = UnitStatsPrefReader.load("data/prefs/stats.pref");

			int[] shared = UnitSharedDataPrefReader.load("data/prefs/shared.pref");
			unitMaxLife = shared[0];
			unitMaxAttack = shared[1];
			unitMaxSpeed = shared[2];
			unitsPerPlayer = shared[3];

			dataLoaded = true;
		}
	}

	public static String getUnitElement(String unitName) {
		switch (unitValues.get(unitName)[0]) {
		case Unit.ELEMENT_FIRE:
			return "fire";
		case Unit.ELEMENT_EARTH:
			return "earth";
		case Unit.ELEMENT_WIND:
			return "wind";
		case Unit.ELEMENT_WATER:
			return "water";
		case Unit.ELEMENT_DARKNESS:
			return "darkness";
		default:
			return "fire";
		}
	}

	public static int getUnitElementIndex(String unitName) {
		return unitValues.get(unitName)[0];
	}

	public static int getUnitTypeIndex(String unitName) {
		return unitValues.get(unitName)[1];
	}

	public static int getUnitLife(String unitName) {
		return unitValues.get(unitName)[2];
	}

	public static int getUnitLifeInScale(String unitName) {
		return (int) ((getUnitLife(unitName) * 10) / unitMaxLife);
	}

	public static int getUnitAttack(String unitName) {
		return unitValues.get(unitName)[3];
	}

	public static int getUnitAttackInScale(String unitName) {
		return (int) ((getUnitAttack(unitName) * 10) / unitMaxAttack);
	}

	public static int getUnitSpeed(String unitName) {
		return unitValues.get(unitName)[4];
	}

	public static int getUnitSpeedInScale(String unitName) {
		return (int) ((getUnitSpeed(unitName) * 10) / unitMaxSpeed);
	}

	public static int getUnitRange(String unitName) {
		return unitValues.get(unitName)[5];
	}

	public static Enumeration<String> getUnitNames() {
		return unitValues.keys();
	}

	public static boolean willTryToLogin() {
		Profile p = profileService.retrieveProfile();
		if (p.hasUserAndPassword()) {
			logIn(p.getUserEmail(), p.getUserPassword());
			return true;
		}
		return false;
	}

	public static boolean isTutorialDone() {
		Profile p = profileService.retrieveProfile();
		return p.isTutorialDone();
	}

	public static void setTutorialDone() {
		Profile p = profileService.retrieveProfile();
		p.setTutorialDone();
	}

	// TODO: Borrar, es solo para probar
	public static void setTutorialNotDone() {
		Profile p = profileService.retrieveProfile();
		p.setTutorialNotDone();
	}

	public static void saveProfile() {
		profileService.persist();
	}

	public static void logIn(String email, String password) {
		MessageBox.build()
				.setMessage("Authenticating...\nIn this last step we will require the blood of a virgin.")
				.noButtonsLayout()
				.setCallback(null)
				.show();
		ServerDriver.sendLogIn(email, password);
	}

	public static void logInSuccess(String userId, String email, String password) {
		profileService.retrieveProfile().setUserEmail(email);
		profileService.retrieveProfile().setUserPassword(password);
		setUser(new User(userId, email, email));
		GameEngine.getInstance().openMenuGames();
	}

	public static void logOut() {
		profileService.retrieveProfile().setUserPassword("");
		profileService.persist();
		currentUser = null;
		GameEngine.getInstance().openMenuLogIn();
	}
}
