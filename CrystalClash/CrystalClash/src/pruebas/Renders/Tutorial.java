package pruebas.Renders;

import javax.sound.midi.ControllerEventListener;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Unit;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.helpers.ResourceHelper;
import pruebas.Renders.helpers.ui.MessageBox;
import pruebas.Renders.helpers.ui.MessageBoxCallback;
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
	private float arrowX;
	private float arrowY;
	
	private Image pointingHand;

	private Array<String> messages;
	private int messageIndex;

	public Tutorial(WorldController world) {
		super(world);
		messageIndex = 0;
		arrowX = 0;
		arrowY = CrystalClash.HEIGHT + 20;
		
		load();
		readTutorialScript();
		GameEngine.hideLoading();
		pushEnterAnimation(Timeline.createParallel()).start(tweenManager);
		GameController.getInstance().loadUnitsStats();
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
		
		final MessageBoxCallback confirmation = new MessageBoxCallback() {
			@Override
			public void onEvent(int type, Object data) {
				if (type == MessageBoxCallback.YES) {
					
				} else {
					MessageBox.build().hide();
				}
			}
		};
		btnSkip = new TextButton("Skip", ResourceHelper.getButtonStyle());
		btnSkip.setPosition(-btnSkip.getWidth(), CrystalClash.HEIGHT - btnSkip.getHeight());
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String text = "We have just started...";
				if(messageIndex >= messages.size / 2)
					text = "We are half road down...";
				if(messageIndex >= messages.size - 8)
					text = "We are almost finished...";
				
				MessageBox.build()
						.setMessage("Are you sure you want to leave?\n" + text)
						.twoButtonsLayout("Yes, i got it", "No, let's go on")
						.setCallback(confirmation)
						.setHideOnAction(false)
						.show();
			}
		});

		arrow = new Image(ResourceHelper.getTexture("data/Images/InGame/selector_arrow.png"));
		arrow.setPosition(arrowX, arrowY);
		// pointingHand = new Image(ResourceHelper.getTexture("data/Images/Tutorial/message_balloon.png"));

		addActor(fireArcher);
		addActor(balloon);
		addActor(lblMessage);
		addActor(btnNext);
		addActor(btnSkip);
		addActor(arrow);
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
		action(messageIndex);
	}
	
	private void action(int index){
		switch(index){
		case 2:
			Unit assassin = new Unit("wind_assassin", false);
			world.addUnit(assassin, 500, 500);
			moveArrow(assassin);
			break;
		case 7:
			Unit tank = new Unit("earth_tank", true);
			tank.getRender().setFacing(FACING.left);
			world.addUnit(tank, 900, 500);
			break;
		case 16:
			Unit archer = new Unit("fire_archer", false);
			world.addUnit(archer, 250, 500);
			break;
		}
	}
	
	private void moveArrow(Unit u) {
		if (u != null) {
			if (arrow.getY() >= CrystalClash.HEIGHT) {
				arrow.setPosition(u.getX(), CrystalClash.HEIGHT + 20);
			}
			arrowX = u.getX();
			arrowY = u.getY() + 120;
		} else {
			arrowY = CrystalClash.HEIGHT + 20;
		}

		tweenManager.killAll();
		Timeline.createParallel()
				.push(Tween.to(arrow, ActorAccessor.X, CrystalClash.ANIMATION_SPEED)
						.target(arrowX))
				.push(Tween.to(arrow, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED)
						.target(arrowY))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						arrow.setPosition(arrowX, arrowY);
						arrowAnimation();
					}
				}).start(tweenManager);
	}

	private void arrowAnimation() {
		Timeline.createSequence()
				.push(Tween.set(arrow, ActorAccessor.Y).target(arrowY))
				.push(Tween.to(arrow, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(arrowY - 10))
				.push(Tween.to(arrow, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(arrowY)).repeat(Tween.INFINITY, 0).start(tweenManager);
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