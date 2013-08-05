package pruebas.Renders.helpers.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Panel extends Component {
	private TextureRegion region;

	public void setTextureRegion(TextureRegion region) {
		this.region = region;
	}

	public void draw(float dt, SpriteBatch batch) {
		if (region != null) {
			batch.draw(region, getX(), getY(), getW(), getH());
		}
	}
}
