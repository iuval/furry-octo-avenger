package pruebas.Renders;

import java.util.ArrayList;

import pruebas.Accessors.ActorAccessor;
import pruebas.Accessors.UnitAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.MoveUnitAction;
import pruebas.Renders.UnitRender.ANIM;
import pruebas.Renders.UnitRender.FACING;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class TurnAnimations extends GameRender {

	private TweenManager tweenManager;

	private ArrayList<MoveUnitAction> movesPlayer1;
	private ArrayList<MoveUnitAction> movesPlayer2;

	public TurnAnimations(WorldController world) {
		super(world);

		tweenManager = new TweenManager();

		init();
	}

	private void loadDummyData() {
		Unit testUnit1 = new Unit("fire_archer");
		if (world.player == 2)
			testUnit1.getRender().setFacing(FACING.left);

		world.cellGrid[5][2].placeUnit(testUnit1, world.player);

		// -------------------------------------------------------------------

		movesPlayer1 = new ArrayList<MoveUnitAction>();

		MoveUnitAction moveUnit1 = new MoveUnitAction();
		moveUnit1.origin = world.cellGrid[5][2];
		moveUnit1.moves.add(world.cellGrid[5][3]);
		moveUnit1.moves.add(world.cellGrid[6][2]);
		moveUnit1.moves.add(world.cellGrid[6][1]);
		moveUnit1.moves.add(world.cellGrid[5][1]);
		moveUnit1.moves.add(world.cellGrid[4][1]);
		moveUnit1.moves.add(world.cellGrid[4][2]);
		moveUnit1.moves.add(world.cellGrid[5][3]);
		moveUnit1.moves.add(world.cellGrid[5][2]);
		movesPlayer1.add(moveUnit1);
	}

	private void createTweens() {
		MoveUnitAction moveUnit1 = movesPlayer1.get(0);

		// moveUnit1.origin.getUnit(1).getRender().setAnimation(ANIM.walk);

		Timeline t = Timeline.createSequence();
		t.beginParallel()
				.push(Tween.to(moveUnit1.origin.getUnit(1), ActorAccessor.X, 1)
						.target(moveUnit1.moves.get(0).getX()
								+ Cell.unitPlayer1X))
				.push(Tween.to(moveUnit1.origin.getUnit(1), ActorAccessor.Y, 1)
						.target(moveUnit1.moves.get(0).getY()
								+ Cell.unitPlayer1Y))
				.end();

		for (int i = 0; i + 1 < moveUnit1.moves.size; i++) {
			t.beginParallel()
					.push(Tween.to(moveUnit1.origin.getUnit(1),
							ActorAccessor.X, 1).target(
							moveUnit1.moves.get(i + 1).getX()
									+ Cell.unitPlayer1X))
					.push(Tween.to(moveUnit1.origin.getUnit(1),
							ActorAccessor.Y, 1).target(
							moveUnit1.moves.get(i + 1).getY()
									+ Cell.unitPlayer1Y)).end();

		}
		t.start(tweenManager);
	}

	public void init() {
		GameController.getInstancia().loadUnitsStats();
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		loadDummyData();
	}

	@Override
	public void render(float dt, SpriteBatch batch, Stage stage) {
		tweenManager.update(dt);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {

		createTweens();

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

}
