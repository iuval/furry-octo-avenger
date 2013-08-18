package pruebas.Renders;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class MenuRender extends Group implements InputProcessor {

	public MenuRender() {
	}

	public abstract void update(float dt);

	public abstract void enterAnimation();

	public abstract void exitAnimation();
}
