package pruebas.Renders;

import pruebas.Entities.Unit;
import pruebas.Util.SuperAnimation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UnitRender {
	public enum ANIM {
		idle, fight, walk
	}

	public Unit unit;

	public SuperAnimation idleAnim;
	public SuperAnimation fightAnim;
	public SuperAnimation walkAnim;
	public SuperAnimation currnetAnim;

	public UnitRender() {
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

	public void draw(SpriteBatch batch, float delta) {
		currnetAnim.update(delta, true);
		batch.draw(currnetAnim.current, unit.getX(), unit.getY());
	}
}
