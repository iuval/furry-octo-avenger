package pruebas.Renders.helpers.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ToggleButton extends Component {
	private TextureRegion normalRegion;
	private TextureRegion pressedRegion;
	private boolean pressed = false;

	public void setTextureRegion(TextureRegion normal, TextureRegion pressed) {
		this.normalRegion = normal;
		this.pressedRegion = pressed;
	}

	public void toggle() {
		pressed = !pressed;
	}

	public void draw(float dt, SpriteBatch batch) {
		if (pressed && pressedRegion != null) {
			batch.draw(pressedRegion, getX(), getY(), getW(), getH());
		} else if (normalRegion != null) {
			batch.draw(normalRegion, getX(), getY(), getW(), getH());
		}
	}
}
