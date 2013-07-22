package pruebas.Entities;

public class User {

	private String email;
	private String nick;
	//Lista de partidas, Icon/Avatar
	
	public User(String email, String nick){
		this.email = email;
		this.nick = nick;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getNick(){
		return nick;
	}
}
