package pruebas.Entities;

import pruebas.Renders.MenuRender;

public abstract class Menu {
	
	public Menu(){
	}
	
	public abstract void update(float delta);
	
	public abstract MenuRender getRender();
}
