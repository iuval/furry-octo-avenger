package pruebas.Renders.helpers.ui;

import pruebas.CrystalClash.CrystalClash;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UnitListItem extends Sprite {
	private String unitName;
	private TextureRegion unitIcon;
	private BitmapFont font;

	public UnitListItem(String unitName, TextureRegion icon, BitmapFont font) {
		this.unitName = unitName;
		this.unitIcon = icon;
		this.font = font;
		setSize(CrystalClash.WIDTH / 2, unitIcon.getRegionHeight());

	}

	public void draw(SpriteBatch batch, float delta) {
		batch.draw(unitIcon, getX(), getY());
		font.draw(batch, unitName, getX() + 74, getY() + 37);
	}

	public boolean hit(float x, float y) {
		return x >= getX() && x <= getX() + getWidth() && y >= getY()
				&& y <= getY() + getHeight();
	}

	public String getUnitName() {
		return unitName;
	}
}
