package pruebas.Renders.helpers.ui;

import java.util.Enumeration;

import pruebas.Controllers.GameController;
import pruebas.CrystalClash.CrystalClash;
import pruebas.Renders.helpers.ResourceHelper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class UnitThumbsList extends Group {
	private Table table;
	private Image imgTableBg;
	private ScrollPane scrollPane;
	private Label lblUnitsCount;
	private UnitThumb selectedThumb;

	public UnitThumbsList(int player, UnitThumbListener unitThumbListener) {
		final UnitThumbListener listener = unitThumbListener;

		lblUnitsCount = new Label("", new LabelStyle(ResourceHelper.getFont(), Color.WHITE));

		imgTableBg = new Image(ResourceHelper.getTexture("in_game/first_turn/list_background"));
		addActor(imgTableBg);

		table = new Table();
		scrollPane = new ScrollPane(table);
		if (player == 1) {
			scrollPane.setPosition(CrystalClash.WIDTH / 2 + 10, 155);
			imgTableBg.setPosition(CrystalClash.WIDTH / 2, 0);
			lblUnitsCount.setPosition(CrystalClash.WIDTH / 2, 0);
		} else {
			scrollPane.setPosition(10, 155);
			imgTableBg.setPosition(0, 0);
			lblUnitsCount.setPosition(0, 0);
		}
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setOverscroll(false, true);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setForceScroll(false, true);
		scrollPane.setSize(623, 685);
		scrollPane.invalidate();
		addActor(scrollPane);

		table.align(Align.top | Align.left);
		table.defaults().width(198).height(252).padLeft(6).padTop(6);
		// List items
		Enumeration<String> unit_names = GameController.getUnitNames();
		String unit_name;
		int i = 0;
		while (unit_names.hasMoreElements()) {
			if (i == 3) {
				table.row();
				i = 0;
			}
			i++;
			unit_name = unit_names.nextElement();
			UnitThumb item = new UnitThumb(unit_name);
			item.addListener((new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					UnitThumb item = (UnitThumb) event.getListenerActor();
					if (item != null) {
						if (item == selectedThumb) {
							item.desselect();
							listener.onClick(item.getUnitName(), false, x, y);
						} else {
							if (selectedThumb != null)
								selectedThumb.desselect();
							item.select();
							selectedThumb = item;
							listener.onClick(item.getUnitName(), true, x, y);
						}
					}
				}
			}));
			table.add(item);
		}

		addActor(lblUnitsCount);
	}

	public void setUnitCountText(String str) {
		lblUnitsCount.setText(str);
	}
}
