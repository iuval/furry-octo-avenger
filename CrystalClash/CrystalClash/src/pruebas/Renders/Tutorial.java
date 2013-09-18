package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Renders.helpers.ResourceHelper;
import sun.font.CreatedFontTracker;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class Tutorial extends GameRender {

	private static TweenManager tweenManager;

	private Image fireArcher;
	private Image balloon;
	private Label lblMessage;
	private TextButton btnNext;
	private TextButton btnSkip;

	private Image arrow;
	private Image pointingHand;

	private Array<String> messages;
	private int messageIndex;

	public Tutorial(WorldController world) {
		super(world);
		messageIndex = 0;
		load();
		readTutorialScript();
		GameEngine.hideLoading();
		pushEnterAnimation(Timeline.createParallel()).start(tweenManager);
	}

	public void load() {
		tweenManager = new TweenManager();

		fireArcher = new Image(ResourceHelper.getTexture("data/Images/Tutorial/fire_archer.png"));
		fireArcher.setPosition(-fireArcher.getWidth(), 0);

		balloon = new Image(ResourceHelper.getTexture("data/Images/Tutorial/message_balloon.png"));
		balloon.setPosition(100 + fireArcher.getWidth(), -balloon.getHeight());

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblMessage.setPosition(balloon.getX() + 50, balloon.getTop() - 50);

		btnNext = new TextButton("Next", ResourceHelper.getButtonStyle());
		btnNext.setPosition(CrystalClash.WIDTH - btnNext.getWidth() - 20, -btnNext.getWidth());
		btnNext.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				next();
			}
		});

		btnSkip = new TextButton("Skip", ResourceHelper.getButtonStyle());
		btnSkip.setPosition(-btnSkip.getWidth(), CrystalClash.HEIGHT - btnSkip.getHeight());
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
			}
		});

		// arrow = new
		// Image(ResourceHelper.getTexture("data/Images/Tutorial/fire_archer.png"));
		// pointingHand = new
		// Image(ResourceHelper.getTexture("data/Images/Tutorial/message_balloon.png"));

		addActor(fireArcher);
		addActor(balloon);
		addActor(lblMessage);
		addActor(btnNext);
		addActor(btnSkip);
		// addActor(arrow);
		// addActor(pointingHand);

	}

	private void readTutorialScript(){
		messages = new Array<String>();
		//Scene 1
		messages.add("Welcome to the front line.\nYou must lead our troop!");
		messages.add("Our goal is to defeat the enemies army.");
		messages.add("Look!! There’s an ally over there.");
		messages.add("Tap to select him!!");
		messages.add("Those are the thing you can ask him to do.");
		messages.add("Keep in mind he can only do one at a time!!");
		messages.add("Here you can see his life, damage and mobility");
		messages.add("Watch out !! there’s an enemy there. ");
		messages.add("Let’s get closer to attack.");
		messages.add("This seems like a good position.");
		messages.add("You must get there by describing the road.");
		messages.add("To confirm the road tap the tick.");
		messages.add("Good Job!! You have ordered him to move there.");
		messages.add("Your orders will be executed once you end your turn.");
		messages.add("Tap Tick!!");
		//Scene 2
		messages.add("oh oh… I wasn’t counting on the enemy moving.");
		messages.add("I’m going to help him.");
		messages.add("The enemy seems to be very tough.");
		messages.add("Order him to take a defensive position while I cover him.");
		messages.add("I should attack him.");
		messages.add("I’m ranged. I can attack from far away");
		messages.add("Don’t forget to tap the tick to confirm your orders.");
		//Scene 3
		messages.add("Now the enemy seems weaker.");
		messages.add("Let both attack to defeat him!!");
		//Scene 4
		messages.add("Oh Sorry!! He moved… I missed!!");
		messages.add("That’s the problem we (ranged units) have.");
		messages.add("They can dodge our shots by moving.");
		messages.add("But if we can foresee where he’s moving, we can hit him.");
		messages.add("Now you are ready to fight on your own.");
		messages.add("Defeat him to achieve your goal!!");
	}
	
	private void next(){
		messageIndex++;
		if(messageIndex < messages.size){
			lblMessage.setText(messages.get(messageIndex));
		} else {
			lblMessage.setText("");
			Timeline.createParallel()
			.push(Tween.to(fireArcher, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(-fireArcher.getWidth()))
			.push(Tween.to(balloon, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(-balloon.getHeight()))
			.push(Tween.to(lblMessage, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(-balloon.getHeight()))
			.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(-btnNext.getHeight()))
			.start(tweenManager);
		}
	}
	
	private void start(Timeline t) {
		t.start(tweenManager);
	}


	@Override
	public void clearAllChanges() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(float dt, SpriteBatch batch) {
		tweenManager.update(dt);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(float screenX, float screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(float screenX, float screenY, int pointer) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t.push(Tween.to(fireArcher, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(100))
				.push(Tween.to(balloon, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(0))
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(20))
				.push(Tween.to(btnSkip, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(0))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						lblMessage.setPosition(balloon.getX() + 50, balloon.getTop() - 150);
						lblMessage.setText(messages.get(messageIndex));
					}
				});
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t;
	}

	@Override
	public boolean canSend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSend() {
		// TODO Auto-generated method stub
	}

	public void pause() {
		tweenManager.pause();
	}

	@Override
	public void resume() {
		tweenManager.resume();
	}
}