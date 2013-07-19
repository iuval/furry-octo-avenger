package pruebas.CrystalClash;

import pruebas.Accessors.ActorAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SplashScreen implements Screen {

	//private SpriteBatch batch;
	private Stage stage;
	private TweenManager tweenManager;
	private OrthographicCamera camera;

	private Texture backgroundTexture;
	private Texture crystalTexture;
	private Texture nameTexture;

	private Image background;
	private Image crystal;
	private Image name;
	
	@Override
	public void show() {
		//batch = new SpriteBatch();
		stage = new Stage();
		tweenManager = new TweenManager();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		backgroundTexture = new Texture(Gdx.files.internal("data/Images/Splash/splash_background.jpg"));
		crystalTexture = new Texture(Gdx.files.internal("data/Images/Splash/splash_crystal.png"));
		nameTexture = new Texture(Gdx.files.internal("data/Images/Splash/splash_name.png"));

		background = new Image(backgroundTexture);
		crystal = new Image(crystalTexture);
		name = new Image(nameTexture);

		background.setSize(CrystalClash.WIDTH, CrystalClash.HEIGHT);
		crystal.setPosition(CrystalClash.WIDTH / 2 - crystal.getWidth() / 2, CrystalClash.HEIGHT / 2 - crystal.getHeight() / 2);
		name.setPosition(CrystalClash.WIDTH / 2 - name.getWidth() / 2, 0);

		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Timeline.createSequence()
				.beginParallel()
				.push(Tween.set(crystal, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(crystal, ActorAccessor.Y).target(crystal.getHeight()))
				.push(Tween.set(name, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(name, ActorAccessor.Y).target(-name.getHeight()))
				.push(Tween.to(crystal, ActorAccessor.ALPHA, 3).target(1))
				.push(Tween.to(crystal, ActorAccessor.Y, 3).target(CrystalClash.HEIGHT / 2 - crystal.getHeight() / 2 + 40))
				.push(Tween.to(name, ActorAccessor.ALPHA, 3).target(1))
				.push(Tween.to(name, ActorAccessor.Y, 3).target(40)).end()
				.beginParallel()
				.push(Tween.to(background, ActorAccessor.ALPHA, 3).target(0))
				.push(Tween.to(crystal, ActorAccessor.ALPHA, 3).target(0))
				.push(Tween.to(name, ActorAccessor.ALPHA, 3).target(0)).end()
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						((Game) Gdx.app.getApplicationListener())
								.setScreen(new GameEngine());
					}
				}).start(tweenManager);

		stage.setCamera(camera);
		stage.addActor(background);
		stage.addActor(crystal);
		stage.addActor(name);
		
		tweenManager.update(Float.MIN_VALUE);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//batch.setProjectionMatrix(camera.combined);
		//batch.begin();
		//background.draw(batch, 1);
		//crystal.draw(batch, 1);
		//name.draw(batch, 1);
		//batch.end();

		stage.act(delta);
		stage.draw();
		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		//batch.dispose();
		stage.dispose();
		backgroundTexture.dispose();
		crystalTexture.dispose();
		nameTexture.dispose();
	}
}
