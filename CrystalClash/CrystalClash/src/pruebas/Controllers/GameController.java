package pruebas.Controllers;

import java.util.Enumeration;
import java.util.Hashtable;

import pruebas.Entities.User;
import pruebas.Networking.ServerDriver;
import pruebas.Renders.GameEngine;
import pruebas.Renders.helpers.ui.MessageBox;
import pruebas.Util.Profile;
import pruebas.Util.ProfileService;
import pruebas.Util.UnitSharedDataPrefReader;
import pruebas.Util.UnitStatsPrefReader;

public class GameController {

	private static GameController instancia;

	public static GameController getInstance() {
		if (instancia == null)
			instancia = new GameController();
		return instancia;
	}

	private boolean dataLoaded = false;
	private User currentUser;
	public ProfileService profileService;
	private Hashtable<String, int[]> unitValues;

	public float unitMaxLife = 0;
	public float unitMaxAttack = 0;
	public float unitMaxSpeed = 0;
	public int unitsPerPlayer = 0;

	private GameController() {
		profileService = new ProfileService();
	}

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

	public boolean willTryToLogin() {
		Profile p = profileService.retrieveProfile();
		if (p.hasUserAndPassword()) {
			logIn(p.getUserEmail(), p.getUserPassword());
			return true;
		}
		return false;
	}
	
	public boolean isTutorialDone() {
		Profile p = profileService.retrieveProfile();
		return p.isTutorialDone();
	}
	
	public void setTutorialDone() {
		Profile p = profileService.retrieveProfile();
		p.setTutorialDone();
	}
	
	// TODO: Borrar, es solo para probar
	public void setTutorialNotDone() {
		Profile p = profileService.retrieveProfile();
		p.setTutorialNotDone();
	}

	public void logIn(String email, String password) {
		MessageBox.build()
				.setMessage("Authenticating...\nIn this last step we will require the blood of a virgin.")
				.noButtonsLayout()
				.setCallback(null)
				.show();
		ServerDriver.sendLogIn(email, password);
	}

	public void logInSuccess(String userId, String email, String password) {
		profileService.retrieveProfile().setUserEmail(email);
		profileService.retrieveProfile().setUserPassword(password);
		profileService.persist();
		setUser(new User(userId, email, email));
		GameEngine.getInstance().openMenuGames();
	}

	public void logOut() {
		profileService.retrieveProfile().setUserPassword("");
		profileService.persist();
		this.currentUser = null;
		GameEngine.getInstance().openMenuLogIn();
	}
}
