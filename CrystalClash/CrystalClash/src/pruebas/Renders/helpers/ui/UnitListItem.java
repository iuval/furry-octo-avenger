package pruebas.Renders.helpers.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UnitListItem extends Panel {
	private TextureRegion unitItem;
	private String unitName;
	private BitmapFont font;

	public UnitListItem(String unitName, TextureRegion icon, BitmapFont font) {
		this.unitName = unitName;
		this.setTextureRegion(icon);
		this.unitItem = icon;
		this.font = font;
	}

	@Override
	public void draw(float dt, SpriteBatch batch) {
		super.draw(dt, batch);
		batch.draw(this.unitItem, getX(), getY(), 74, 74);
		font.draw(batch, unitName, getX() + 74, getY() + 37);
	}

	public String getUnitName() {
		return unitName;
	}
}
