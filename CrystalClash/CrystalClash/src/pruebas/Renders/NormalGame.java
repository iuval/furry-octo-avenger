package pruebas.Renders;

import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.Entities.Cell;
import pruebas.Entities.Path;
import pruebas.Entities.Unit;
import pruebas.Entities.helpers.AttackUnitAction;
import pruebas.Entities.helpers.DefendUnitAction;
import pruebas.Entities.helpers.MoveUnitAction;
import pruebas.Entities.helpers.NoneUnitAction;
import pruebas.Entities.helpers.UnitAction;
import pruebas.Entities.helpers.UnitAction.UnitActionType;
import pruebas.Renders.UnitRender.STATE;
import pruebas.Renders.helpers.PathManager;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class NormalGame extends GameRender {
	private Unit selectedUnit;
	private Cell selectedCell;

	private TweenManager tweenManager;

	private UnitActionType actionType;
	private int maxMoves;

	private Array<MoveUnitAction> moveActions;
	private Array<AttackUnitAction> attackActions;
	private UnitAction unitAction;
	private Array<Cell> ghostlyCells;
	private Array<Unit> defensiveUnits;

	private PathManager paths;

	public NormalGame(WorldController world) {
		super(world);
		selectedUnit = null;
		selectedCell = null;

		actionType = UnitActionType.NONE;
		maxMoves = 0;

		moveActions = new Array<MoveUnitAction>();
		ghostlyCells = new Array<Cell>();

		defensiveUnits = new Array<Unit>();

		attackActions = new Array<AttackUnitAction>();

		paths = new PathManager();

		tweenManager = new TweenManager();

		load();
		clearAllChanges();
		GameEngine.hideLoading();
	}

	public void load() {
		GameController.getInstance().loadUnitsStats();
		PathManager.load();

		// lblAttack = new Label("150", new LabelStyle(ResourceHelper.getFont(),
		// Color.WHITE));
		// lblAttack.setPosition(btnAttack.getX() + (btnAttack.getWidth() / 2 -
		// lblAttack.getWidth() / 2), btnAttack.getY() + 3);
		// lblMoves = new Label("5", new LabelStyle(ResourceHelper.getFont(),
		// Color.WHITE));
		// lblMoves.setPosition(btnMove.getX() + (btnMove.getWidth() / 2 -
		// lblMoves.getWidth() / 2), btnMove.getY() + 3);
	}

	public void onAttackAction() {
		setUnitAction(new AttackUnitAction(selectedUnit.isMelee()));
		unitAction.origin = selectedCell;

		world.getRender().fadeOutActionsRing(false, null);
		showAbleToAttackCells();
	}

	public void onDefendAction() {
		setUnitAction(new DefendUnitAction());
		unitAction.origin = selectedCell;
		defensiveUnits.add(selectedUnit);
		selectedUnit.setDefendingPosition(true);

		world.getRender().fadeOutActionsRing(false, null);
	}

	public void onMoveAction() {
		setUnitAction(new MoveUnitAction());
		unitAction.origin = selectedCell;
		((MoveUnitAction) unitAction).moves.add(selectedCell);
		selectedCell.addState(Cell.MOVE_TARGET);

		world.getRender().fadeOutActionsRing(false, null);
		showAbleToMoveCells();
	}

	public void onUndoAction() {
		undoAction();
	}

	private void showAbleToActionCells() {
		switch (actionType) {
		case PLACE:
			break;
		case ATTACK:
			showAbleToAttackCells();
			break;
		case DEFENSE:
			break;
		case MOVE:
			showAbleToMoveCells();
			break;
		case NONE:
			break;
		default:
			break;
		}
	}

	private void showAbleToMoveCells() {
		clearAvailableCells();
		actionType = UnitActionType.MOVE;

		if (((MoveUnitAction) unitAction).moves.size <= maxMoves) {
			Cell top = ((MoveUnitAction) unitAction).moves.peek();
			boolean continueMoving = true;

			if (top.Equals(unitAction.origin))
				continueMoving = true;

			if (continueMoving) {
				int[][] cells = top.neigbours;
				Cell aux = null;
				for (int i = 0; i < top.neigbours.length; i++) {
					aux = world.cellAtByGrid(cells[i][0], cells[i][1]);
					if (!aux.hasState(Cell.MOVE_TARGET) && aux.getUnit() == null)
						aux.addState(Cell.ABLE_TO_MOVE);
				}
			}
		}
	}

	private void showAbleToAttackCells() {
		clearAvailableCells();

		showAbleToAttackCellRecursive(selectedCell, selectedUnit.isMelee(), selectedUnit.getRange(), false);
	}

	// Method that actually "shows" (change state) the cell where units can
	// attack
	private void showAbleToAttackCellRecursive(Cell cell, boolean onlyCellsWithUnit, int range, boolean hide) {
		int[][] cells = cell.neigbours;

		Unit unit;
		Cell neigbourCell = null;
		for (int i = 0; i < cells.length; i++) {
			neigbourCell = world.cellAtByGrid(cells[i][0], cells[i][1]);
			unit = neigbourCell.getUnit();
			if (!neigbourCell.hasState(Cell.MOVE_TARGET | Cell.ATTACK_TARGET_CENTER) &&
					(hide || (onlyCellsWithUnit && (unit == null || !unit.isEnemy())))) {
				neigbourCell.removeState(Cell.ABLE_TO_ATTACK);
			} else if (neigbourCell != selectedCell && (unit == null || unit.isEnemy()) &&
					!neigbourCell.hasState(Cell.MOVE_TARGET | Cell.ATTACK_TARGET_CENTER))
				neigbourCell.addState(Cell.ABLE_TO_ATTACK);

			if (range > 1)
				showAbleToAttackCellRecursive(neigbourCell, onlyCellsWithUnit, range - 1, hide);
		}
	}

	private void clearAvailableCells() {
		switch (actionType) {
		case PLACE:
			break;
		case ATTACK:
			showAbleToAttackCellRecursive(selectedCell, false, selectedUnit.getRange(), true);
			break;
		case DEFENSE:
			break;
		case MOVE:
			Cell cell = null;
			for (int i = 0; i < ((MoveUnitAction) unitAction).moves.size; i++) {
				cell = ((MoveUnitAction) unitAction).moves.get(i);
				int[][] cells = cell.neigbours;
				for (int j = 0; j < cell.neigbours.length; j++) {
					world.cellAtByGrid(cells[j][0], cells[j][1]).removeState(Cell.ABLE_TO_MOVE);
				}
			}
			break;
		case NONE:
			break;
		default:
			break;
		}
	}

	private void clearPathCells(Array<Cell> cells) {
		for (int i = 0; i < cells.size; i++) {
			cells.get(i).removeState(Cell.MOVE_TARGET);
		}
	}

	private void clearSelection() {
		selectedUnit = null;
		selectedCell = null;
		unitAction = null;
		actionType = UnitAction.UnitActionType.NONE;
		world.getRender().fadeOutActionsRing(false, null);
	}

	private void undoAction() {
		world.getRender().undoAction();

		switch (actionType) {
		case ATTACK:
			clearAvailableCells();
			paths.removePath(selectedUnit);
			((AttackUnitAction) unitAction).target.removeState(Cell.ATTACK_TARGET_CENTER);
			attackActions.removeValue((AttackUnitAction) unitAction, false);

			setUnitAction(new NoneUnitAction());
			selectedCell.setAction(unitAction);
			break;
		case DEFENSE:
			selectedUnit.setDefendingPosition(false);

			setUnitAction(new NoneUnitAction());
			selectedCell.setAction(unitAction);
			break;
		case MOVE:
			clearAvailableCells();
			paths.removePath(selectedUnit);
			Array<Cell> moves = ((MoveUnitAction) unitAction).moves;
			clearPathCells(moves);
			if (moves.size > 1)
				popUnitFromPath(moves);

			// TODO: popup
			// lblMoves.setText(maxMoves + "");

			setUnitAction(new NoneUnitAction());
			selectedCell.setAction(unitAction);
			paths.removePath(selectedUnit);
			break;
		case NONE:
			break;
		case PLACE:
			break;
		default:
			break;
		}
	}

	@Override
	public void clearAllChanges() {
		clearSelection();

		moveActions.clear();
		ghostlyCells.clear();
		attackActions.clear();

		for (int i = 0; i < defensiveUnits.size; i++) {
			defensiveUnits.get(i).setDefendingPosition(false);
		}
		defensiveUnits.clear();

		setUnitAction(new NoneUnitAction());
		for (int i = 0; i < world.cellGrid.length; i++) {
			for (int j = 0; j < world.cellGrid[0].length; j++) {
				world.cellGrid[i][j].setAction(unitAction);
				world.cellGrid[i][j].state = Cell.NONE;
			}
		}
	}

	@Override
	public void renderInTheBack(float dt, SpriteBatch batch) {
		paths.render(batch, dt, Path.TYPE.MOVE);
	}

	@Override
	public void renderInTheFront(float dt, SpriteBatch batch) {
		paths.render(batch, dt, Path.TYPE.ATTACK);

		tweenManager.update(dt);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Cell cell = world.cellAt(x, y);
		if (cell != null) {
			switch (actionType) {
			case PLACE:
				break;
			case ATTACK:
				if (cell.hasState(Cell.ABLE_TO_ATTACK)) {
					if (unitAction != null && ((AttackUnitAction) unitAction).target != null)
						((AttackUnitAction) unitAction).target.removeState(Cell.ATTACK_TARGET_CENTER);
					cell.addState(Cell.ATTACK_TARGET_CENTER);
					((AttackUnitAction) unitAction).target = cell;

					Path p = paths.createOrResetPath(selectedUnit, Path.TYPE.ATTACK);
					if (selectedUnit.isMelee()) {
						PathManager.addLine(p,
								selectedCell.getCenterX(),
								selectedCell.getCenterY(),
								cell.getCenterX(),
								cell.getCenterY());
					} else {
						PathManager.addArc(p,
								selectedCell.getCenterX(),
								selectedCell.getCenterY(),
								cell.getCenterX(),
								cell.getCenterY());
					}
				} else {
					saveAttack();
				}
				break;
			case DEFENSE:
				saveDefense();
				break;
			case MOVE:
				if (cell.hasState(Cell.ABLE_TO_MOVE)) {
					clearAvailableCells();
					Array<Cell> moves = ((MoveUnitAction) unitAction).moves;
					Path p = paths.getOrCreatePath(selectedUnit, Path.TYPE.MOVE);

					if (moves.size == 0) {
						PathManager.addLine(p,
								selectedCell.getCenterX(),
								selectedCell.getCenterY(),
								cell.getCenterX(),
								cell.getCenterY());
					} else {
						PathManager.addLine(p,
								moves.get(moves.size - 1).getCenterX(),
								moves.get(moves.size - 1).getCenterY(),
								cell.getCenterX(),
								cell.getCenterY());
					}
					Unit ghost;
					if (moves.size > 1) {
						ghost = popUnitFromPath(moves);
					} else {
						ghost = new Unit(selectedUnit.getName(), world.player);
						ghost.getRender().setState(STATE.ghost);
						ghostlyCells.add(cell);
					}
					cell.setUnit(ghost);
					cell.addState(Cell.MOVE_TARGET);
					cell.removeState(Cell.ABLE_TO_MOVE);

					// TODO: popup
					// lblMoves.setText(maxMoves - moves.size + "");
					moves.add(cell);

					showAbleToMoveCells();
				} else if (cell.hasState(Cell.MOVE_TARGET)) {
					clearAvailableCells();
					Array<Cell> moves = ((MoveUnitAction) unitAction).moves;
					if (moves.size > 0) {
						Path p = paths.createOrResetPath(selectedUnit, Path.TYPE.MOVE);
						p.clear();

						int index = ((MoveUnitAction) unitAction).moves.indexOf(cell, true);
						if (index > 0) {
							ghostlyCells.removeValue(moves.get(moves.size - 1), true);
							ghostlyCells.add(cell);

							cell.setUnit(popUnitFromPath(moves));

							for (int i = moves.size - 1; i > index; i--) {
								moves.get(i).removeState(Cell.MOVE_TARGET);
							}
							moves.truncate(index + 1);

							for (int i = 1; i < moves.size; i++) {
								PathManager.addLine(p,
										moves.get(i - 1).getCenterX(),
										moves.get(i - 1).getCenterY(),
										moves.get(i).getCenterX(),
										moves.get(i).getCenterY());
							}
						} else {
							if (moves.size > 1) {
								popUnitFromPath(moves);
								for (int i = 1; i < moves.size; i++) {
									moves.get(i).removeState(Cell.MOVE_TARGET);
								}
							}
							moves.truncate(1);
						}

						// lblMoves.setText(maxMoves + 1 - ((MoveUnitAction)
						// unitAction).moves.size + "");
						showAbleToMoveCells();
					}
				} else {
					saveMove();
				}
				break;
			case NONE:
				clearSelection();
				Unit u = cell.getUnit();
				if (u != null && u.getRender().getState() != STATE.ghost) {
					if (selectedUnit != u) {
						selectedUnit = u;
						selectedCell = cell;

						// TODO: popup
						// lblAttack.setText(GameController.getInstance().getUnitAttack(selectedUnit.getName())
						// + "");
						maxMoves = GameController.getInstance().getUnitSpeed(selectedUnit.getName());
						// lblMoves.setText(maxMoves + "");

						if (u.isEnemy()) {
							world.getRender().fadeOutActionsRing(false, null);
						} else {
							if (cell.getAction() != null && cell.getAction().getActionType() != UnitActionType.NONE) {
								setUnitAction(cell.getAction());
								showAbleToActionCells();
							}
							world.getRender().moveActionsRing(selectedCell);
						}
					}
				} else {
					clearSelection();
				}
				break;
			default:
				break;
			}
		}
		return true;
	}

	private Unit popUnitFromPath(Array<Cell> moves) {
		Unit ghost = moves.get(moves.size - 1).getUnit();
		moves.get(moves.size - 1).removeUnit();
		return ghost;
	}

	private void setUnitAction(UnitAction act) {
		unitAction = act;
		actionType = unitAction.getActionType();
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Timeline pushEnterAnimation(Timeline t) {
		return t;
	}

	@Override
	public Timeline pushExitAnimation(Timeline t) {
		return t;
	}

	@Override
	public boolean canSend() {
		return selectedUnit == null;
	}

	private void saveAttack() {
		clearAvailableCells();
		if (((AttackUnitAction) unitAction).target != null) {
			attackActions.add((AttackUnitAction) unitAction);
		} else {
			setUnitAction(new NoneUnitAction());
		}
		selectedCell.setAction(unitAction);
		clearSelection();
	}

	private void saveDefense() {
		selectedCell.setAction(unitAction);
		clearSelection();
		actionType = UnitActionType.NONE;
	}

	private void saveMove() {
		clearAvailableCells();

		MoveUnitAction action = (MoveUnitAction) unitAction;
		if (action.moves.size > 1) {
			moveActions.add(action);
		} else {
			undoAction();
		}
		selectedCell.setAction(unitAction);

		clearSelection();
	}

	@Override
	public void onSend() {
		switch (actionType) {
		case ATTACK:
			saveAttack();
			break;
		case DEFENSE:
			saveDefense();
			break;
		case MOVE:
			saveMove();
			break;
		}
	}

	public void pause() {
	}

	@Override
	public void resume() {
	}
}
