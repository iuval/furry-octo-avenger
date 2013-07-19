package com.prototipes;

import java.util.ArrayList;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameScreen extends AbstractScreen {
	float dx = 0f;
	float dy = 0f;
	float yoffset = 0f;

	float selectionRadius = 20;
	Cell selectedCell = null;
	ArrayList<Cell> allowdNeighbours = new ArrayList<Cell>();
	Stack<Cell> path = new Stack<Cell>();

	Sprite grassSprite;

	Unit archer;

	Sprite hexaSprite_normal;

	Sprite hexaSprite_selected;

	float hexaHeight;
	float hexaWidht;
	public static Cell[][] CELLS;

	public static float w;
	public static float h;

	public GameScreen(GridPrototipe game) {
		super(game);
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		Initialize();
		LoadContent();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		for (int i = 0; i < CELLS.length; i++) {
			for (int j = 0; j < CELLS[i].length; j++) {
				CELLS[i][j].dispose();
			}
		}
		archer.dispose();
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		batch.begin();
		try {// Esto hay que sacarlo
			for (int i = 0; i < CELLS.length; i++) {
				for (int j = 0; j < CELLS[i].length; j++) {
					batch.draw(CELLS[i][j], CELLS[i][j].getX(),
							CELLS[i][j].getY());
				}
			}

			for (int i = 0; i < allowdNeighbours.size(); i++) {
				Cell c = allowdNeighbours.get(i);
				batch.draw(hexaSprite_selected, c.getX(), c.getY());
			}

			for (int i = 0; i < path.size(); i++) {
				Cell c = path.get(i);
				batch.draw(hexaSprite_selected, c.getX(), c.getY());
			}
			batch.draw(hexaSprite_normal, selectedCell.getX(),
					selectedCell.getY());
			batch.draw(archer, archer.getX(), archer.getY());
		} finally {
			batch.end();
		}
	}

	protected void LoadContent() {
		// Archer
		Texture archerText = new Texture(Gdx.files.internal("data/archer.png"));
		TextureRegion archerRegion = new TextureRegion(archerText, 0, 0,
				archerText.getWidth(), archerText.getHeight());
		archer = new Unit(archerRegion);
		archer.setPosition(0, 0);
		archer.gridPos = new GridPos(0, 0);
		archer.MaxMovements = 4;

		// Selected Cell
		Texture hexaTexture_selected = new Texture(
				Gdx.files.internal("data/hexa.png"));
		TextureRegion hexaRegion_selected = new TextureRegion(
				hexaTexture_selected, 0, 0, hexaTexture_selected.getWidth(),
				hexaTexture_selected.getHeight());
		hexaSprite_selected = new Sprite(hexaRegion_selected);

		// Normal Cell
		Texture hexaTexture_normal = new Texture(
				Gdx.files.internal("data/hexa2.png"));
		hexaHeight = hexaTexture_normal.getHeight();
		hexaWidht = hexaTexture_normal.getWidth();
		TextureRegion hexaRegion_normal = new TextureRegion(hexaTexture_normal,
				0, 0, hexaWidht, hexaHeight);
		hexaSprite_normal = new Sprite(hexaRegion_normal);

		//
		selectedCell = new Cell(hexaRegion_selected);
		selectedCell.gridPos = new GridPos(0, 0);
		selectedCell.Visible = false;

		/*
		 * x x x o x x x
		 */
		int[][] oddNeighbours = { { 0, -1 }, { -1, 1 }, { 1, 0 }, { 1, 1 },
				{ 0, 1 }, { -1, 0 } };

		/*
		 * x x x o x x x
		 */
		int[][] evenNeighbours = { { -1, -1 }, { 0, -1 }, { 1, 0 }, { 0, 1 },
				{ 1, -1 }, { -1, 0 } };

		dx = (float) ((3f / 4f) * hexaWidht);
		dy = (float) ((Math.sqrt(3f) / 2f) * hexaWidht);
		yoffset = 0f;

		ArrayList<int[]> temp = new ArrayList<int[]>();
		for (int h = 0; h < 6; h++) {
			for (int v = 0; v < 9; v++) {
				Cell c = new Cell(hexaRegion_normal);
				c.Visible = true;
				temp.clear();

				if (v % 2 == 0) {
					yoffset = dy / 2;

					for (int i = 0; i < 6; i++) {
						if (inMap(v + oddNeighbours[i][0], h
								+ oddNeighbours[i][1])) {
							temp.add(new int[] { v + oddNeighbours[i][0],
									h + oddNeighbours[i][1] });
						}
					}
				} else {
					yoffset = 0;

					for (int i = 0; i < 6; i++) {
						if (inMap(v + evenNeighbours[i][0], h
								+ evenNeighbours[i][1])) {
							temp.add(new int[] { v + evenNeighbours[i][0],
									h + evenNeighbours[i][1] });
						}
					}
				}
				c.neigbours = new int[temp.size()][2];
				for (int i = 0; i < temp.size(); i++) {
					c.neigbours[i] = temp.get(i);
				}
				c.setPosition(v * dx,  yoffset + (h * dy));
				c.gridPos = new GridPos(v, h);

				CELLS[v][h] = c;
			}
		}

		path.push(CELLS[0][0]);
		selectedCell.gridPos = CELLS[0][0].gridPos;
		selectedCell.setPosition(CELLS[0][0].getX(), CELLS[0][0].getY());
		selectedCell.Visible = true;
		UpdateAllowedNeighbours();

		Texture grassText = new Texture(
				Gdx.files.internal("data/grass-texture.png"));
		TextureRegion grassRegion = new TextureRegion(grassText, 0, 0, w, h);
		grassSprite = new Sprite(grassRegion);

		// UnitManager.Init();
		// MovesManager.Init();

	}

	public boolean inMap(int gx, int gy) {
		return gx >= 0 && gx < 9 && gy >= 0 && gy < 6;
	}

	private void UpdateAllowedNeighbours() {
		if (path.size() < archer.MaxMovements) {
			Cell top = path.peek();
			for (int i = 0; i < top.neigbours.length; i++) {
				allowdNeighbours
						.add(CELLS[top.neigbours[i][0]][top.neigbours[i][1]]);
			}
		}
	}

	protected void Initialize() {
		CELLS = new Cell[9][6];
	}

}
