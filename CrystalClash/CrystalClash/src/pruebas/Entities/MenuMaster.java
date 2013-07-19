package pruebas.Entities;

public class MenuMaster {

	private Menu currentMenu;
	private MenuLogIn menuLogIn;
	
	public MenuMaster(){
		menuLogIn = new MenuLogIn();
		currentMenu = menuLogIn;
	}
	
	public void update(float dt){
		currentMenu.update(dt);
	}
	
	public Menu getCurrentMenu(){
		return currentMenu;
	}
}
