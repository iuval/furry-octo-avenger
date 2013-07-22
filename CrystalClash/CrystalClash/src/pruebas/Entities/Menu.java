package pruebas.Entities;

import pruebas.Renders.MenuRender;

public abstract class Menu {
	private MenuMaster menuMaster;
	
	public Menu(){
	}
	
	public abstract void update(float delta);
	
	public abstract MenuRender getRender();
	
	public void setMenuMaster(MenuMaster menuMaster){
		this.menuMaster = menuMaster;
	}
}
