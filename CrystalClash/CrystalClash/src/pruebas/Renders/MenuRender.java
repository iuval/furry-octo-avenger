package pruebas.Renders;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class MenuRender implements InputProcessor {

	public MenuRender(){
	}
	
	public abstract void render(float dt, Stage stage);
}
