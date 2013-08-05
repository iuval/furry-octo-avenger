package pruebas.Renders.helpers.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Panel extends Component {
	protected TextureRegion background;

	public Panel(TextureRegion region) {
		this.background = region;
	}

	public void draw(float dt, SpriteBatch batch) {
		if (background != null) {
			batch.draw(background, getX(), getY(), getW(), getH());
		}
	}
}
