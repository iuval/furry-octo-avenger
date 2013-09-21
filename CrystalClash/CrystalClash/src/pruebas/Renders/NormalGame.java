package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.Controllers.GameController;
import pruebas.Controllers.WorldController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Entities.Cell;
import pruebas.Entities.Cell.State;
import pruebas.Entities.GridPos;
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
import pruebas.Renders.helpers.ResourceHelper;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class NormalGame extends GameRender {
	private TweenManager tweenManager;

	private Unit selectedUnit;
	private Cell selectedCell;

	private Image selectorArrow;
	private float arrowX;
	private float arrowY;

	private Image actionsBar;
	private TextButton btnAttack;
	private TextButton btnMove;
	private TextButton btnDefense;
	private TextButton btnUndo;
	private Group grpActionBar;
	private Label lblMoves;
	private Label lblAttack;
	private boolean actionsBarVisible;
	private boolean undoVisible;

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

		tweenManager = new TweenManager();

		selectedUnit = null;
		selectedCell = null;

		arrowX = 0;
		arrowY = CrystalClash.HEIGHT + 20;
		undoVisible = false;
		actionsBarVisible = false;

		actionType = UnitActionType.NONE;
		maxMoves = 0;

		moveActions = new Array<MoveUnitAction>();
		ghostlyCells = new Array<Cell>();

		defensiveUnits = new Array<Unit>();

		attackActions = new Array<AttackUnitAction>();

		paths = new PathManager();

		load();
		clearAllChanges();
		GameEngine.hideLoading();
	}

	public void load() {
		GameController.getInstance().loadUnitsStats();

		PathManager.load();

		Texture arrow = ResourceHelper.getTexture("data/Images/InGame/selector_arrow.png");
		selectorArrow = new Image(arrow);
		selectorArrow.setPosition(arrowX, arrowY);

		TextureAtlas atlas = new TextureAtlas("data/Images/InGame/options_bar.pack");
		Skin skin = new Skin(atlas);

		TextureRegion aux = skin.getRegion("actions_bar");
		actionsBar = new Image(aux);

		TextButtonStyle attackStyle = new TextButtonStyle(
				skin.getDrawable("action_attack_button"),
				skin.getDrawable("action_attack_button_pressed"), null, ResourceHelper.getFont());
		btnAttack = new TextButton("", attackStyle);
		btnAttack.setPosition(actionsBar.getX() + 15, actionsBar.getY() - 20);
		btnAttack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnUndo.setPosition(btnAttack.getX(), btnDefense.getY());
				undoVisible = true;
				updateActionsBar();
				setUnitAction(new AttackUnitAction(selectedUnit.isMelee()));
				unitAction.origin = selectedCell;

				showAbleToAttackCells();
			}
		});

		TextButtonStyle defenseStyle = new TextButtonStyle(
				skin.getDrawable("action_defensive_button"),
				skin.getDrawable("action_defensive_button_pressed"), null, ResourceHelper.getFont());
		btnDefense = new TextButton("", defenseStyle);
		btnDefense.setPosition(btnAttack.getX() + btnAttack.getWidth() + 15,
				actionsBar.getY());
		btnDefense.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnUndo.setPosition(btnDefense.getX(), btnDefense.getY());
				undoVisible = true;
				updateActionsBar();
				setUnitAction(new DefendUnitAction());
				unitAction.origin = selectedCell;
				defensiveUnits.add(selectedUnit);
				selectedUnit.setDefendingPosition(true);

				showTargetCells(false);
			}
		});

		TextButtonStyle moveStyle = new TextButtonStyle(
				skin.getDrawable("action_run_button"),
				skin.getDrawable("action_run_button_pressed"), null, ResourceHelper.getFont());
		btnMove = new TextButton("", moveStyle);
		btnMove.setPosition(btnDefense.getX() + btnDefense.getWidth() + 15,
				actionsBar.getY() - 20);
		btnMove.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				btnUndo.setPosition(btnMove.getX(), btnDefense.getY());
				undoVisible = true;
				updateActionsBar();

				setUnitAction(new MoveUnitAction());
				unitAction.origin = selectedCell;
				((MoveUnitAction) unitAction).moves.add(selectedCell);

				showAbleToMoveCells();
				showTargetCells(true);
			}
		});

		TextButtonStyle undoStyle = new TextButtonStyle(
				skin.getDrawable("action_cancel_button"),
				skin.getDrawable("action_cancel_button_pressed"), null, ResourceHelper.getFont());
		btnUndo = new TextButton("", undoStyle);
		btnUndo.setPosition(0, 300); // Afuera de la ventana
		btnUndo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				undoAction();
				undoVisible = false;
				updateActionsBar();
			}
		});

		lblAttack = new Label("150", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblAttack.setPosition(btnAttack.getX()
				+ (btnAttack.getWidth() / 2 - lblAttack.getWidth() / 2),
				btnAttack.getY() + 3);

		lblMoves = new Label("5", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));
		lblMoves.setPosition(btnMove.getX()
				+ (btnMove.getWidth() / 2 - lblMoves.getWidth() / 2),
				btnMove.getY() + 3);

		grpActionBar = new Group();
		grpActionBar.addActor(actionsBar);
		grpActionBar.addActor(btnAttack);
		grpActionBar.addActor(lblAttack);
		grpActionBar.addActor(btnMove);
		grpActionBar.addActor(lblMoves);
		grpActionBar.addActor(btnDefense);

		grpActionBar.setSize(actionsBar.getWidth(), actionsBar.getHeight());
		grpActionBar.setPosition(
				CrystalClash.WIDTH / 2 - grpActionBar.getWidth() / 2,
				CrystalClash.HEIGHT + 50);

		addActor(grpActionBar);
	}

	private void moveArrow(Unit u) {
		if (u != null) {
			System.out.println(arrowY);
			if (selectorArrow.getY() >= CrystalClash.HEIGHT) {
				selectorArrow.setPosition(u.getX(), CrystalClash.HEIGHT + 20);
			}
			arrowX = u.getX();
			arrowY = u.getY() + 120;
		} else {
			arrowY = CrystalClash.HEIGHT + 20;
		}

		tweenManager.killAll();
		Timeline.createParallel()
				.push(Tween.to(selectorArrow, ActorAccessor.X, CrystalClash.ANIMATION_SPEED)
						.target(arrowX))
				.push(Tween.to(selectorArrow, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED)
						.target(arrowY))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						selectorArrow.setPosition(arrowX, arrowY);
						arrowAnimation();
					}
				}).start(tweenManager);
	}

	private void arrowAnimation() {
		Timeline.createSequence()
				.push(Tween.set(selectorArrow, ActorAccessor.Y).target(arrowY))
				.push(Tween.to(selectorArrow, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(
						arrowY - 10))
				.push(Tween.to(selectorArrow, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(
						arrowY)).repeat(Tween.INFINITY, 0).start(tweenManager);
	}

	private void showActionsBar() {
		Timeline t = Timeline.createSequence();
		if (450 < selectedUnit.getX() && selectedUnit.getX() < 825) {
			if (actionsBarVisible) {
				t.push(Tween.to(grpActionBar, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(
						CrystalClash.WIDTH / 4 - grpActionBar.getWidth() / 2));
			} else {
				grpActionBar.setX(CrystalClash.WIDTH / 4
						- grpActionBar.getWidth() / 2);
				t.push(Tween.to(grpActionBar, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(
						CrystalClash.HEIGHT - grpActionBar.getHeight()));
			}
		} else {
			if (actionsBarVisible) {
				t.push(Tween.to(grpActionBar, ActorAccessor.X, CrystalClash.ANIMATION_SPEED).target(
						CrystalClash.WIDTH / 2 - grpActionBar.getWidth() / 2));
			} else {
				grpActionBar.setX(CrystalClash.WIDTH / 2
						- grpActionBar.getWidth() / 2);
				t.push(Tween.to(grpActionBar, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(
						CrystalClash.HEIGHT - grpActionBar.getHeight()));
			}
		}
		t.start(tweenManager);
		actionsBarVisible = true;
	}

	private void hideActionsBar() {
		actionsBarVisible = false;
		pushHideActionBar(Timeline.createSequence()).start(tweenManager);
	}

	private Timeline pushHideActionBar(Timeline t) {
		return t.push(Tween.to(grpActionBar, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED).target(
				CrystalClash.HEIGHT + 50));
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
					if (aux.getState() == State.NONE && aux.getUnit() == null)
						aux.setState(Cell.State.ABLE_TO_MOVE);
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
			if ((neigbourCell.getState() != State.MOVE_TARGET && neigbourCell.getState() != State.ATTACK_TARGET_CENTER) &&
					(hide || (onlyCellsWithUnit && (unit == null || !unit.isEnemy())))) {
				neigbourCell.setState(Cell.State.NONE);
			} else if (neigbourCell.getState() != State.MOVE_TARGET && neigbourCell.getState() != State.ATTACK_TARGET_CENTER)
				neigbourCell.setState(Cell.State.ABLE_TO_ATTACK);

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
			Cell top = null;
			for (int i = 0; i < ((MoveUnitAction) unitAction).moves.size; i++) {
				top = ((MoveUnitAction) unitAction).moves.get(i);
				int[][] cells = top.neigbours;
				for (int j = 0; j < top.neigbours.length; j++) {
					if (world.cellGrid[cells[j][0]][cells[j][1]].getState() == State.ABLE_TO_MOVE)
						world.setCellStateByGridPos(cells[j][0], cells[j][1],
								Cell.State.NONE);
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
			cells.get(i).setState(State.NONE);
		}
	}

	private void clearSelection() {
		selectedUnit = null;
		selectedCell = null;
		unitAction = null;
		actionType = UnitAction.UnitActionType.NONE;
		moveArrow(selectedUnit);
		hideActionsBar();
	}

	private void showTargetCells(boolean stillAssigning) {
		GridPos g = null;
		switch (unitAction.getActionType()) {
		case ATTACK:
			btnUndo.setPosition(btnAttack.getX(), btnDefense.getY());

			if (((AttackUnitAction) unitAction).target != null) {
				g = ((AttackUnitAction) unitAction).target.getGridPosition();
				world.setCellStateByGridPos(g.getX(), g.getY(), Cell.State.ATTACK_TARGET_CENTER);
			}
			break;
		case DEFENSE:
			actionType = UnitActionType.DEFENSE;
			btnUndo.setPosition(btnDefense.getX(), btnDefense.getY());
			break;
		case MOVE:
			actionType = UnitActionType.MOVE;
			btnUndo.setPosition(btnMove.getX(), btnDefense.getY());

			for (int i = 0; i < ((MoveUnitAction) unitAction).moves.size; i++) {
				g = ((MoveUnitAction) unitAction).moves.get(i).getGridPosition();
				world.setCellStateByGridPos(g.getX(), g.getY(), Cell.State.MOVE_TARGET);
			}

			lblMoves.setText(maxMoves + 1 - ((MoveUnitAction) unitAction).moves.size + "");
			undoVisible = true;
			break;
		case NONE:
			undoVisible = false;
			actionType = UnitActionType.NONE;
			break;
		case PLACE:
			undoVisible = false;
			actionType = UnitActionType.NONE;
			break;
		default:
			break;
		}
	}

	private void hideAction(UnitAction action) {
		GridPos g = null;
		switch (action.getActionType()) {
		case ATTACK:
			if (((AttackUnitAction) action).target != null) {
				g = ((AttackUnitAction) action).target.getGridPosition();
				world.setCellStateByGridPos(g.getX(), g.getY(), Cell.State.NONE);
			}
			break;
		case DEFENSE:
			break;
		case MOVE:
			for (int i = 0; i < ((MoveUnitAction) action).moves.size; i++) {
				g = ((MoveUnitAction) action).moves.get(i).getGridPosition();
				world.setCellStateByGridPos(g.getX(), g.getY(), Cell.State.NONE);
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

	private void undoAction() {
		switch (actionType) {
		case ATTACK:
			clearAvailableCells();
			hideAction(unitAction);
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
			Array<Cell> moves = ((MoveUnitAction) unitAction).moves;
			clearPathCells(moves);
			if (moves.size > 1)
				popUnitFromPath(moves);

			lblMoves.setText(maxMoves + "");

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

	private void updateActionsBar() {
		if (undoVisible) {
			grpActionBar.removeActor(btnAttack);
			grpActionBar.removeActor(lblAttack);
			grpActionBar.removeActor(btnMove);
			grpActionBar.removeActor(lblMoves);
			grpActionBar.removeActor(btnDefense);
			grpActionBar.addActor(btnUndo);
		} else {
			grpActionBar.addActor(btnAttack);
			grpActionBar.addActor(lblAttack);
			grpActionBar.addActor(btnMove);
			grpActionBar.addActor(lblMoves);
			grpActionBar.addActor(btnDefense);
			grpActionBar.removeActor(btnUndo);
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
				world.cellGrid[i][j].setState(Cell.State.NONE);
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
		selectorArrow.draw(batch, 1);

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
				if (cell.getState() == Cell.State.ABLE_TO_ATTACK) {
					if (unitAction != null && ((AttackUnitAction) unitAction).target != null)
						((AttackUnitAction) unitAction).target.setState(State.ABLE_TO_ATTACK);
					cell.setState(Cell.State.ATTACK_TARGET_CENTER);
					((AttackUnitAction) unitAction).target = cell;

					Path p = paths.createOrResetPath(selectedUnit, Path.TYPE.ATTACK);
					PathManager.addArc(p,
							selectedCell.getCenterX(),
							selectedCell.getCenterY(),
							cell.getCenterX(),
							cell.getCenterY());
				} else {
					saveAttack();
				}
				break;
			case DEFENSE:
				saveDefense();
				break;
			case MOVE:
				if (cell.getState() == Cell.State.ABLE_TO_MOVE) {
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
					cell.setState(State.MOVE_TARGET);

					lblMoves.setText(maxMoves - moves.size + "");
					moves.add(cell);

					showAbleToMoveCells();
				} else if (cell.getState() == Cell.State.MOVE_TARGET) {
					clearAvailableCells();
					Array<Cell> moves = ((MoveUnitAction) unitAction).moves;
					if (moves.size > 0) {
						clearPathCells(moves);

						Path p = paths.createOrResetPath(selectedUnit, Path.TYPE.MOVE);
						p.clear();

						int index = ((MoveUnitAction) unitAction).moves.indexOf(cell, true);
						if (index > 0) {
							ghostlyCells.removeValue(moves.get(moves.size - 1), true);
							ghostlyCells.add(cell);

							cell.setUnit(popUnitFromPath(moves));

							moves.truncate(index + 1);

							for (int i = 1; i < moves.size; i++) {
								PathManager.addLine(p,
										moves.get(i - 1).getCenterX(),
										moves.get(i - 1).getCenterY(),
										moves.get(i).getCenterX(),
										moves.get(i).getCenterY());
							}
						} else {
							if (moves.size > 1)
								popUnitFromPath(moves);
							moves.truncate(1);
						}

						lblMoves.setText(maxMoves + 1 - ((MoveUnitAction) unitAction).moves.size + "");

						showTargetCells(true);
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

						lblAttack.setText(GameController.getInstance().getUnitAttack(selectedUnit.getName()) + "");
						maxMoves = GameController.getInstance().getUnitSpeed(selectedUnit.getName());
						lblMoves.setText(maxMoves + "");

						moveArrow(selectedUnit);

						if (u.isEnemy()) {
							hideActionsBar();
						} else {
							if (cell.getAction() != null) {
								setUnitAction(cell.getAction());
								showTargetCells(true);
								showAbleToActionCells();
							}

							updateActionsBar();
							showActionsBar();
						}
					}
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
		arrowY = CrystalClash.HEIGHT + 20;
		tweenManager.killAll();
		return pushHideActionBar(t)
				.push(Tween.to(grpActionBar, ActorAccessor.X, CrystalClash.ANIMATION_SPEED)
						.target(-grpActionBar.getHeight()))
				.push(Tween.to(selectorArrow, ActorAccessor.Y, CrystalClash.ANIMATION_SPEED)
						.target(arrowY));
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
			setUnitAction(new NoneUnitAction());
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
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}
