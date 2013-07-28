package pruebas.Controllers;

import pruebas.Renders.MenuGamesRender;
import pruebas.Renders.MenuRender;

public class MenuGames extends Menu {

	@Override
	public void update(float delta) {
	}

	@Override
	public MenuRender getRender() {
		return MenuGamesRender.getInstance(this);
	}

}
