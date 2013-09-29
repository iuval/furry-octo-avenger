package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Accessors.UnitAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.MoveUnitAction;
import pruebas.Renders.UnitRender.FACING;
import pruebas.Renders.UnitRender.STATE;
import pruebas.Renders.helpers.CellHelper;
import pruebas.Renders.helpers.ResourceHelper;
import pruebas.Renders.helpers.ui.MessageBox;
import pruebas.Renders.helpers.ui.MessageBoxCallback;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class Tutorial extends GameRender {

	private static TweenManager tweenManager;

	private Image fireArcher;
	private Image balloon;
	private Label lblMessage;
	private TextButton btnNext;
	private TextButton btnSkip;
	private Image imgBtnSkipBackground;

	private Array<String> messages;
	private int messageIndex;

	private Unit assassin;
	private Unit tank;
	private Unit archer;
	private MoveUnitAction tankMove;

	private boolean blockButtons;

	public Tutorial(WorldController world) {
		super(world);
		messageIndex = 0;

		load();
		readTutorialScript();
		GameController.getInstance().loadUnitsStats();
		setData();

		world.getRender().setReadInput(false);
		world.getRender().setBlockButtons(true);

		btnNext.setDisabled(true);
		btnSkip.setDisabled(true);
		blockButtons = true;
		GameEngine.hideLoading();
	}

	public void load() {
		TextureAtlas atlas = new TextureAtlas("data/Images/InGame/options_bar.pack");
		Skin skin = new Skin(atlas);

		tweenManager = new TweenManager();
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		fireArcher = new Image(ResourceHelper.getTexture("data/Images/Tutorial/fire_archer.png"));
		fireArcher.scale(-0.55f);
		fireArcher.setPosition(-fireArcher.getWidth(), 0);

		balloon = new Image(ResourceHelper.getTexture("data/Images/Tutorial/message_balloon.png"));
		balloon.scale(-0.1f);
		balloon.setPosition(160 + fireArcher.getWidth() * 0.45f, -balloon.getHeight());

		lblMessage = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.BLACK));
		lblMessage.setPosition(balloon.getX() + 145, balloon.getTop() - 50);

		btnNext = new TextButton("Next", ResourceHelper.getNextButtonStyle());
		btnNext.setPosition(CrystalClash.WIDTH - btnNext.getWidth() - 20, -balloon.getHeight() - btnNext.getHeight());
		btnNext.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!blockButtons)
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

		imgBtnSkipBackground = new Image(skin.getRegion("exit_hud"));
		imgBtnSkipBackground.setPosition(CrystalClash.WIDTH, CrystalClash.HEIGHT - imgBtnSkipBackground.getHeight());
		TextButtonStyle skipStyle = new TextButtonStyle(
				skin.getDrawable("exit_button"),
				skin.getDrawable("exit_button_pressed"), null, ResourceHelper.getFont());
		btnSkip = new TextButton("", skipStyle);
		btnSkip.setPosition(CrystalClash.WIDTH, CrystalClash.HEIGHT - btnSkip.getHeight());
		btnSkip.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!blockButtons) {
					String text = "We have just started...";
					if (messageIndex >= messages.size / 2)
						text = "We are half road down...";
					if (messageIndex >= messages.size - 8)
						text = "We are almost finished...";

					MessageBox.build()
							.setMessage("Are you sure you want to leave?\n" + text)
							.twoButtonsLayout("Yes, i got it", "No, let's go on")
							.setCallback(confirmation)
							.setHideOnAction(false)
							.show();
				}
			}
		});

		addActor(fireArcher);
		addActor(balloon);
		addActor(lblMessage);
		addActor(btnNext);
		addActor(imgBtnSkipBackground);
		addActor(btnSkip);
	}

	private void readTutorialScript() {
		messages = new Array<String>();
		// Scene 1
		messages.add("Welcome to the front line.\nYou must lead our troop!"); // 0
		messages.add("Our goal is to defeat the\nenemies army."); // 1
		messages.add("Look!! There’s an ally\nover there."); // 2
		messages.add("Tap to select him!!"); // 3
		messages.add("Those are the thing you\ncan ask him to do."); // 4
		messages.add("Keep in mind he can only\ndo one at a time!!"); // 5
		messages.add("Here you can see his life,\ndamage and mobility"); // 6
		messages.add("Watch out !! there’s an\nenemy there. "); // 7
		messages.add("Let’s get closer to attack."); // 8
		messages.add("This seems like a good\nposition."); // 9
		messages.add("You must get there by\ndescribing the road."); // 10
		messages.add("To confirm the road tap\nthe tick."); // 11
		messages.add("Good Job!! You have ordered\nhim to move there."); // 12
		messages.add("Your orders will be executed\nonce you end your turn."); // 13
		messages.add("Tap Tick!!"); // 14
		// Scene 2
		messages.add("oh oh. . . I wasn’t counting\non the enemy moving."); // 15
		messages.add("I’m going to help him."); // 16
		messages.add("The enemy seems to be very\ntough."); // 17
		messages.add("Order him to take a\ndefensive position while\nI cover him."); // 18
		messages.add("I should attack him."); // 19
		messages.add("I’m ranged. I can attack\nfrom far away"); // 20
		messages.add("Don’t forget to tap the\ntick to confirm your orders."); // 21
		// Scene 3
		messages.add("Now the enemy seems weaker."); // 22
		messages.add("Let both attack to\ndefeat him!!"); // 23
		// Scene 4
		messages.add("Oh Sorry!! He moved. . .\nI missed!!"); // 24
		messages.add("That’s the problem we\n(ranged units) have."); // 25
		messages.add("They can dodge our shots\nby moving."); // 26
		messages.add("But if we can foresee\nwhere he’s moving,\nwe can hit him."); // 27
		messages.add("Now you are ready to\nfight on your own."); // 28
		messages.add("Defeat him to achieve\nyour goal!!"); // 29
	}

	private void setData() {
		assassin = new Unit("wind_assassin", false);
		world.addUnit(assassin, 300, 700);
		assassin.setPosition(-100, 354);
		assassin.getRender().setState(STATE.walking);

		tank = new Unit("earth_tank", true);
		tank.getRender().setFacing(FACING.left);
		world.addUnit(tank, 900, 500);
		tank.setPosition(CrystalClash.WIDTH + 100, 354);
		tank.getRender().setState(STATE.walking);
		archer = new Unit("fire_archer", false);
		world.addUnit(archer, 400, 500);
		archer.setPosition(-100, 354);
		archer.getRender().setState(STATE.walking);

		tankMove = new MoveUnitAction();
		tankMove.origin = world.cellAt(900, 500);
		tankMove.moves.add(world.cellAtByGrid(6, 3));
		tankMove.moves.add(world.cellAtByGrid(5, 4));
		world.cellAt(900, 500).setAction(tankMove);
	}

	private void next() {
		messageIndex++;
		if (messageIndex < messages.size) {
			lblMessage.setText(messages.get(messageIndex));
		} else {
			world.getRender().setReadInput(true);
			world.getRender().setBlockButtons(false);
			lblMessage.setText("");
			Timeline.createParallel()
					.push(Tween.to(fireArcher, ActorAccessor.X, CrystalClash.SLOW_ANIMATION_SPEED).target(-fireArcher.getWidth()))
					.push(Tween.to(balloon, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(-balloon.getHeight()))
					.push(Tween.to(lblMessage, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(-balloon.getHeight()))
					.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED).target(-btnNext.getHeight()))
					.start(tweenManager);
		}
		action(messageIndex);
	}

	private void action(int index) {
		switch (index) {
		case 2:
			world.getRender().moveHand(assassin);
			break;
		case 3:
			world.getRender().setReadInput(true);
			hideNext();
			break;
		case 4:
			world.getRender().setReadInput(false);
			showNext();
			break;
		case 7:
			world.getRender().moveArrow(tank);
			break;
		case 8:
			world.getRender().hideArrow();
			break;
		case 9:
			world.getRender().moveHand(675, CrystalClash.HEIGHT - 150);
			world.cellAtByGrid(5, 5).addState(Cell.MOVE_TARGET);
			showNext();
			break;
		case 10:
			world.getRender().setReadInput(true);
			// hideNext();
			break;
		case 11:
			world.getRender().moveHand(0, 125);
			world.getRender().setBlockButtons(false);
			break;
		case 12:
			world.getRender().setBlockButtons(true);
			world.getRender().hideHand();
			showNext();
			break;
		case 14:
			world.getRender().setBlockButtons(false);
			world.getRender().moveHand(0, 125);
			// hideNext();
			break;
		case 16:
			Timeline.createParallel()
					.push(Tween.to(archer, UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
							.target(CellHelper.getUnitX(world.cellAt(400, 500))).ease(TweenEquations.easeNone))
					.push(Tween.to(archer, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
							.target(CellHelper.getUnitY(world.cellAt(400, 500))).ease(TweenEquations.easeNone))
					.setCallbackTriggers(TweenCallback.COMPLETE)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							// TODO Auto-generated method stub
							archer.getRender().setState(STATE.idle);
						}
					}).start(tweenManager);
			break;
		case 18:
			world.getRender().setReadInput(true);
			world.getRender().moveHand(assassin);
			// hideNext();
			break;
		case 22:
			world.getRender().setReadInput(false);
			showNext();
			break;
		case 23:
			world.getRender().setReadInput(true);
			// hideNext();
			break;
		case 24:
			world.getRender().setReadInput(false);
			world.getRender().setBlockButtons(true);
			showNext();
			break;
		}
	}

	private void hideNext() {
		blockButtons = true;
		btnSkip.setDisabled(true);
		btnNext.setDisabled(true);
		Timeline.createSequence()
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(-btnNext.getHeight())).start(tweenManager);
	}

	private void showNext() {
		blockButtons = false;
		btnSkip.setDisabled(false);
		btnNext.setDisabled(false);
		Timeline.createSequence()
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.SLOW_ANIMATION_SPEED)
						.target(20)).start(tweenManager);
	}

	@Override
	public void clearAllChanges() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Cell cell = world.cellAt(x, y);
		if (cell != null) {
			Unit u = cell.getUnit();
			if (u != null) {
				if (!u.isEnemy()) {
					// showHUD
					world.getRender().hideHand();
					next();
				}
			}
		}
		return true;
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
		Timeline moveAssassin = Timeline.createParallel()
				.push(Tween.to(assassin, UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAt(300, 700))).ease(TweenEquations.easeNone))
				.push(Tween.to(assassin, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAt(300, 700))).ease(TweenEquations.easeNone))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						// TODO Auto-generated method stub
						assassin.getRender().setState(STATE.idle);
					}
				});

		Timeline moveTank = Timeline.createParallel()
				.push(Tween.to(tank, UnitAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitX(world.cellAt(900, 500))).ease(TweenEquations.easeNone))
				.push(Tween.to(tank, UnitAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED)
						.target(CellHelper.getUnitY(world.cellAt(900, 500))).ease(TweenEquations.easeNone))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						// TODO Auto-generated method stub
						tank.getRender().setState(STATE.idle);
					}
				});

		return t.push(moveAssassin).push(moveTank)
				.push(Tween.to(fireArcher, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(200))
				.push(Tween.to(balloon, ActorAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(0))
				.push(Tween.to(btnNext, ActorAccessor.Y, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(20))
				.push(Tween.to(imgBtnSkipBackground, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(
						CrystalClash.WIDTH - imgBtnSkipBackground.getWidth()))
				.push(Tween.to(btnSkip, ActorAccessor.X, CrystalClash.ENTRANCE_ANIMATION_SPEED).target(CrystalClash.WIDTH - btnSkip.getWidth()))
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						lblMessage.setPosition(balloon.getX() + 145, balloon.getTop() - 150);
						lblMessage.setText(messages.get(messageIndex));

						btnNext.setDisabled(false);
						btnSkip.setDisabled(false);
						blockButtons = false;
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

	@Override
	public void onAttackAction() {
	}

	@Override
	public void onDefendAction() {
	}

	@Override
	public void onMoveAction() {
	}

	@Override
	public void onUndoAction() {
	}

	@Override
	public void renderInTheBack(float dt, SpriteBatch batch) {
		tweenManager.update(dt);

	}

	@Override
	public void renderInTheFront(float dt, SpriteBatch batch) {
		// TODO Auto-generated method stub

	}
}