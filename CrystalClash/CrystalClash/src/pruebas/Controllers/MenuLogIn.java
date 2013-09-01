package pruebas.Controllers;

import pruebas.Networking.ServerDriver;
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

	public void sendLogIn(String email, String nick) {
		this.email = email;
		this.nick = nick;

		ServerDriver.sendLogIn(email, nick);
	}

	public void sendLogInError(String message) {
		render.authenticateError(message);
	}

	public void serverError(String message) {
		System.out.println(message);
	}

	public void sendSignIn(String email, String nick) {
		this.email = email;
		this.nick = nick;

		ServerDriver.sendSignIn(email, nick);
	}
}
