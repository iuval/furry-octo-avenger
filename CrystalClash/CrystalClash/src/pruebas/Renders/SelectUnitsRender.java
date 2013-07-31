package pruebas.Renders;

import pruebas.Accessors.ActorAccessor;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Renders.shared.UnitRender;
import pruebas.Renders.shared.UnitTableItem;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class SelectUnitsRender {
	private GameEngine engine;
	private DragAndDrop dragAndDrop;
	private TweenManager tweenManager;
	private Group unitTableGroup;
	private Skin listItemSkin;
	private Table table;
	private ScrollPane scrollPane;

	private UnitRender selectedUnit;

	public SelectUnitsRender(GameEngine e) {
		engine = e;
		tweenManager = new TweenManager();
		load();
	}

	public void load() {
		unitTableGroup = new VerticalGroup();
		unitTableGroup.setBounds(0, 0, CrystalClash.WIDTH, CrystalClash.HEIGHT);

		Texture popupPanelTexture = new Texture(
				Gdx.files.internal("data/Images/Menu/menu_login_popup.png"));
		Image popupPanel = new Image(popupPanelTexture);
		popupPanel.setBounds(0, 0, CrystalClash.WIDTH / 2, CrystalClash.HEIGHT);
		unitTableGroup.addActor(popupPanel);

		table = new Table();
		table.align(Align.top).padBottom(20).padTop(20);

		// put the table inside a scrollpane
		scrollPane = new ScrollPane(table);
		// unitTableGroup.addActor(scrollPane);
		scrollPane.setBounds(CrystalClash.WIDTH / 2, 0, CrystalClash.WIDTH / 2,
				CrystalClash.HEIGHT);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, true);
		scrollPane.setSmoothScrolling(true);
		scrollPane.invalidate();

		TextureAtlas listItemButtonAtlas = new TextureAtlas(
				"data/Units/units_icons.pack");
		listItemSkin = new Skin(listItemButtonAtlas);
		listItemSkin
				.add("font",
						new BitmapFont(Gdx.files
								.internal("data/Fonts/font.fnt"), false));
		listItemSkin
				.add("background",
						new Texture(
								Gdx.files
										.internal("data/Images/Menu/games_item_background.png")));

		InputListener input = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				String unitName = ((UnitTableItem) event.getListenerActor()).unitName;
				selectedUnit = UnitRender.getUnitRender(unitName);
				selectedUnit.setZIndex(50);
				selectedUnit.setPosition(event.getStageX() - UnitRender.WIDTH
						/ 2, event.getStageY() - UnitRender.HEIGHT / 2);
				selectedUnit.addListener((new DragListener() {
					public void touchDragged(InputEvent event, float x,
							float y, int pointer) {
						// example code below for origin and position
						selectedUnit.setOrigin(Gdx.input.getX(),
								Gdx.input.getY());
						selectedUnit.setPosition(x, y);
						System.out.println("touchdragged" + x + ", " + y);
						scrollPane.setVisible(false);
					}

				}));

				return true;
			}
		};
		for (int i = 0; i < 20; i++) {
			UnitTableItem item = new UnitTableItem("fire_archer", listItemSkin);
			item.addListener(input);
			table.row();
			table.add(item);
		}
		// enterAnimation();
	}

	public void enterAnimation() {
		float speed = CrystalClash.ANIMATION_SPEED;
		Timeline.createParallel()
				.push(Tween.to(unitTableGroup, ActorAccessor.Y, speed).target(
						-CrystalClash.HEIGHT / 2)).start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
	}

	public void render(float dt, SpriteBatch batch, Stage stage) {
		stage.addActor(scrollPane);
		scrollPane.act(dt);
		// stage.addActor(scrollPane);
		// tweenManager.update(dt);
		if (selectedUnit != null) {
			stage.addActor(selectedUnit);
			selectedUnit.act(dt);
		}
	}

	private void setUpDragAndDrop() {
		dragAndDrop = new DragAndDrop();
		dragAndDrop.addSource(new Source(selectedUnit) {
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				Payload payload = new Payload();
				payload.setDragActor(selectedUnit);
				return payload;
			}
		});

	}
}
