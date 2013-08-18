package pruebas.Controllers;

import pruebas.Entities.User;
import pruebas.Networking.ServerDriver;
import pruebas.Renders.GameEngine;
import pruebas.Renders.MenuLogInRender;

public class MenuLogIn {

	private static MenuLogIn instance;

	public static MenuLogIn getInstance() {
		if (instance == null)
			instance = new MenuLogIn();
		return instance;
	}

	private String email;
	private String nick;
	private MenuLogInRender render;

	private MenuLogIn() {
		render = MenuLogInRender.getInstance(this);
		email = "";
		nick = "";
	}

	public MenuLogInRender getRender() {
		return render;
	}

	public String getEmail() {
		return email;
	}

	public String getNick() {
		return nick;
	}

	public boolean authenticate(String email, String nick) {
		this.email = email;
		this.nick = nick;

		ServerDriver.logIn(email, nick);

		return true;
	}

	public void authenticateSuccess(String userId, String name) {
		GameController.getInstancia().setUser(new User(userId, name, name));
		GameEngine.getInstance().openMenuGames();
	}

	public void authenticateError(String message) {
		render.authenticateError(message);
	}

	public void serverError(String message) {
		System.out.println(message);
	}

	public boolean signIn(String email, String nick) {
		this.email = email;
		this.nick = nick;

		ServerDriver.signIn(email, nick);

		return true;
	}

	public void resetRender() {
		render.reset();
	}
}
