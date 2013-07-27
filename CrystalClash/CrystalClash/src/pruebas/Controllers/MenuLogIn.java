package pruebas.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonValue;

import pruebas.Networking.ServerDriver;
import pruebas.Renders.MenuLogInRender;
import pruebas.Renders.MenuRender;

public class MenuLogIn extends Menu {
	
	private static MenuLogIn instance;
	public static MenuLogIn getInstance(){
		if(instance == null) instance = new MenuLogIn();
		return instance;
	}
	
	private String email;
	private String nick;

	public MenuLogIn() {
		email = "";
		nick = "";
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public MenuRender getRender() {
		return MenuLogInRender.getInstance(this);
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

		ServerDriver.LogIn(email, nick);

		return true;
	}

	public void authenticateSuccess(String userId, String name) {
		((MenuLogInRender) getRender()).authenticateSuccess(userId, name);
	}

	public void authenticateError(String message) {
		((MenuLogInRender) getRender()).authenticateError(message);
	}

	public void serverError(String message) {
		System.out.println(message);
	}

	public boolean singIn(String email, String nick) {
		this.email = email;
		this.nick = nick;

		return true;
	}

	public void logIn() {
		MenuMaster.changeMenuToGames();
	}
}
