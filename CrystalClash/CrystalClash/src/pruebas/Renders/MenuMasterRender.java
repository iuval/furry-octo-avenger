package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.MenuMaster;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MenuMasterRender {

	private TweenManager tweenManager;
	
	private GameEngine engine;
	private MenuMaster menu;

	private Texture backgroundTexture;
	private Image background;
	
	public MenuMasterRender(GameEngine engine, MenuMaster menu) {
		this.engine = engine;
		this.menu = menu;
		
		tweenManager = new TweenManager();

		backgroundTexture = new Texture(Gdx.files.internal("data/Images/Menu/menu_background.jpg"));
		background = new Image(backgroundTexture);
		
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Timeline.createParallel()
				.push(Tween.set(background, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(background, ActorAccessor.ALPHA, 2).target(1))
				.start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
	}
	
	public void render(float dt, Stage stage) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.addActor(background);
		menu.getCurrentMenu().getRender().render(dt, stage);
		tweenManager.update(dt);
	}
	
	public void dispose(){
		backgroundTexture.dispose();
	}
}
