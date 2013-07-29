package pruebas.Controllers;

import pruebas.Renders.MenuGamesRender;
import pruebas.Renders.MenuRender;

public class MenuGames extends Menu {

	private static MenuGames instance;

	public static MenuGames getInstance() {
		if (instance == null)
			instance = new MenuGames();
		return instance;
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public MenuRender getRender() {
		return MenuGamesRender.getInstance(this);
	}

	public void listGamesSuccess(String[][] games) {
		((MenuGamesRender) getRender()).listGamesSuccess(games);
	}

	public void listGamesError(String message) {
		((MenuGamesRender) getRender()).listGamesError(message);
	}
}
