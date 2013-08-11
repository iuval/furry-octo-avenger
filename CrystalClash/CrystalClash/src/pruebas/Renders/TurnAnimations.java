package pruebas.Renders;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import pruebas.Accessors.ActorAccessor;
import pruebas.Accessors.UnitAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.MoveUnitAction;
import pruebas.Entities.helpers.UnitAction;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class TurnAnimations extends GameRender {

	private TweenManager tweenManager;

	private Array<MoveUnitAction> alliedMoves;
	private Array<MoveUnitAction> enemyMoves;

	public TurnAnimations(WorldController world) {
		super(world);

		tweenManager = new TweenManager();

		init();
	}

	// private void loadDummyData() {
	// Unit testUnit1 = new Unit("fire_archer");
	// if (world.player == 2)
	// testUnit1.getRender().setFacing(FACING.left);
	//
	// world.cellGrid[5][2].placeUnit(testUnit1, world.player);
	//
	// // -------------------------------------------------------------------
	//
	// movesPlayer1 = new ArrayList<MoveUnitAction>();
	//
	// MoveUnitAction moveUnit1 = new MoveUnitAction();
	// moveUnit1.origin = world.cellGrid[5][2];
	// moveUnit1.moves.add(world.cellGrid[5][3]);
	// moveUnit1.moves.add(world.cellGrid[6][2]);
	// moveUnit1.moves.add(world.cellGrid[6][1]);
	// moveUnit1.moves.add(world.cellGrid[5][1]);
	// moveUnit1.moves.add(world.cellGrid[4][1]);
	// moveUnit1.moves.add(world.cellGrid[4][2]);
	// moveUnit1.moves.add(world.cellGrid[5][3]);
	// moveUnit1.moves.add(world.cellGrid[5][2]);
	// movesPlayer1.add(moveUnit1);
	// }

	private void moveUnits() {
		Timeline t = Timeline.createSequence();
		t.beginParallel();
		
		MoveUnitAction action = null;
		for (int m = 0; m < alliedMoves.size; m++) {
			action = alliedMoves.get(m);
			t.beginSequence();
			for (int i = 0; i + 1 < action.moves.size; i++) {
				t.beginParallel()
						.push(Tween.to(action.origin.getUnit(1),
								ActorAccessor.X, 1).target(
								action.moves.get(i + 1).getX()
										+ Cell.unitPlayer1X))
						.push(Tween.to(action.origin.getUnit(1),
								ActorAccessor.Y, 1).target(
								action.moves.get(i + 1).getY()
										+ Cell.unitPlayer1Y)).end();

			}
			t.end();
		}
		t.end();
		
		t.beginParallel();
		for (int m = 0; m < enemyMoves.size; m++) {
			action = enemyMoves.get(m);
			t.beginSequence();
			for (int i = 0; i + 1 < action.moves.size; i++) {
				t.beginParallel()
						.push(Tween.to(action.origin.getUnit(2),
								ActorAccessor.X, 1).target(
								action.moves.get(i + 1).getX()
										+ Cell.unitPlayer1X))
						.push(Tween.to(action.origin.getUnit(2),
								ActorAccessor.Y, 1).target(
								action.moves.get(i + 1).getY()
										+ Cell.unitPlayer1Y)).end();

			}
			t.end();
		}
		t.end();
		
		t.start(tweenManager);
	}

	private void readActions() {
		for (int row = 0; row < world.cellGrid.length; row++) {
			for (int col = 0; col < world.cellGrid[0].length; col++) {

				UnitAction alliedAction = world.cellGrid[row][col].getAction(1);

				if (alliedAction != null) {
					switch (alliedAction.getActionType()) {
					case ATTACK:
						break;
					case DEFENSE:
						break;
					case MOVE:
						alliedMoves.add((MoveUnitAction) alliedAction);
						break;
					case NONE:
						break;
					case PLACE:
						break;
					default:
						break;
					}
				}

				UnitAction enemyAction = world.cellGrid[row][col].getAction(2);

				if (enemyAction != null) {
					switch (enemyAction.getActionType()) {
					case ATTACK:
						break;
					case DEFENSE:
						break;
					case MOVE:
						enemyMoves.add((MoveUnitAction) enemyAction);
						break;
					case NONE:
						break;
					case PLACE:
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public void init() {
		GameController.getInstancia().loadUnitsStats();
		Tween.registerAccessor(Unit.class, new UnitAccessor());

		alliedMoves = new Array<MoveUnitAction>();
		enemyMoves = new Array<MoveUnitAction>();
		
		readActions();
	}

	@Override
	public void render(float dt, SpriteBatch batch, Stage stage) {
		tweenManager.update(dt);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {

		moveUnits();

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
