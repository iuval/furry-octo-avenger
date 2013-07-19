package pruebas.Entities;

import pruebas.Renders.MenuLogInRender;
import pruebas.Renders.MenuRender;

public class MenuLogIn extends Menu{

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

	public void logIn(String email) {
		this.email = email;
		// TODO: LogIn con el Servidor (falta pass)	
	}
	
	public void singIn(String email, String nick) {
		this.email = email;
		this.nick = nick;
		// TODO: SingIn en el Servidor (nick pasa a ser pass)
	}
}
