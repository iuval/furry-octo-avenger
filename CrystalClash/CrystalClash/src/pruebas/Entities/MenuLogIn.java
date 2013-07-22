package pruebas.Entities;

import pruebas.Renders.MenuLogInRender;
import pruebas.Renders.MenuRender;

public class MenuLogIn extends Menu {

	private MenuMaster menuMaster;
	private String email;
	private String nick;

	public MenuLogIn(MenuMaster menuMaster) {
		email = "";
		nick = "";
		
		this.menuMaster = menuMaster;
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
		// TODO: LogIn con el Servidor (falta pass)

		return true;
	}

	public boolean singIn(String email, String nick) {
		this.email = email;
		this.nick = nick;
		// TODO: SingIn en el Servidor (nick pasa a ser pass)

		return true;
	}
	
	public void logIn(){
		menuMaster.setCurrentMenu(new MenuMatches(new User(email, nick)));
	}
}
