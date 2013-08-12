package pruebas.Renders;

import pruebas.Entities.Unit;
import pruebas.Util.SuperAnimation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class UnitRender {
	public enum FACING {
		right, left
	}

	public enum ANIM {
		idle, fight, walk
	}

	public Unit unit;

	public SuperAnimation idleAnim;
	public SuperAnimation fightAnim;
	public SuperAnimation walkAnim;
	private SuperAnimation currnetAnim;
	private Texture hp;
	private float hpWidth;
	private FACING facing = FACING.right;
	private boolean ghostly;

	public UnitRender() {
		ghostly = false;
		Pixmap pixmap = new Pixmap(50, 10, Format.RGBA4444); // or RGBA8888
		pixmap.setColor(0, 1, 0, 1);
		pixmap.fill();
		hp = new Texture(pixmap); // must be manually disposed
		hpWidth = 50;
		pixmap.dispose();
	}

	public void setFacing(FACING at) {
		facing = at;
	}

	public void setAnimation(ANIM anim) {
		switch (anim) {
		case idle: {
			currnetAnim = idleAnim;
		}
			break;
		case fight: {
			currnetAnim = fightAnim;
		}
			break;
		case walk: {
			currnetAnim = walkAnim;
		}
			break;
		}
	}

	public void updateHp() {
		hpWidth = (unit.getHP() * 50) / unit.getTotalHP();
	}

	public void draw(SpriteBatch batch, float dt) {
		currnetAnim.update(dt, true, facing);
		currnetAnim.draw(batch, dt, unit.getX(), unit.getY(), ghostly);
		batch.draw(hp, unit.getX(), unit.getY(), hpWidth, 10);
	}

	public UnitRender clone() {
		UnitRender ren = new UnitRender();
		ren.idleAnim = idleAnim.clone();
		ren.fightAnim = fightAnim.clone();
		ren.walkAnim = walkAnim.clone();
		ren.setAnimation(ANIM.idle);
		return ren;
	}
	
	public void setGhostly(){
		ghostly = true;
	}
}
