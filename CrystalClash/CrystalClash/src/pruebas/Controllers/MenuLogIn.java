package pruebas.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonValue;

import pruebas.Networking.ServerDriver;
import pruebas.Renders.MenuLogInRender;
import pruebas.Renders.MenuRender;

public class MenuLogIn extends Menu {
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

		Gdx.net.sendHttpRequest(ServerDriver.LogIn(email, nick), new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				JsonValue values = ServerDriver.ProcessResponce(httpResponse);
				((MenuLogInRender) getRender()).authenticateSuccess(values.getString("id"), values.getString("name"));
			}

			public void failed(Throwable t) {
				((MenuLogInRender) getRender()).authenticateError("error");
			}
		});
		
		return true;
	}

	public boolean singIn(String email, String nick) {
		this.email = email;
		this.nick = nick;

		Gdx.net.sendHttpRequest(ServerDriver.SignIn(email, nick), new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				JsonValue values = ServerDriver.ProcessResponce(httpResponse);
				((MenuLogInRender) getRender()).authenticateSuccess(values.getString("id"), values.getString("name"));
			}

			public void failed(Throwable t) {
				((MenuLogInRender) getRender()).authenticateError("error");
			}
		});
		
		return true;
	}

	public void logIn() {
		MenuMaster.changeMenuToGames();
	}
}
