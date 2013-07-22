package pruebas.Controllers;

import pruebas.Renders.MenuMatchesRender;
import pruebas.Renders.MenuRender;

public class MenuMatches extends Menu {

	@Override
	public void update(float delta) {
	}

	@Override
	public MenuRender getRender() {
		return MenuMatchesRender.getInstance(this);
	}

}
