package pruebas.Entities;

import pruebas.Renders.MenuMatchesRender;
import pruebas.Renders.MenuRender;

public class MenuMatches extends Menu {
	private User user;
	// Lista de partidas

	public MenuMatches(User user) {
		this.user = user;
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public MenuRender getRender() {
		return MenuMatchesRender.getInstance(this);
	}
	
	public User getUser(){
		return user;
	}
}
