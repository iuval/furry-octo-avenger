package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Accessors.UnitAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.Entities.Cell;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.MoveUnitAction;
import pruebas.Entities.helpers.UnitAction;
import pruebas.Renders.UnitRender.ANIM;
import pruebas.Renders.helpers.CellHelper;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class TurnAnimations extends GameRender {


	private TweenManager tweenManager;

	private Array<MoveUnitAction> player1Moves;
	private Array<MoveUnitAction> player2Moves;

	public TurnAnimations(WorldController world) {
		super(world);

		tweenManager = new TweenManager();

		init();
	}

	private void moveUnits() {
		Timeline t = Timeline.createSequence();
		t.beginParallel();
		createPaths(player1Moves, 1, t);
		t.end();
		t.beginParallel();
		createPaths(player2Moves, 2, t);
		t.end();
		t.start(tweenManager);
	}

	private void createPaths(Array<MoveUnitAction> moveActions, int player,
			Timeline pathsTimeline) {
		MoveUnitAction action = null;
		for (int m = 0; m < moveActions.size; m++) {
			action = moveActions.get(m);
			
			Timeline path = Timeline.createSequence();
			path.setUserData(new Object[] { action.moves.size, action.origin.getUnit(player) });
			path.setCallbackTriggers(TweenCallback.BEGIN);
			path.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					int stepsCount = (Integer) (((Object[]) source.getUserData())[0]);
					Unit unit = (Unit) (((Object[]) source.getUserData())[1]);

					if(stepsCount > 1)
					{
						unit.getRender().setAnimation(ANIM.walk);
					}
				}
			});
			
			
			for (int i = 0; i + 1 < action.moves.size; i++) {
				Timeline step = createStep(action, i, action.moves.size, player);
				path.push(step);
			}
			pathsTimeline.push(path);
		}
	}

	private Timeline createStep(MoveUnitAction action, int currentStepIndex, int stepsCount,
			int player) {
		Timeline step = Timeline.createParallel();
		boolean isLastStep = ((currentStepIndex + 1) == (stepsCount - 1)) || (stepsCount == 1);
		
		step.setUserData(new Object[] { action.moves.get(currentStepIndex),
				action.moves.get(currentStepIndex + 1), player, isLastStep });
		step.setCallbackTriggers(TweenCallback.COMPLETE);
		step.setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				repositionUnit(source);
			}
		});
		step.push(Tween
				.to(action.origin.getUnit(player), ActorAccessor.X, 1)
				.target(action.moves.get(currentStepIndex + 1).getX() + (player == 1 ? CellHelper.UNIT_PLAYER_1_X
						: CellHelper.UNIT_PLAYER_2_X)));
		step.push(Tween
				.to(action.origin.getUnit(player), ActorAccessor.Y, 1)
				.target(action.moves.get(currentStepIndex + 1).getY() + (player == 1 ? CellHelper.UNIT_PLAYER_1_Y
						: CellHelper.UNIT_PLAYER_2_Y)));
		return step;
	}

	private void repositionUnit(BaseTween<?> source) {
		Cell cellFrom = (Cell) (((Object[]) source.getUserData())[0]);
		Cell cellTo = (Cell) (((Object[]) source.getUserData())[1]);
		int player = (Integer) (((Object[]) source.getUserData())[2]);
		boolean isLastStep = (Boolean) (((Object[]) source.getUserData())[3]);
		Unit unit = cellFrom.getUnit(player);

		cellFrom.removeUnit(player);
		cellTo.setUnit(unit, player);
		
		if(isLastStep)
		{
			unit.getRender().setAnimation(ANIM.idle);
		}

		String coordsFrom = cellFrom.getGridPosition().getX() + ","
				+ cellFrom.getGridPosition().getY();
		String coordsTo = cellTo.getGridPosition().getX() + ","
				+ cellTo.getGridPosition().getY();
		System.out.println("step:[" + coordsFrom + "]-->[" + coordsTo + "]");
	}

	private void readActions(int player) {
		for (int row = 0; row < world.cellGrid.length; row++) {
			for (int col = 0; col < world.cellGrid[0].length; col++) {

				UnitAction action = world.cellGrid[row][col].getAction(player);

				if (action != null) {
					switch (action.getActionType()) {
					case ATTACK:
						break;
					case DEFENSE:
						break;
					case MOVE:
						if (player == 1) {
							player1Moves.add((MoveUnitAction) action);
						} else {
							player2Moves.add((MoveUnitAction) action);
						}
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

		player1Moves = new Array<MoveUnitAction>();
		player2Moves = new Array<MoveUnitAction>();

		readActions(1);
		readActions(2);
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
