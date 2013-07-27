package pruebas.Controllers;

import pruebas.Entities.User;

public class GameController {

	private static GameController instancia;

	public static GameController getInstancia() {
		if (instancia == null)
			instancia = new GameController();
		return instancia;
	}

	private User currentUser;

	public void setUser(User user) {
		currentUser = user;
	}

	public User getUser() {
		return currentUser;
	}
}
