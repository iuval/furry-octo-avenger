package pruebas.Controllers;

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
	private static MenuMatches menuMatchesInstance;

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
		currentMenu = getMenuLogIn();
	}

	public static MenuMatches getMenuGames(){
		if (menuMatchesInstance == null)
			menuMatchesInstance = new MenuMatches();
		return menuMatchesInstance;
	}
	
	public static void changeMenuToGames() {
		currentMenu = getMenuGames();
	}
}
