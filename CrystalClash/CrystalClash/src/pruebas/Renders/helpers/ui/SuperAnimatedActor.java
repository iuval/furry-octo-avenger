package pruebas.Renders.helpers.ui;

import pruebas.Renders.UnitRender.FACING;
import pruebas.Util.SuperAnimation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SuperAnimatedActor extends Actor {
	public final SuperAnimation anim;
	private FACING at;

	public SuperAnimatedActor(float time, float[] frameDuration, boolean loop, FACING at, TextureRegion... keyFrames) {
		this(new SuperAnimation(time, frameDuration, keyFrames), loop, at);
	}

	public SuperAnimatedActor(SuperAnimation anim, boolean loop, FACING at) {
		this.anim = anim;
		setSize(anim.getFirstWidth(), anim.getFirstHeight());
		this.anim.setLooping(loop);
		this.at = at;
	}

	@Override
	public void act(float delta) {
		anim.update(delta, at);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		anim.draw(batch, getX(), getY());
	}
}
