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
	private String password;
	private MenuLogInRender render;

	private MenuLogIn() {
		render = MenuLogInRender.getInstance(this);
		email = "";
		password = "";
	}

	public MenuLogInRender getRender() {
		return render;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public void sendLogIn(String email, String password) {
		this.email = email;
		this.password = password;

		ServerDriver.sendLogIn(email, password);
	}

	public void sendLogInError(String message) {
		render.logInError(message);
	}

	public void serverError(String message) {
		System.out.println(message);
	}

	public void sendSignIn(String email, String password) {
		this.email = email;
		this.password = password;

		ServerDriver.sendSignIn(email, password);
	}
}
