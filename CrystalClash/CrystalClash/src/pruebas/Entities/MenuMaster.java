package pruebas.Entities;

public class MenuMaster {

	private Menu currentMenu;

	public MenuMaster() {
		currentMenu = new MenuLogIn(this);
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
}
