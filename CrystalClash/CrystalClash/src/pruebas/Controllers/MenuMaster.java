package pruebas.Controllers;

import pruebas.Renders.GameEngine;

public class MenuMaster {
	private static MenuMaster instance;

	public static MenuMaster getInstance() {
		if (instance == null)
			instance = new MenuMaster();
		instance.init();
		return instance;
	}

	private static Menu currentMenu;

	private static MenuLogIn menuLogInInstance;
	private static MenuGames menuMatchesInstance;

	private MenuMaster() {
	}

	private void init() {
		changeMenuToLogIn();
	}

	public void update(float dt) {
		currentMenu.update(dt);
	}

	public Menu getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(Menu menu) {
		currentMenu = menu;
	}

	public static MenuLogIn getMenuLogIn(){
		if (menuLogInInstance == null)
			menuLogInInstance = new MenuLogIn();
		return menuLogInInstance;
	}
	
	public static void changeMenuToLogIn() {
		MenuLogIn aux = getMenuLogIn();
		aux.resetRender();
		currentMenu = aux;
	}

	public static MenuGames getMenuGames(){
		if (menuMatchesInstance == null)
			menuMatchesInstance = new MenuGames();
		return menuMatchesInstance;
	}
	
	public static void changeMenuToGames() {
		currentMenu = getMenuGames();
	}
}
