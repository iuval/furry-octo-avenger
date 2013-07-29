package pruebas.Entities;

public class User {

	private String id;
	private String email;
	private String nick;

	// Lista de partidas, Icon/Avatar

	public User(String id, String email, String nick) {
		this.id = id;
		this.email = email;
		this.nick = nick;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getNick() {
		return nick;
	}
}
